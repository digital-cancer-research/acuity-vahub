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
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.TargetLesionRaw;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.TargetLesion;
import com.acuity.va.security.acl.domain.Datasets;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Component
public class TargetLesionDatasetsDataProvider extends
        SubjectAwareDatasetsRegularDataProvider<TargetLesionRaw, TargetLesion> implements CommonBaselineDataProvider {

    // consider lesion targets with numbers from 1 to 5 (T is a possible prefix), 0 is possible before the number
    private static final Pattern REGEX_LESION_NUMBER_PATTERN = Pattern.compile("T?0?[1-5]");

    @Override
    protected TargetLesion getWrapperInstance(TargetLesionRaw event, Subject subject) {
        return new TargetLesion(event, subject);
    }

    @Override
    protected Class<TargetLesionRaw> rawDataClass() {
        return TargetLesionRaw.class;
    }

    @Override
    protected Collection<TargetLesion> wrap(Datasets datasets, Collection<TargetLesionRaw> events) {

        final Map<String, Subject> subjectsById = getPopulationDatasetsDataProvider().loadData(datasets)
                .stream()
                .collect(Collectors.toMap(Subject::getSubjectId, s -> s));

        events = withCalculatedFields(events, subjectsById.values());

        return events.stream()
                .filter(e -> subjectsById.containsKey(e.getSubjectId())) //this filters out events for subjects not in study population
                .map(e -> getWrapperInstance(e, subjectsById.get(e.getSubjectId())))
                .collect(toList());
    }

    /**
     * Derives baseline, lesions diameters, percentage changes from {@link TargetLesionRaw}
     * and {@link Subject}.
     * Lesions with empty or invalid lesion numbers are filtered out.
     *
     * @param lesions - raw objects from database
     * @param subjects - raw subjects from the database as a map subjectId - subject
     * @return target lesions with calculated fields set
     */
    private List<TargetLesionRaw> withCalculatedFields(Collection<TargetLesionRaw> lesions,
                                                       Collection<Subject> subjects) {

        Map<String, List<TargetLesionRaw>> tlPerSubject = lesions.stream()
                .filter(tl -> tl.getLesionNumber() != null)
                .filter(tl -> REGEX_LESION_NUMBER_PATTERN.matcher(tl.getLesionNumber()).matches())
                .collect(Collectors.groupingBy(TargetLesionRaw::getSubjectId));
        Map<String, Date> baselineDatePerSubject = defineBaselineDatePerSubject(tlPerSubject, subjects);

        Map<String, List<TargetLesionRaw>> tlWithCalculatedFieldsPerSubject =
                withCalculatedFields(tlPerSubject, baselineDatePerSubject);

        return tlWithCalculatedFieldsPerSubject.values().stream()
                .flatMap(List::stream)
                .collect(toList());
    }

    /**
     * Methods calculates {@code baseline}, {@code percentageChange} and {@code bestPercentageChange} and set them
     *
     * @param lesionsBySubject - raw lesions grouped by subjectId
     * @param baselineDatePerSubject - baseline date for each subject
     * @return
     */
    private Map<String, List<TargetLesionRaw>> withCalculatedFields(Map<String, List<TargetLesionRaw>> lesionsBySubject,
                                                                    Map<String, Date> baselineDatePerSubject) {
        return lesionsBySubject.entrySet().stream()
                .filter(e -> baselineDatePerSubject.get(e.getKey()) != null)
                .collect(Collectors.toMap(Map.Entry::getKey,
                        m -> withCalculatedFieldsPerSubject(m.getValue(), baselineDatePerSubject.get(m.getKey()))
                )).entrySet().stream()
                .filter(e -> !e.getValue().isEmpty())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * Methods calculates {@code baseline}, {@code percentageChange} and {@code bestPercentageChange} per subject's
     * target lesions.
     * Lesions with empty lesion date and with lesion date before baseline are filtered out.
     *
     * @param tlsPerSubject - subject's target lesions
     * @param baselineDate - subject's baseline date
     * @return sorted list of subject's target lesions with calculated fields
     */
    private List<TargetLesionRaw> withCalculatedFieldsPerSubject(List<TargetLesionRaw> tlsPerSubject, Date baselineDate) {

        tlsPerSubject = tlsPerSubject.stream().filter(l -> l.getLesionDate() != null).filter(tl -> !baselineDate.after(tl.getLesionDate()))
                .sorted(Comparator.comparing(TargetLesionRaw::getLesionDate)
                        .thenComparing(TargetLesionRaw::getVisitNumber, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(TargetLesionRaw::getLesionNumber))
                .collect(toList());

        Map<Date, List<TargetLesionRaw>> tlsPerSubjectByDate = withDiameterSumsAndBaseline(tlsPerSubject, baselineDate);

        List<TargetLesionRaw> baselineLesions = tlsPerSubjectByDate.get(baselineDate);
        Map<String, Integer> baselineDiameterByLesion = baselineLesions.stream()
                .filter(tl -> tl.getLesionDiameter() != null)
                .collect(toMap(TargetLesionRaw::getLesionNumber, TargetLesionRaw::getLesionDiameter, (tl1, tl2) -> tl1));
        Integer baselineLesionsDiameter = baselineLesions.stream().findAny()
                .map(TargetLesionRaw::getLesionsDiameterPerAssessment).orElse(0);

        if (!baselineLesionsDiameter.equals(0)) {
            // then calculate percentage change relative to baseline
            tlsPerSubject = tlsPerSubjectByDate.values().stream().map(tlsByDate -> {
                Double changeForAssessment = calcPercentageChange(tlsByDate.stream().findAny()
                                .map(TargetLesionRaw::getLesionsDiameterPerAssessment).orElse(null),
                        baselineLesionsDiameter).orElse(null);

                return tlsByDate.stream().map(tl -> {

                    int baselineLesionDiameter = baselineDiameterByLesion.getOrDefault(tl.getLesionNumber(), 0);
                    Double changeForLesion = calcPercentageChange(tl.getLesionDiameter(),
                            baselineLesionDiameter).orElse(null);

                    return tl.toBuilder().baselineLesionDiameter(baselineLesionDiameter)
                            .lesionPercentageChangeFromBaseline(changeForLesion)
                            .sumBaselineDiameter(baselineLesionsDiameter)
                            .sumPercentageChangeFromBaseline(changeForAssessment).build();
                }).collect(toList());
            }).flatMap(Collection::stream).collect(toList());
        } else {
            tlsPerSubject = tlsPerSubjectByDate.values().stream().flatMap(Collection::stream).collect(toList());
        }

        // define best percentage change (which is minimum among all changes)
        OptionalDouble bestPercentageChange = tlsPerSubject.stream().filter(t -> !t.isBaseline())
                .filter(t -> t.getSumPercentageChangeFromBaseline() != null)
                .mapToDouble(TargetLesionRaw::getSumPercentageChangeFromBaseline).min();

        boolean subjectHasMissings = tlsPerSubject.stream().anyMatch(TargetLesionRaw::isMissingsAtVisitPresent);
        return tlsPerSubject.stream().map(t -> {
            TargetLesionRaw.TargetLesionRawBuilder builder = t.toBuilder();
            builder.missingsPresent(subjectHasMissings);
            if (bestPercentageChange.isPresent()) {
                final Double bestPercentageChangeValue = bestPercentageChange.getAsDouble();
                builder.sumBestPercentageChangeFromBaseline(bestPercentageChangeValue);
                // mark target lesions as best percentage change if they are
                if (bestPercentageChangeValue.equals(t.getSumPercentageChangeFromBaseline())) {
                    builder.bestPercentageChange(true);
                }
            }
            return builder.build();
        }).collect(toList());
    }

    /**
     * Sets sum of lesion's diameter per lesion date and baseline data
     *
     * @param subjectLesions
     * @param baselineDate
     * @return
     */
    private Map<Date, List<TargetLesionRaw>> withDiameterSumsAndBaseline(Collection<TargetLesionRaw> subjectLesions, Date baselineDate) {

        Map<Date, List<TargetLesionRaw>> lesionsBySubjectByLesionDate = subjectLesions.stream()
                .collect(Collectors.groupingBy(TargetLesionRaw::getLesionDate, TreeMap::new, toList()));

        lesionsBySubjectByLesionDate.put(baselineDate, lesionsBySubjectByLesionDate.get(baselineDate).stream()
                .map(tl -> tl.toBuilder().baseline(true).build())
                .collect(toList()));

        Integer[] minSum = new Integer[1];
        minSum[0] = getDiametersSum(lesionsBySubjectByLesionDate.get(baselineDate)).orElse(null);

        List<String> baselineLesionsNumbers = getValidLesionsNumbers(lesionsBySubjectByLesionDate.get(baselineDate));

        return lesionsBySubjectByLesionDate.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> withDiametersSumByLesionDate(e.getValue(), baselineLesionsNumbers, minSum),
                        (u, v) -> u, TreeMap::new));
    }


    /**
     * Calculates and sets sum of lesion diameters per lesion date; calculates absolute change from minimum,
     * percentage change from minimum and from baseline
     * If there are no missings on current date (no lesions are missing comparing to the baseline and no lesion diameter is null)
     * and current diameter is less then previous minimum, minimum is updated
     *
     * @param subjectLesionsByLesionDate subject's lesions with particular lesion date
     * @param baselineLesionsNumbers - lesion numbers on baseline
     * @param minSum - minimal diameters sum on previous period
     * @return
     */
    private List<TargetLesionRaw> withDiametersSumByLesionDate(List<TargetLesionRaw> subjectLesionsByLesionDate,
                                                               List<String> baselineLesionsNumbers, Integer[] minSum) {
        Optional<Integer> diameterSum = getDiametersSum(subjectLesionsByLesionDate);
        List<String> lesionsNumbers = getValidLesionsNumbers(subjectLesionsByLesionDate);

        //Subject has missings when lesions numbers differ from baseline's numbers or when lesion data missing.
        boolean hasMissingsAtVisit = !baselineLesionsNumbers.equals(lesionsNumbers);

        List<TargetLesionRaw> withDiametersSumByLesionDate = subjectLesionsByLesionDate.stream()
                .map(lesionsByDate -> lesionsByDate.toBuilder()
                        .lesionsDiameterPerAssessment(diameterSum.orElse(null))
                        .sumChangeFromMinimum(minSum[0] != null && diameterSum.isPresent() ? diameterSum.get() - minSum[0] : null)
                        .sumPercentageChangeFromMinimum(calcPercentageChange(diameterSum.orElse(null), minSum[0]).orElse(null))
                        .lesionCountAtBaseline(baselineLesionsNumbers.size())
                        .lesionCountAtVisit(lesionsNumbers.size())
                        .missingsAtVisitPresent(hasMissingsAtVisit)
                        .build())
                .collect(toList());

        if (!hasMissingsAtVisit && diameterSum.isPresent() && (diameterSum.get() < (minSum[0] == null ? Integer.MAX_VALUE : minSum[0]))) {
            minSum[0] = diameterSum.get();
        }

        return withDiametersSumByLesionDate;
    }

    /**
     * Derives sorted list of lesions numbers per assessment of a subject. Numbers have string type as it's stored
     * in date base in this type
     * Lesions with empty lesion date, lesion diameter, lesion number, or with the lesion number of a wrong format are filtered out
     *
     * @param tlsPerAssessment - sorted target lesions
     * @return
     */
    private List<String> getValidLesionsNumbers(List<TargetLesionRaw> tlsPerAssessment) {
        return tlsPerAssessment.stream()
                .filter(tl -> tl.getLesionDate() != null)
                .filter(tl -> tl.getLesionDiameter() != null)
                .map(TargetLesionRaw::getLesionNumber)
                .collect(toList());
    }

    private Optional<Double> calcPercentageChange(Integer dev, Integer baseline) {
        if (dev == null || baseline == null || baseline == 0) {
            return Optional.empty();
        }
        return Optional.of(Math.round((dev - baseline) * 100 * 100.0 / baseline) / 100.0); // round to 2 decimal places
    }

    /**
     * Calculates sum of lesion diameters. Empty values are just skipped, if there are any non-null values
     *
     * @param subjectLesionsByLesionDate
     * @return
     */
    private Optional<Integer> getDiametersSum(List<TargetLesionRaw> subjectLesionsByLesionDate) {
        if (subjectLesionsByLesionDate.stream().anyMatch(t -> t.getLesionDiameter() != null)) {
            return Optional.of(subjectLesionsByLesionDate.stream().mapToInt(tl -> tl.getLesionDiameter() == null ? 0 : tl.getLesionDiameter()).sum());
        } else {
            return Optional.empty();
        }
    }
}
