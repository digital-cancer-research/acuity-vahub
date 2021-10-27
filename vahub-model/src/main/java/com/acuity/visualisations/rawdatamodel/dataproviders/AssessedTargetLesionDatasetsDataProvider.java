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

import com.acuity.visualisations.rawdatamodel.dataproviders.common.SubjectAwareDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.vo.AssessedTargetLesionRaw;
import com.acuity.visualisations.rawdatamodel.vo.AssessmentRaw;
import com.acuity.visualisations.rawdatamodel.vo.AssessmentRaw.Response;
import com.acuity.visualisations.rawdatamodel.vo.NonTargetLesionRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.AssessedTargetLesion;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Assessment;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.TargetLesion;
import com.acuity.va.security.acl.domain.Dataset;
import com.acuity.va.security.acl.domain.Datasets;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.acuity.visualisations.rawdatamodel.util.Constants.NO;
import static com.acuity.visualisations.rawdatamodel.util.Constants.YES;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

/**
 * This data provider is for aggregated entity {@link AssessedTargetLesionRaw}. Since it's aggregated, it does NOT
 * have an own repository to communicate with data base.
 */
@Component
public class AssessedTargetLesionDatasetsDataProvider extends
        SubjectAwareDatasetsDataProvider<AssessedTargetLesionRaw, AssessedTargetLesion> implements CommonBaselineDataProvider {

    public static final int DEFAULT_FREQUENCY = 6;

    @Autowired
    private AssessmentDatasetsDataProvider assessmentDatasetsDataProvider;
    @Autowired
    private TargetLesionDatasetsDataProvider targetLesionDatasetsDataProvider;
    @Autowired
    private NonTargetLesionDatasetsDataProvider nonTargetLesionDatasetsDataProvider;

    @Override
    protected AssessedTargetLesion getWrapperInstance(AssessedTargetLesionRaw event, Subject subject) {
        return new AssessedTargetLesion(event, subject);
    }

    @Override
    protected Class<AssessedTargetLesionRaw> rawDataClass() {
        return AssessedTargetLesionRaw.class;
    }

    @Override
    protected Collection<AssessedTargetLesionRaw> getData(Dataset dataset) {
        return dataProvider.getData(AssessedTargetLesionRaw.class, dataset, ds -> {

            Datasets dss = new Datasets(ds);
            Collection<Assessment> assessments = assessmentDatasetsDataProvider.loadData(dss);
            Collection<TargetLesion> targetLesions = targetLesionDatasetsDataProvider.loadData(dss);
            Collection<NonTargetLesionRaw> nonTargetLesions = nonTargetLesionDatasetsDataProvider.getData(dataset);

            return mergeIntoAssessedTargetLesionsWithNTLInfo(assessments, targetLesions, nonTargetLesions);
        });
    }

    /**
     * For some plots we display data by visit, not all individual lesions, so group by lesion date to exclude "duplicates"
     * in sense of that plots
     * @param datasets - datasets object
     * @return collection of AssessedTargetLesion where there is only one event per subject's visit
     */
    @SneakyThrows
    public Collection<AssessedTargetLesion> loadDataByVisit(Datasets datasets) {
        List<AssessedTargetLesionRaw> events = datasets.getDatasets().stream()
                .map(this::getDataByVisit)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        return wrap(datasets, events);
    }

    /**
     * For some plots we display data by visit, not all individual lesions, so group by lesion date to exclude "duplicates"
     * in sense of that plots
     * @param dataset - dataset object
     * @return collection of AssessedTargetLesionRaw where there is only one event per subject's visit
     */
    private Collection<AssessedTargetLesionRaw> getDataByVisit(Dataset dataset) {

        return this.getData(dataset).stream()
                    .collect(groupingBy(AssessedTargetLesionRaw::getSubjectId))
                    .values().stream()
                    .flatMap(atls -> atls.stream()
                                    .collect(toMap(AssessedTargetLesionRaw::getLesionDate, atl -> atl, (atl1, atl2) -> atl1))
                            .values().stream())
                    .collect(toList());
    }

    /**
     * Derives response and best response from {@link Assessment}. Merge Assessment with {@link TargetLesion}
     * Merge non-target lesion info ({@link NonTargetLesionRaw}) with {@link TargetLesion}
     *
     * @param assessments - assessment events per dataset
     * @param lesions - target lesion events per dataset
     * @param nonTargetLesions - non-target lesion events per dataset
     * @return list of {@link AssessedTargetLesionRaw} - aggregated entities
     */
    private List<AssessedTargetLesionRaw> mergeIntoAssessedTargetLesionsWithNTLInfo(Collection<Assessment> assessments,
                                                                                    Collection<TargetLesion> lesions,
                                                                                    Collection<NonTargetLesionRaw> nonTargetLesions) {

        Map<String, List<AssessedTargetLesionRaw>> atlsBySubject = preliminaryMerge(lesions, assessments);

        atlsBySubject = atlsBySubject.entrySet().stream()
                .collect(toMap(Map.Entry::getKey,
                        e -> withCalculatedResponseInfoPerSubject(e.getValue())));
        atlsBySubject = withNonTargetLesionInfo(atlsBySubject, nonTargetLesions);
        atlsBySubject = withAssessmentFrequency(atlsBySubject);

        // this step must be performed last, so that best response events were fully initialized
        return withBestResponseEventSet(atlsBySubject);
    }

    /**
     * Define responses of assessments and best response of assessments per subject and set it returning list
     * of new {@link AssessedTargetLesionRaw} objects
     *
     * @param atlsPerSubject - assessed target lesion events grouped by subject
     * @return events with assessment response and best response information set
     */
    private List<AssessedTargetLesionRaw> withCalculatedResponseInfoPerSubject(List<AssessedTargetLesionRaw> atlsPerSubject) {
        Map<Date, List<AssessedTargetLesionRaw>> atlsPerLesionDateMap = atlsPerSubject.stream()
                .collect(groupingBy(atl -> atl.getTargetLesionRaw().getLesionDate()));
        Map<Date, List<AssessedTargetLesionRaw>> atlsPerAssessmentWithResponse = getAtlsWithCalculatedResponsesByAssessment(atlsPerLesionDateMap);
        boolean hasMissings = atlsPerSubject.stream().anyMatch(t -> t.getTargetLesionRaw().isMissingsPresent());
        return getAtlsPerSubjectWithCalculatedBestResponse(atlsPerAssessmentWithResponse, hasMissings);

    }

    /**
     * Calculates responses of assessment for every target lesions (target lesions within one assessments have the same response)
     * and return new objects of {@link AssessedTargetLesionRaw} grouped by assessments
     * Baseline cannot have "Missing Target Lesions" response, baseline's lesions are considered as reference ones
     *
     * @param atlsPerVisDateMap - list of assessed target lesions per visit date
     * @return map of assessed target lesions list per visit date with assessment info populated
     */
    private Map<Date, List<AssessedTargetLesionRaw>> getAtlsWithCalculatedResponsesByAssessment(
            Map<Date, List<AssessedTargetLesionRaw>> atlsPerVisDateMap) {
        return atlsPerVisDateMap.entrySet().stream()
                .collect(toMap(Map.Entry::getKey,
                        e -> {
                            List<AssessedTargetLesionRaw> atlsPerAssessment = e.getValue();
                            if (!atlsPerAssessment.isEmpty()) {
                                String response;
                                AssessedTargetLesionRaw atl = atlsPerAssessment.iterator().next();
                                if (!atl.isBaseline() && atl.getTargetLesionRaw().isMissingsAtVisitPresent()) {
                                    response = Response.MISSING_TARGET_LESIONS_RESPONSE.getName();
                                } else {
                                    response = (atl.getAssessmentRaw() == null)
                                            ? Response.NO_ASSESSMENT.getName() : atl.getAssessmentRaw().getResponse();
                                }
                                return atlsPerAssessment.stream()
                                        .map(o -> o.toBuilder().response(response).build())
                                        .collect(toList());
                            } else {
                                return atlsPerAssessment;
                            }
                        }));
    }

    /**
     * Defines best response and set it into every {@link AssessedTargetLesionRaw} creating new object.
     *
     * @param atlsByAssessment - assessed target lesion events grouped by lesion date
     * @param hasMissings - if current subject has any missings during the study
     * @return events with best response calculated and set
     */
    private List<AssessedTargetLesionRaw> getAtlsPerSubjectWithCalculatedBestResponse(
            Map<Date, List<AssessedTargetLesionRaw>> atlsByAssessment, boolean hasMissings) {
        Map<Date, Response> lesionDateResponseMap = getAssessmentsResponsesMap(atlsByAssessment);
        String bestResponse = calculateBestResponse(lesionDateResponseMap, hasMissings);

        return atlsByAssessment.values().stream()
                .flatMap(List::stream)
                .map(atl -> atl.toBuilder().bestResponse(bestResponse).build())
                .collect(toList());
    }

    /**
     * Get latest event (any of the last visit), that has response equal to the calculated best response,
     * or null if there are no such events
     * @param atlsPerSubject - assessed target lesions grouped by subject
     * @return best response event (assessed target lesion event which had best response) for particular subject
     */
    private AssessedTargetLesionRaw findBestResponseEvent(List<AssessedTargetLesionRaw> atlsPerSubject) {

        return atlsPerSubject.stream().filter(atl -> atl.getBestResponse().equals(atl.getResponse()))
                .max(Comparator.comparing(AssessedTargetLesionRaw::getLesionDate)).orElse(null);
    }

    /**
     * Then takes one of responses from a group (as the group per assessment has the same response for all target lesions).
     * Excluding baseline as it should not be considered to calculate best response.
     *
     * @param atlsByAssessment - assessed target lesions grouped by assessment date
     * @return map of subject's response by lesion date
     */
    private Map<Date, Response> getAssessmentsResponsesMap(Map<Date, List<AssessedTargetLesionRaw>> atlsByAssessment) {
        return atlsByAssessment.entrySet().stream()
                .filter(e -> e.getValue().stream().findFirst().map(atl -> !atl.isBaseline()).orElse(false))
                .collect(toMap(Map.Entry::getKey, e -> {
                    List<AssessedTargetLesionRaw> atlsPerAssessment = e.getValue();
                    return atlsPerAssessment.stream().findFirst()
                            .map(atl -> Response.getInstance(atl.getResponse())).orElse(Response.NO_ASSESSMENT);
                }));
    }

    /**
     * Calculates best response of assessments for a subject
     *
     * @param responseByVisitDate - subject's responses by visit date
     * @return subject's best response
     */
    private String calculateBestResponse(Map<Date, Response> responseByVisitDate, Boolean hasMissings) {
        String bestResponse;
        List<String> responses = responseByVisitDate.values().stream().map(Response::getName).collect(toList());
        boolean containsMissingStatus = responses.contains(Response.MISSING_TARGET_LESIONS_RESPONSE.getName());
        if (!(containsMissingStatus || hasMissings)) {
            Optional<Response> proposedBestRespOpt = responseByVisitDate.values().stream()
                    .min(Comparator.comparing(Response::getRank));
            bestResponse = proposedBestRespOpt.map(proposed -> getBestResponseFromProposed(proposed, responseByVisitDate))
                    .orElse(Response.NO_ASSESSMENT.getName());
        } else {
            bestResponse = Response.MISSING_TARGET_LESIONS_RESPONSE.getName();
        }
        return bestResponse;
    }

    private String getBestResponseFromProposed(Response proposedBestResp, Map<Date, Response> visitDateResponseMap) {
        String bestResponse;
        if (Response.PARTIAL_RESPONSE.equals(proposedBestResp)) {
            Optional<Date> lastAssessmentDate = visitDateResponseMap.keySet().stream().max(Date::compareTo);
            bestResponse = lastAssessmentDate.map(date -> getBestResponseFromLastAssessmentDate(date, visitDateResponseMap))
                    .orElse(Response.NO_ASSESSMENT.getName());
        } else {
            bestResponse = proposedBestResp.getName();
        }
        return bestResponse;
    }

    private String getBestResponseFromLastAssessmentDate(Date lastAssessmentDate, Map<Date, Response> visitDateResponseMap) {
        String bestResponse;
        if (Response.PARTIAL_RESPONSE.equals(visitDateResponseMap.get(lastAssessmentDate))) {
            bestResponse = Response.PARTIAL_RESPONSE.getName();
        } else {
            if (visitDateResponseMap.values().stream().filter(Response.PARTIAL_RESPONSE::equals).count() > 1) {
                bestResponse = Response.PARTIAL_RESPONSE.getName();
            } else {
                bestResponse = Response.STABLE_DISEASE.getName();
            }
        }
        return bestResponse;
    }

    /**
     * Merges target lesions and assessments of response into {@link AssessedTargetLesionRaw} without calculated fields.
     * Entities joined by {@code subjectId}, {@code visitDate} and {@code visitNumber}
     *
     * @param lesions - list of target lesions per dataset
     * @param assessments - list of assessments per dataset
     * @return map of merged assessed target lesion events grouped by subject
     */
    private Map<String, List<AssessedTargetLesionRaw>> preliminaryMerge(Collection<TargetLesion> lesions,
                                                                        Collection<Assessment> assessments) {

        Map<String, Map<Integer, List<TargetLesion>>> lesionsBySubjectByVisitNumber =
                groupTLBySubjectAndVisitNumber(lesions);
        Map<String, Map<Integer, List<Assessment>>> assessmentsBySubjectByVisNum =
                groupAssessmentsBySubjectAndVisitNumber(assessments);

        return lesionsBySubjectByVisitNumber.entrySet().stream()
                .collect(toMap(Map.Entry::getKey,
                        entry -> {
                            String subjectId = entry.getKey();
                            Map<Integer, List<Assessment>> assessmentsByVisNumMap = assessmentsBySubjectByVisNum.getOrDefault(subjectId, new HashMap<>());
                            return mergeByVisitNumberAndDate(subjectId, assessmentsByVisNumMap, entry.getValue());
                        }));
    }

    /**
     * Merges target lesions and assessments of response into {@link AssessedTargetLesionRaw} without calculated fields
     * by {@code visitNumber} and {@code visitDate} for {@code subject}
     *
     * @param subjectId - subject's id
     * @param assessmentByVisNum - map of subject's assessments grouped by visit number
     * @param subjectLesionsByVisNum  - map of subject's target lesions grouped by visit number
     * @return list of subject's assessed target lesions (merged entity)
     */
    private List<AssessedTargetLesionRaw> mergeByVisitNumberAndDate(String subjectId, Map<Integer, List<Assessment>> assessmentByVisNum,
                                                                    Map<Integer, List<TargetLesion>> subjectLesionsByVisNum) {
        List<AssessedTargetLesionRaw> assessedTargetLesions = new ArrayList<>();
        subjectLesionsByVisNum.forEach((number, tls) -> {
            List<Assessment> as = assessmentByVisNum.getOrDefault(number, new ArrayList<>());
            tls.forEach(tl ->
                    assessedTargetLesions.add(AssessedTargetLesionRaw.builder()
                            .subjectId(subjectId)
                            .assessmentRaw(assessmentMatchedByVisDate(as, tl.getEvent().getVisitDate()))
                            .targetLesionRaw(tl.getEvent())
                            .build())
            );
        });
        return assessedTargetLesions;
    }

    /**
     * In rare case there are several assessment events per one subject's visit, which is incorrect data,
     * give it a chance and try to match by visit date before taking any of events that satisfy this condition
     */
    private AssessmentRaw assessmentMatchedByVisDate(List<Assessment> as, Date visDate) {
        return as.stream()
                .filter(a -> a.getEvent().getVisitDate().equals(visDate)).findFirst().map(Assessment::getEvent).orElse(null);
    }

    /**
     * Method to group list of {@link TargetLesion} by {@code subjectId} and {@code visitNumber}.
     * Before grouping the list is filtered to get rid of non-valuable entities (from requirements).
     *
     * @param lesions - list of target lesions per dataset
     * @return target lesions grouped first by subject and then by visit number
     */
    private Map<String, Map<Integer, List<TargetLesion>>> groupTLBySubjectAndVisitNumber(Collection<TargetLesion> lesions) {
        return lesions.stream()
                .filter(l -> l.getEvent().getLesionDiameter() != null)
                .filter(l -> l.getEvent().getLesionDate() != null)
                .filter(l -> l.getEvent().getVisitDate() != null)
                .filter(l -> l.getEvent().getVisitNumber() != null)
                .collect(groupingBy(TargetLesion::getSubjectId,
                        groupingBy(l -> l.getEvent().getVisitNumber())))
                .entrySet().stream()
                .filter(e -> e.getValue().size() > 1) // there should exist at least two measurements (not baseline only)
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * Method to group list of {@link Assessment} by {@code subjectId} and {@code visitNumber}.
     *
     * @param assessments - list of assessments per dataset
     * @return assessments grouped first by subject and then by visit number
     */
    private Map<String, Map<Integer, List<Assessment>>> groupAssessmentsBySubjectAndVisitNumber(Collection<Assessment> assessments) {
        return assessments.stream()
                .filter(a -> a.getEvent().getVisitNumber() != null)
                .filter(a -> a.getEvent().getVisitDate() != null)
                .collect(groupingBy(Assessment::getSubjectId,
                        groupingBy(a -> a.getEvent().getVisitNumber())));
    }

    /**
     * Merges assessed target lesions with non-target lesion info
     * by {@code visitNumber} and {@code visitDate} for {@code subject}
     *
     * @param atlsBySubject - map of assessed target lesions grouped by by subject
     * @param ntls - list non-target lesions. Normally there is 1 NonTargetLesionRaw per subject per visit.
     *                    In rare case there are several non-target
     *                    lesion events per one subject's visit, which is incorrect data,
     *                    give it a chance and try to match by visit date before taking any of events that satisfy this condition
     * @return list of assessed target lesions by subject with non-target lesion info attached
     */
    private Map<String, List<AssessedTargetLesionRaw>> withNonTargetLesionInfo(Map<String, List<AssessedTargetLesionRaw>> atlsBySubject,
                                                                               Collection<NonTargetLesionRaw> ntls) {
        Map<String, Map<Integer, List<NonTargetLesionRaw>>> ntlsBySubjectByVisNum = groupNtlsBySubjectAndVisitNumber(ntls);

        return atlsBySubject.entrySet().stream().collect(toMap(Map.Entry::getKey, atlsPerSubject -> {

            boolean hasNonTargetLesions = ntlsBySubjectByVisNum.containsKey(atlsPerSubject.getKey());

            return atlsPerSubject.getValue().stream().map(atl -> {
                List<NonTargetLesionRaw> ntlsOfSubjectOfVisNum = ntlsBySubjectByVisNum.getOrDefault(atlsPerSubject.getKey(), new HashMap<>())
                        .getOrDefault(atl.getVisitNumber(), new ArrayList<>());
                final NonTargetLesionRaw nonTargetLesionRaw = ntlsOfSubjectOfVisNum.stream()
                        .filter(ntl -> ntl.getVisitDate().equals(atl.getTargetLesionRaw().getVisitDate())).findFirst().orElse(null);
                return atl.toBuilder()
                        .nonTargetLesionsPresent(hasNonTargetLesions ? YES : NO)
                        .nonTargetLesionRaw(nonTargetLesionRaw)
                        .build();
            }).collect(toList());
        }));
    }

    /**
     * Method to group list of {@link NonTargetLesionRaw} by {@code subjectId} and {@code visitNumber}.
     *
     * @param ntls - list of non-target lesions
     * @return non-target lesions grouped first by subject and then by visit number
     */
    private Map<String, Map<Integer, List<NonTargetLesionRaw>>> groupNtlsBySubjectAndVisitNumber(Collection<NonTargetLesionRaw> ntls) {
        return ntls.stream()
                .filter(a -> a.getVisitNumber() != null)
                .filter(a -> a.getVisitDate() != null)
                .collect(groupingBy(NonTargetLesionRaw::getSubjectId,
                        groupingBy(NonTargetLesionRaw::getVisitNumber)));
    }

    private Map<String, List<AssessedTargetLesionRaw>> withAssessmentFrequency(Map<String, List<AssessedTargetLesionRaw>> atlsBySubject) {

        Map<String, Integer> frequencyBySubject = new HashMap<>();

        return atlsBySubject.entrySet().stream().collect(toMap(Map.Entry::getKey,
                atls -> atls.getValue().stream().map(atl -> {
                    final AssessedTargetLesionRaw.AssessedTargetLesionRawBuilder builder = atl.toBuilder();

                    if (isAssessmentFrequencyEmpty(atl)) {
                        Integer frequency = getFrequencyFromSubjectOrAnyEvent(atl.getSubjectId(), atlsBySubject, frequencyBySubject);
                        builder.assessmentFrequency(frequency);
                    } else {
                        builder.assessmentFrequency(atl.getAssessmentRaw().getAssessmentFrequency());
                    }
                    return builder.build();
                }).collect(toList())));
    }

    /**
     * Rare case when target lesion has no joined assessment.
     * In this case try finding non-null frequency among other events of this subject;
     * If no frequency found for the subject, take any non-null frequency within the dataset.
     * If nothing found, default to 6.
     */
    private Integer getFrequencyFromSubjectOrAnyEvent(String subjectId, Map<String, List<AssessedTargetLesionRaw>> atlsBySubject,
                                                      Map<String, Integer> frequencyBySubject) {
        if (frequencyBySubject.isEmpty()) {
            initFrequencyBySubject(frequencyBySubject, atlsBySubject);
        }
        Integer frequency = frequencyBySubject.get(subjectId);
        if (frequency == null) {
            frequency = atlsBySubject.values().parallelStream()
                    .flatMap(Collection::parallelStream)
                    .filter(t -> !isAssessmentFrequencyEmpty(t)).findAny()
                    .map(e -> e.getAssessmentRaw().getAssessmentFrequency()).orElse(DEFAULT_FREQUENCY);
        }
        return frequency;
    }

    private boolean isAssessmentFrequencyEmpty(AssessedTargetLesionRaw atl) {
        return atl.getAssessmentRaw() == null || atl.getAssessmentRaw().getAssessmentFrequency() == null;
    }

    private void initFrequencyBySubject(Map<String, Integer> frequencyBySubject, Map<String, List<AssessedTargetLesionRaw>> data) {
        data.forEach((key, value) -> frequencyBySubject.put(key, value.stream().filter(atl -> !isAssessmentFrequencyEmpty(atl))
                .findFirst().map(atl -> atl.getAssessmentRaw().getAssessmentFrequency()).orElse(null)));
    }

    private List<AssessedTargetLesionRaw> withBestResponseEventSet(Map<String, List<AssessedTargetLesionRaw>> merged) {
        return merged.values().stream().flatMap(atlsPerSubject -> {
            AssessedTargetLesionRaw bestResponseEvent = findBestResponseEvent(atlsPerSubject);
            return atlsPerSubject.stream().map(atl -> atl.toBuilder().bestResponseEvent(bestResponseEvent).build());
        }).collect(toList());
    }

}
