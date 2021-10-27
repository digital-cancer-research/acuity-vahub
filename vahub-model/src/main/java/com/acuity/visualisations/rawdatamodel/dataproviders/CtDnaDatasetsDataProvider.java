/*
 * Copyright 2021 The University of Manchester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.acuity.visualisations.rawdatamodel.dataproviders;

import com.acuity.visualisations.rawdatamodel.dataproviders.common.SubjectAwareDatasetsRegularDataProvider;
import com.acuity.visualisations.rawdatamodel.vo.CtDnaRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.CtDna;
import com.acuity.va.security.acl.domain.Dataset;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static com.acuity.visualisations.rawdatamodel.util.Constants.YES;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.CtDna.NO_MUTATIONS_DETECTED;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.CtDna.NO_MUTATIONS_DETECTED_VALUE;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.CtDna.NO_MUTATIONS_DETECTED_VALUE_IN_PERCENT;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Component
public class CtDnaDatasetsDataProvider extends SubjectAwareDatasetsRegularDataProvider<CtDnaRaw, CtDna> {

    private static final Set<String> NO_MUTATIONS_DETECTED_SYNONYMS = newHashSet(
            "no reported mutation",
            "no reported mutations",
            "no mutation detected",
            "no mutations detected",
            "no mutation",
            "no mutations",
            "none");

    @Override
    protected CtDna getWrapperInstance(CtDnaRaw event, Subject subject) {
        return new CtDna(event, subject);
    }

    @Override
    protected Class<CtDnaRaw> rawDataClass() {
        return CtDnaRaw.class;
    }

    @Override
    protected Collection<CtDnaRaw> getData(Dataset dataset) {
        return dataProvider.getData(CtDnaRaw.class, dataset, ds -> {
            Collection<CtDnaRaw> ctDna = rawDataRepository.getRawData(ds.getId());
            final List<CtDnaRaw> withCaseNormalized = ctDna.stream().map(this::withTrackedMutationCaseNormalized)
                    .collect(toList());
            return withNoReportedMutationsAdded(withCaseNormalized);
        });
    }

    private CtDnaRaw withTrackedMutationCaseNormalized(CtDnaRaw e) {
        if (YES.equalsIgnoreCase(e.getTrackedMutation())) {
            e = e.toBuilder().trackedMutation(YES).build();
        }
        String lowerCaseMutation = Optional.ofNullable(e.getMutation()).map(String::toLowerCase).orElse(null);
        if (NO_MUTATIONS_DETECTED_SYNONYMS.contains(lowerCaseMutation)) {
            e = e.toBuilder().mutation(NO_MUTATIONS_DETECTED).build();
        }
        return e;
    }

    /**
     * Method that handles adding events if no mutations were detected per subject+gene+mutation combination
     * on subject's sample date. The algorithm is as follows
     * 1) All events are grouped by subject.
     * 2) All subject's events are analysed, and list of sample dates is extracted.
     * 3) For each subject+gene+mutation combination it is checked whether there is an event on each sample date
     * from the list formed in p.1.
     * 4) If on some sample date the event is missing,
     * it is generated with value of VAF (Variant Allele Frequency) equal to the threshold value (0.002).
     * 5) If on some sample date the event's VAF is less then the threshold value or empty,
     * it is rounded up to the threshold value.
     * @param data - input collection of CtDnaRaw
     * @return collection of CtDnaRaw with added generated events for 'no mutations detected' case
     */
    private Collection<CtDnaRaw> withNoReportedMutationsAdded(Collection<CtDnaRaw> data) {

        Map<String, List<CtDnaRaw>> ctdnaBySubject = data.stream().collect(groupingBy(CtDnaRaw::getSubjectId));

        return ctdnaBySubject.values().stream()
                .map(this::withNoReportedMutationsAddedPerSubject)
                .flatMap(Collection::stream)
                .collect(toList());
    }

    private Collection<CtDnaRaw> withNoReportedMutationsAddedPerSubject(Collection<CtDnaRaw> ctDnaPerSubject) {

        Map<Date, Double> visitBySampleDate = ctDnaPerSubject.stream()
                // cannot use toMap collector, because merge function throws NPE if null visitNumber occurs
                .collect(HashMap::new, (m, v) -> m.put(v.getSampleDate(), v.getVisitNumber()), HashMap::putAll);
        Map<SubjectIdGeneMutation, Map<Date, List<CtDnaRaw>>> eventsBySubjectGeneMutation = ctDnaPerSubject.stream()
                .filter(e -> !NO_MUTATIONS_DETECTED.equals(e.getMutation()))
                .collect(groupingBy(e -> new SubjectIdGeneMutation(e.getSubjectId(), e.getGene(), e.getMutation()),
                        groupingBy(CtDnaRaw::getSampleDate)));

        Map<SubjectIdGeneMutation, List<CtDnaRaw>> eventsByGeneMutationExtended = eventsBySubjectGeneMutation.entrySet()
                .stream()
                .collect(toMap(Map.Entry::getKey,
                        entry -> withNoMutationsDetectedPerSubjectGeneMutation(visitBySampleDate,
                                entry.getKey(), entry.getValue())));

        return eventsByGeneMutationExtended.values().stream().flatMap(Collection::stream).collect(toList());
    }

    private List<CtDnaRaw> withNoMutationsDetectedPerSubjectGeneMutation(Map<Date, Double> visitBySampleDate,
                                                                         SubjectIdGeneMutation subjectGeneMutation,
                                                                         Map<Date, List<CtDnaRaw>> ctdnaByDatePerSubjectGeneMutation) {
        List<CtDnaRaw> events = new ArrayList<>();
        visitBySampleDate.forEach((sampleDate, visit) -> {
            if (!ctdnaByDatePerSubjectGeneMutation.containsKey(sampleDate)) {
                events.add(createNoMutationsDetectedEvent(sampleDate, visit, subjectGeneMutation));
            }
        });
        ctdnaByDatePerSubjectGeneMutation
                .values()
                .stream()
                .flatMap(Collection::stream)
                .map(e -> {
                    if (e.getReportedVaf() == null || e.getReportedVaf() < NO_MUTATIONS_DETECTED_VALUE) {
                        return e.toBuilder()
                                .reportedVafCalculated(NO_MUTATIONS_DETECTED_VALUE)
                                .reportedVafCalculatedPercent(NO_MUTATIONS_DETECTED_VALUE_IN_PERCENT)
                                .build();
                    } else {
                        return e.toBuilder()
                                .reportedVafCalculated(e.getReportedVaf())
                                .reportedVafCalculatedPercent(e.getReportedVafPercent())
                                .build();
                    }
                })
                .forEach(events::add);
        return events;
    }

    private CtDnaRaw createNoMutationsDetectedEvent(Date sampleDate, Double visit, SubjectIdGeneMutation subjectGeneMutation) {
        return CtDnaRaw.builder().id(UUID.randomUUID().toString())
                .mutation(subjectGeneMutation.getMutation())
                .gene(subjectGeneMutation.getGene())
                .subjectId(subjectGeneMutation.getSubjectId())
                .sampleDate(sampleDate)
                .visitNumber(visit)
                .reportedVafCalculated(NO_MUTATIONS_DETECTED_VALUE)
                .reportedVafCalculatedPercent(NO_MUTATIONS_DETECTED_VALUE_IN_PERCENT)
                .build();
    }

    @EqualsAndHashCode
    @Getter
    @AllArgsConstructor
    private static class SubjectIdGeneMutation {
        private String subjectId;
        private String gene;
        private String mutation;
    }
}
