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

import com.acuity.visualisations.rawdatamodel.dao.DeathRepository;
import com.acuity.visualisations.rawdatamodel.dao.DoseDiscRepository;
import com.acuity.visualisations.rawdatamodel.dao.DrugDoseRepository;
import com.acuity.visualisations.rawdatamodel.dao.PopulationRepository;
import com.acuity.visualisations.rawdatamodel.dao.StudyInfoRepository;
import com.acuity.visualisations.rawdatamodel.dataproviders.common.DatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.vo.DeathRaw;
import com.acuity.visualisations.rawdatamodel.vo.DoseDiscRaw;
import com.acuity.visualisations.rawdatamodel.vo.DrugDiscontinued;
import com.acuity.visualisations.rawdatamodel.vo.DrugDoseRaw;
import com.acuity.visualisations.rawdatamodel.vo.DrugDosed;
import com.acuity.visualisations.rawdatamodel.vo.GroupType;
import com.acuity.visualisations.rawdatamodel.vo.StudyInfo;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.Subject.SubjectMedicalHistories;
import com.acuity.visualisations.rawdatamodel.vo.Subject.SubjectStudySpecificFilters;
import com.acuity.visualisations.rawdatamodel.vo.Subject.SubjectVisit;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.VisitNumber;
import com.acuity.va.security.acl.domain.Dataset;
import com.acuity.va.security.acl.domain.Datasets;
import com.google.common.collect.Range;
import com.google.common.collect.Sets;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.acuity.visualisations.rawdatamodel.util.Constants.NO;
import static com.acuity.visualisations.rawdatamodel.util.Constants.YES;
import static com.acuity.visualisations.rawdatamodel.util.DaysUtil.daysBetween;
import static com.acuity.visualisations.rawdatamodel.util.DaysUtil.monthsBetween;
import static com.acuity.visualisations.rawdatamodel.util.DaysUtil.truncLocalTime;
import static com.acuity.visualisations.rawdatamodel.util.GroupsUtil.getGroupCharacter;
import static java.util.Comparator.comparing;
import static org.apache.commons.lang3.ObjectUtils.firstNonNull;

@Component
public class PopulationDatasetsDataProvider extends DatasetsDataProvider<Subject, Subject> {

    private static final String WEIGHT_TEST = "weight";
    private static final String HEIGHT_TEST = "height";
    private static final String DEFAULT_GROUP = "Default group";
    private static final String COHORT = "Cohort";
    private static final int MONTHS_IN_YEAR = 12;

    @Autowired
    private DrugDoseRepository drugDoseRepository;
    @Autowired
    private DeathRepository deathRepository;
    @Autowired
    private DoseDiscRepository doseDiscRepository;
    @Autowired
    private StudyInfoRepository studyInfoRepository;
    @Autowired
    protected PopulationRepository populationRepository;

    @Getter
    private static class SubjectMapFieldsHolder {
        private Map<String, String> drugsDosed = new HashMap<>();
        private Map<String, String> drugsDiscontinued = new HashMap<>();
        private Map<String, String> drugsMaxDoses = new HashMap<>();
        private Map<String, Double> drugsRawMaxDoses = new HashMap<>();
        private Map<String, String> drugsMaxFrequencies = new HashMap<>();
        private Map<String, String> drugDiscontinuationMainReason = new HashMap<>();
        private Map<String, Date> drugDiscontinuationDate = new HashMap<>();
        private Map<String, Date> drugFirstDoseDate = new HashMap<>();
        private Map<String, Date> drugLastDoseDate = new HashMap<>();
        private Map<String, Integer> drugTotalDurationInclBreaks = new HashMap<>();
        private Map<String, Integer> drugTotalDurationExclBreaks = new HashMap<>();
    }

    @Override
    @Cacheable(keyGenerator = "datasetsKeyGenerator", cacheResolver = "refreshableCacheResolver")
    public Collection<Subject> loadData(Datasets datasets) {
        return super.loadData(datasets);
    }

    @Override
    protected Collection<Subject> wrap(Datasets datasets, Collection<Subject> events) {
        List<Subject> eventsToMerge = events.stream()
                .map(s -> s.toBuilder().build())
                .collect(Collectors.toList());
        return mergeDatasets(datasets, eventsToMerge);
    }

    @Override
    public Collection<Subject> getData(Dataset dataset) {
        return dataProvider.getData(Subject.class, dataset, ds -> {
                    Collection<Subject> rawData = populationRepository.getRawData(ds.getId());
                    List<DrugDoseRaw> drugDoseRawData = drugDoseRepository.getRawData(dataset.getId());
                    List<DeathRaw> deathRawData = deathRepository.getRawData(dataset.getId());
                    List<DoseDiscRaw> doseDiscRawData = doseDiscRepository.getRawData(dataset.getId());

                    Map<String, SubjectMapFieldsHolder> subjectWithDrugInfoMap = new HashMap<>();

                    Map<String, List<DrugDoseRaw>> drugDosesBySubject = drugDoseRawData.stream().collect(Collectors.groupingBy(DrugDoseRaw::getSubjectId));

                    Set<String> studyDrugs = drugDoseRawData.stream().map(DrugDoseRaw::getDrug).collect(Collectors.toSet());

                    List<DrugDosed> drugsDosed = rawData.stream().map(e -> buildDrugDosed(e, drugDosesBySubject.get(e.getSubjectId()), studyDrugs))
                            .flatMap(List::stream).collect(Collectors.toList());

                    Map<String, List<DoseDiscRaw>> drugDiscBySubject = doseDiscRawData.stream().collect(Collectors.groupingBy(DoseDiscRaw::getSubjectId));

                    List<DrugDiscontinued> drugsDisc = rawData.stream().map(e -> buildDrugDiscontinued(e, drugDiscBySubject.get(e.getSubjectId()), studyDrugs))
                            .flatMap(List::stream).collect(Collectors.toList());

                    drugsDosed.forEach(drugDosed -> populateSubjectDrugsDosed(subjectWithDrugInfoMap, drugDosed));

                    drugsDisc.forEach(drugDisc -> populateSubjectDrugsDisc(subjectWithDrugInfoMap, drugDisc));

                    List<SubjectVisit> subjVisits = populationRepository.getAttendedVisits(ds.getId());
                    Map<String, List<String>> attendedVisits = subjVisits
                            .stream()
                            .collect(Collectors
                                    .groupingBy(SubjectVisit::getSubjectId, Collectors.mapping(SubjectVisit::getVisit, Collectors
                                            .toList())));

                    Map<String, SubjectVisit> lastVisitBySubject = subjVisits
                            .stream()
                            .filter(subjectVisit -> subjectVisit.getVisit() != null && subjectVisit.getDate() != null)
                            .collect(Collectors.toMap(e -> e.getSubjectId(), e -> e, (sv1, sv2) ->
                                    sv1.getDate().compareTo(sv2.getDate()) > 0 ? sv1 : sv2
                            ));

                    Map<String, Set<String>> medicalHistory = populationRepository.getMedicalHistories(ds.getId())
                            .stream()
                            .collect(Collectors
                                    .groupingBy(SubjectMedicalHistories::getSubjectId,
                                            Collectors.mapping(SubjectMedicalHistories::getReportedTerm, Collectors.toSet())));

                    Map<String, List<String>> studySpecificFilters = populationRepository.getSubjectStudySpecificFilters(ds.getId())
                            .stream().collect(Collectors.groupingBy(SubjectStudySpecificFilters::getSubjectId,
                                    Collectors.mapping(SubjectStudySpecificFilters::getStudySpecificFilter, Collectors.toList())));

                    Map<String, Date> deathDateBySubject = deathRawData.stream().filter(e -> Objects.nonNull(e.getDateOfDeath()))
                            .collect(Collectors.groupingBy(DeathRaw::getSubjectId, Collectors.mapping(DeathRaw::getDateOfDeath,
                                    Collectors.minBy(Date::compareTo))))
                            .entrySet()
                            .stream()
                            .filter(e -> e.getValue().isPresent())
                            .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().get()));

                    Map<String, Subject.SubjectEthnicGroup> ethnicGroupBySubject = populationRepository.getSubjectEthnicGroup(dataset
                            .getId()).stream()
                            .collect(Collectors.toMap(Subject.SubjectEthnicGroup::getSubjectId, Function.identity()));

                    Map<String, List<Subject.SubjectVitalsInfo>> vitalsInfoBySubject = populationRepository.getSubjectVitalsInfo(dataset
                            .getId()).stream()
                            .collect(Collectors.groupingBy(Subject.SubjectVitalsInfo::getSubjectId));

                    Map<String, List<Subject.SubjectGroup>> groupBySubject = populationRepository.getSubjectGroup(dataset.getId())
                            .stream()
                            .collect(Collectors.groupingBy(Subject.SubjectGroup::getSubjectId, Collectors.toList()));

                    StudyInfo studyInfo = studyInfoRepository.getRawData(dataset.getId()).stream().findAny().orElse(StudyInfo.EMPTY);

                    rawData = rawData.stream().map(subject -> {
                        String subjectId = subject.getSubjectId();
                        Subject.SubjectBuilder builder = subject.toBuilder();
                        SubjectMapFieldsHolder subjectWithDrugInfo = subjectWithDrugInfoMap.get(subjectId);
                        builder = setDrugInfo(builder, subjectWithDrugInfo);

                        List<String> subjectVisits = attendedVisits.get(subjectId);
                        if (subjectVisits != null) {
                            subjectVisits = subjectVisits.stream()
                                    .map(VisitNumber::normalizeVisitNumberString)
                                    .collect(Collectors.toList());
                        } else {
                            subjectVisits = new ArrayList<>();
                        }

                        Set<String> subjectMedicalHistory = medicalHistory.get(subjectId);
                        List<String> subjectStudySpecificFilters = studySpecificFilters.get(subjectId);

                        String visitNumber = lastVisitBySubject.get(subjectId) != null
                                ? lastVisitBySubject.get(subjectId).getVisit()
                                : null;
                        visitNumber = VisitNumber.normalizeVisitNumberString(visitNumber);

                        builder.attendedVisitNumbers(subjectVisits)
                                .lastVisitNumber(visitNumber)
                                .medicalHistories(subjectMedicalHistory == null ? new HashSet<>() : subjectMedicalHistory)
                                .studySpecificFilters(subjectStudySpecificFilters == null ? new ArrayList<>() : subjectStudySpecificFilters)
                                .randomised(subject.getDateOfRandomisation() == null ? NO : YES)
                                .withdrawal(subject.getDateOfWithdrawal() == null ? NO : YES)
                                .dateOfDeath(deathDateBySubject.get(subjectId))
                                .deathFlag(deathDateBySubject.get(subjectId) == null ? NO : YES)
                                .durationOnStudy(getDurationOnStudy(subject.getDateOfWithdrawal(), deathDateBySubject.get(subjectId),
                                        subject.getLastEtlDate(), subject.getFirstTreatmentDate()))
                                .lastTreatmentDate(getLastTreatmentDate(drugDosesBySubject.get(subjectId), subject.getLastEtlDate()))
                                .ethnicGroup(getEthnicGroup(ethnicGroupBySubject.get(subjectId)))
                                .specifiedEthnicGroup(getSpecifiedEthnicGroup(ethnicGroupBySubject.get(subjectId)))
                                .age(getAge(subject.getFirstTreatmentDate(), subject.getDateOfBirth()))
                                .weight(getValueOfLastTest(vitalsInfoBySubject.get(subjectId), WEIGHT_TEST))
                                .height(getValueOfLastTest(vitalsInfoBySubject.get(subjectId), HEIGHT_TEST))
                                .studyInfo(studyInfo);
                        setGroupingsData(builder, groupBySubject.get(subjectId));
                        return builder.build();
                    }).collect(Collectors.toList());

                    return rawData;
                }
        );
    }

    @Override
    protected Class<Subject> rawDataClass() {
        return Subject.class;
    }

    private Subject.SubjectBuilder setDrugInfo(Subject.SubjectBuilder builder, SubjectMapFieldsHolder subjectWithDrugInfo) {
        if (subjectWithDrugInfo != null) {
            return builder.drugsDosed(subjectWithDrugInfo.getDrugsDosed())
                    .drugsDiscontinued(subjectWithDrugInfo.getDrugsDiscontinued())
                    .drugsMaxDoses(subjectWithDrugInfo.getDrugsMaxDoses())
                    .drugsRawMaxDoses(subjectWithDrugInfo.getDrugsRawMaxDoses())
                    .drugFirstDoseDate(subjectWithDrugInfo.getDrugFirstDoseDate())
                    .drugsMaxFrequencies(subjectWithDrugInfo.getDrugsMaxFrequencies())
                    .drugDiscontinuationMainReason(subjectWithDrugInfo.getDrugDiscontinuationMainReason())
                    .drugDiscontinuationDate(subjectWithDrugInfo.getDrugDiscontinuationDate())
                    .drugTotalDurationInclBreaks(subjectWithDrugInfo.getDrugTotalDurationInclBreaks())
                    .drugTotalDurationExclBreaks(subjectWithDrugInfo.getDrugTotalDurationExclBreaks());
        } else {
            builder.drugsDosed(new HashMap<>())
                    .drugsDiscontinued(new HashMap<>())
                    .drugsMaxDoses(new HashMap<>())
                    .drugsRawMaxDoses(new HashMap<>())
                    .drugFirstDoseDate(new HashMap<String, Date>())
                    .drugsMaxFrequencies(new HashMap<>())
                    .drugDiscontinuationMainReason(new HashMap<>())
                    .drugDiscontinuationDate(new HashMap<>())
                    .drugTotalDurationInclBreaks(new HashMap<>())
                    .drugTotalDurationExclBreaks(new HashMap<>());
        }
        return builder;
    }

    private void populateSubjectDrugsDosed(Map<String, SubjectMapFieldsHolder> subjectWithDrugInfoMap, DrugDosed drugDosed) {
        subjectWithDrugInfoMap.putIfAbsent(drugDosed.getSubjectId(), new SubjectMapFieldsHolder());
        SubjectMapFieldsHolder subject = subjectWithDrugInfoMap.get(drugDosed.getSubjectId());

        String drug = drugDosed.getDrug() == null ? "" : drugDosed.getDrug();
        subject.getDrugsDosed().put(drug, drugDosed.getDosed());
        subject.getDrugsMaxDoses().put(drug, drugDosed.getMaxDose());
        subject.getDrugsRawMaxDoses().put(drug, drugDosed.getRawMaxDose());
        subject.getDrugFirstDoseDate().put(drug, drugDosed.getFirstDoseDate());
        subject.getDrugLastDoseDate().put(drug, drugDosed.getLastDoseDate());
        subject.getDrugsMaxFrequencies().put(drug, drugDosed.getMaxFrequency());
        subject.getDrugTotalDurationInclBreaks().put(drug, drugDosed.getTotalDurationInclBreaks());
        subject.getDrugTotalDurationExclBreaks().put(drug, drugDosed.getTotalDurationExclBreaks());
    }

    private void populateSubjectDrugsDisc(Map<String, SubjectMapFieldsHolder> subjectWithDrugInfoMap, DrugDiscontinued drugDiscontinued) {
        subjectWithDrugInfoMap.putIfAbsent(drugDiscontinued.getSubjectId(), new SubjectMapFieldsHolder());
        SubjectMapFieldsHolder subject = subjectWithDrugInfoMap.get(drugDiscontinued.getSubjectId());

        String drug = drugDiscontinued.getDrug() == null ? "" : drugDiscontinued.getDrug();

        subject.getDrugsDiscontinued().put(drug, drugDiscontinued.getDiscontinued());
        subject.getDrugDiscontinuationMainReason().put(drug, drugDiscontinued.getDiscReason());
        subject.getDrugDiscontinuationDate().put(drug, drugDiscontinued.getDiscDate());
    }

    private Collection<Subject> mergeDatasets(Datasets datasets, Collection<Subject> events) {
        Collection<Subject> eventsToMerge = datasets.getDatasets().size() > 1 ? mergeSubjectCodes(events) : events;
        return mergeDatasetDrugs(eventsToMerge);
    }

    /**
     * Returns collection of {@link Subject}.Populate with missed drug drug related map field.
     *
     * @param events collection of {@link Subject}
     * @return collection of {@link Subject}
     */
    private Collection<Subject> mergeDatasetDrugs(Collection<Subject> events) {
        Set<String> allDrugs = events.stream().flatMap(e -> e.getDrugsDosed().keySet().stream()).collect(Collectors.toSet());
        List<Subject> subjects = new ArrayList<>(events);
        events.forEach(subject -> {

            if (!subject.getDrugsDosed().keySet().containsAll(allDrugs)) {
                Map<String, String> newDrugDosedMap = new HashMap<>();
                Map<String, String> newDrugsMaxDoses = new HashMap<>();
                Map<String, Double> newDrugsRawMaxDoses = new HashMap<>();
                Map<String, String> newDrugsDiscontinuedMap = new HashMap<>();
                Map<String, String> newDrugsMaxFrequencies = new HashMap<>();
                Map<String, Date> newDrugDiscontinuationDate = new HashMap<>();
                Map<String, String> newDrugDiscontinuationMainReason = new HashMap<>();

                Sets.difference(allDrugs, subject.getDrugsDosed().keySet()).forEach(d -> {
                    newDrugDosedMap.put(d, "No");
                    newDrugsDiscontinuedMap.put(d, "No");
                    newDrugsMaxDoses.put(d, null);
                    newDrugsRawMaxDoses.put(d, null);
                    newDrugsMaxFrequencies.put(d, null);
                    newDrugDiscontinuationDate.put(d, null);
                    newDrugDiscontinuationMainReason.put(d, null);
                });
                newDrugDosedMap.putAll(subject.getDrugsDosed());
                newDrugsDiscontinuedMap.putAll(subject.getDrugsDiscontinued());
                newDrugsMaxDoses.putAll(subject.getDrugsMaxDoses());
                newDrugsRawMaxDoses.putAll(subject.getDrugsRawMaxDoses());
                newDrugsMaxFrequencies.putAll(subject.getDrugsMaxFrequencies());
                newDrugDiscontinuationDate.putAll(subject.getDrugDiscontinuationDate());
                newDrugDiscontinuationMainReason.putAll(subject.getDrugDiscontinuationMainReason());

                Subject newSubject = subject.toBuilder()
                        .drugsDosed(newDrugDosedMap)
                        .drugsDiscontinued(newDrugsDiscontinuedMap)
                        .drugsMaxDoses(newDrugsMaxDoses)
                        .drugsRawMaxDoses(newDrugsRawMaxDoses)
                        .drugsMaxFrequencies(newDrugsMaxFrequencies)
                        .drugDiscontinuationDate(newDrugDiscontinuationDate)
                        .drugDiscontinuationMainReason(newDrugDiscontinuationMainReason).build();

                subjects.remove(subject);
                subjects.add(newSubject);
            }
        });
        return subjects;
    }

    private Collection<Subject> mergeSubjectCodes(Collection<Subject> events) {
        return events.stream()
                .map(subject -> subject.toBuilder()
                        .merged(true)
                        .build())
                .collect(Collectors.toList());
    }

    private Integer getDurationOnStudy(Date withdrawalDate, Date deathDate, Date studyDateLastUploaded, Date firstDoseDate) {
        return daysBetween(firstDoseDate, firstNonNull(withdrawalDate, deathDate, studyDateLastUploaded)).orElse(0) + 1;
    }

    private Date getLastTreatmentDate(List<DrugDoseRaw> doses, Date studyDateLastUploaded) {
        return doses == null ? studyDateLastUploaded
                : doses.stream()
                .filter(e -> e.getDose() > 0)
                .map(e -> e.getEndDate() == null ? studyDateLastUploaded : e.getEndDate())
                .max(Date::compareTo)
                .orElse(studyDateLastUploaded);
    }

    private String getEthnicGroup(Subject.SubjectEthnicGroup ethnicGroup) {
        return ethnicGroup == null ? null : ethnicGroup.getEthnicGroup();
    }

    private String getSpecifiedEthnicGroup(Subject.SubjectEthnicGroup ethnicGroup) {
        return ethnicGroup == null ? null : ethnicGroup.getSpecifiedEthnicGroup();
    }

    private Integer getAge(Date firstTreatmentDate, Date dateOfBirth) {
        OptionalInt monthsBetween = monthsBetween(dateOfBirth, firstTreatmentDate);
        return monthsBetween.isPresent() ? monthsBetween.getAsInt() / MONTHS_IN_YEAR : null;
    }

    private Double getValueOfLastTest(List<Subject.SubjectVitalsInfo> vitalsInfo, String testName) {
        return vitalsInfo == null ? null : vitalsInfo.stream().filter(e -> e.getTestName().equalsIgnoreCase(testName))
                .max(Comparator.comparing(Subject.SubjectVitalsInfo::getTestDate, Comparator.nullsFirst(Comparator.naturalOrder())))
                .map(Subject.SubjectVitalsInfo::getTestValue).orElse(null);
    }

    private void setGroupingsData(Subject.SubjectBuilder subjectBuilder, List<Subject.SubjectGroup> subjectGroups) {
        Optional<Subject.SubjectGroup> doseGroup = findGroup(subjectGroups, GroupType.DOSE);
        Optional<Subject.SubjectGroup> otherGroup = findGroup(subjectGroups, GroupType.NONE);

        if (!otherGroup.isPresent()) {
            String doseCohortName = doseGroup.map(this::groupToName).orElse(DEFAULT_GROUP);
            String doseGroupingName = doseGroup.map(Subject.SubjectGroup::getGroupingName).orElse(COHORT);
            subjectBuilder.doseCohort(doseCohortName).doseGrouping(doseGroupingName);
            if (!DEFAULT_GROUP.equals(doseCohortName) || !COHORT.equals(doseGroupingName)) {
                subjectBuilder.otherCohort(DEFAULT_GROUP).otherGrouping(COHORT);
            }
        } else if (!doseGroup.isPresent()) {
            String otherCohortName = otherGroup.map(this::groupToName).orElse(DEFAULT_GROUP);
            String otherGroupingName = otherGroup.map(Subject.SubjectGroup::getGroupingName).orElse(COHORT);
            subjectBuilder.otherCohort(otherCohortName).otherGrouping(otherGroupingName);
            if (!DEFAULT_GROUP.equals(otherCohortName) || !COHORT.equals(otherGroupingName)) {
                subjectBuilder.doseCohort(DEFAULT_GROUP).doseGrouping(COHORT);
            }
        } else {
            subjectBuilder.otherCohort(groupToName(otherGroup.get()))
                    .doseCohort(groupToName(doseGroup.get()))
                    .otherGrouping(otherGroup.get().getGroupingName())
                    .doseGrouping(doseGroup.get().getGroupingName());
        }
    }

    private Optional<Subject.SubjectGroup> findGroup(List<Subject.SubjectGroup> groups, GroupType groupType) {
        return Optional.ofNullable(groups)
                .flatMap(gs -> gs.stream()
                        .filter(e -> e.getGroupType().equals(groupType))
                        .findFirst());
    }

    private String groupToName(Subject.SubjectGroup g) {
        String name = firstNonNull(g.getGroupPreferredName(), g.getGroupName(), g.getGroupDefaultName(), DEFAULT_GROUP);
        Integer groupIndex = g.getGroupIndex();
        return groupIndex == null ? name : String.format("(%s)%s", getGroupCharacter(groupIndex), name);
    }

    private List<DrugDosed> buildDrugDosed(Subject subject, List<DrugDoseRaw> drugDoseRaws, Set<String> studyDrugs) {
        Map<String, List<DrugDoseRaw>> dosesByDrug = drugDoseRaws == null ? Collections.emptyMap() : drugDoseRaws.stream().filter(e -> e.getDose() > 0)
                .collect(Collectors.groupingBy(e -> e.getDrug() == null ? "" : e.getDrug()));

        List<DrugDosed> result = new ArrayList<>();
        studyDrugs.forEach(drug -> {
            List<DrugDoseRaw> drugDoses = drug == null ? dosesByDrug.get("") : dosesByDrug.get(drug);
            if (drugDoses != null && !drugDoses.isEmpty()) {
                DrugDoseRaw maxDrugDoseRaw = drugDoses.stream().max(Comparator.comparing(DrugDoseRaw::getDose)).get();
                List<Range<Date>> dateRanges = getRangesForDrug(drugDoses, subject.getLastEtlDate());
                result.add(DrugDosed.builder()
                        .subjectId(subject.getSubjectId())
                        .drug(drug)
                        .dosed("Yes")
                        .firstDoseDate(drugDoses.stream().map(DrugDoseRaw::getStartDate).min(Comparator.nullsLast(Comparator.naturalOrder())).orElse(null))
                        .maxDose(getMaxDoseWithUnit(maxDrugDoseRaw))
                        .rawMaxDose(maxDrugDoseRaw.getDose())
                        .maxFrequency(maxDrugDoseRaw.getFrequencyName())
                        .totalDurationInclBreaks(getTotalDurationInclBreaks(dateRanges))
                        .totalDurationExclBreaks(getTotalDurationExclBreaks(dateRanges)).build());
            } else {
                result.add(DrugDosed.builder().subjectId(subject.getSubjectId()).drug(drug).dosed("No").build());
            }
        });
        return result;
    }

    private String getMaxDoseWithUnit(DrugDoseRaw maxDrugDose) {
        String dose = String.valueOf(maxDrugDose.getDose());
        dose = dose.endsWith(".0") ? dose.replace(".0", "") : dose;
        String doseUnit = maxDrugDose.getDoseUnit();
        return dose + (doseUnit == null || doseUnit.matches("\\d+") ? "" : " " + doseUnit);
    }

    //Merge two date ranges if the start date of the second period is less than or equal to the end date of the first
    //for the same drug. For example if the first period is 01.01.2019 - 05.01.2019 and the second is 05.01.2019 - 07.01.2019
    //this method will return 01.01.2109 - 07.01.2019 period.
    private List<Range<Date>> getRangesForDrug(List<DrugDoseRaw> doses, Date studyDateLastUploaded) {
        List<Range<Date>> originalRanges = doses.stream()
                .map(e -> Range.closed(truncLocalTime(e.getStartDate()),
                        truncLocalTime(e.getEndDate() == null ? studyDateLastUploaded : e.getEndDate())))
                .sorted(comparing(Range::lowerEndpoint)).collect(Collectors.toList());

        List<Range<Date>> alignedRanges = new ArrayList<>();
        alignedRanges.add(Range.closed(originalRanges.get(0).lowerEndpoint(), originalRanges.get(0).upperEndpoint()));
        for (int i = 1; i < originalRanges.size(); i++) {
            Range<Date> range = originalRanges.get(i);
            Range<Date> previousRange = alignedRanges.get(i - 1);
            if (!previousRange.upperEndpoint().before(range.lowerEndpoint())) {
                alignedRanges.add(Range.closed(previousRange.lowerEndpoint(),
                        range.upperEndpoint().before(previousRange.upperEndpoint()) ? previousRange.upperEndpoint() : range.upperEndpoint()));
            } else {
                alignedRanges.add(Range.closed(range.lowerEndpoint(), range.upperEndpoint()));
            }
        }
        return new ArrayList<>(alignedRanges.stream()
                .collect(Collectors.toMap(Range::lowerEndpoint, Function.identity(), BinaryOperator.maxBy(comparing(Range::upperEndpoint))))
                .values());
    }

    private Integer getTotalDurationInclBreaks(List<Range<Date>> dateRanges) {
        Date minDate = dateRanges.stream().map(Range::lowerEndpoint).min(Date::compareTo).get();
        Date maxDate = dateRanges.stream().map(Range::upperEndpoint).max(Date::compareTo).get();
        OptionalInt duration = daysBetween(minDate, maxDate);
        return duration.isPresent() ? duration.getAsInt() + 1 : null;
    }

    private Integer getTotalDurationExclBreaks(List<Range<Date>> dateRanges) {
        return dateRanges.stream().map(e -> daysBetween(e.lowerEndpoint(), e.upperEndpoint()).getAsInt() + 1).reduce(0, Integer::sum);
    }

    private List<DrugDiscontinued> buildDrugDiscontinued(Subject subject, List<DoseDiscRaw> doseDiscRaws, Set<String> studyDrugs) {
        Map<String, List<DoseDiscRaw>> doseDiscsByDrug = doseDiscRaws == null ? Collections.emptyMap() : doseDiscRaws.stream()
                .collect(Collectors.groupingBy(e -> e.getStudyDrug() == null ? "" : e.getStudyDrug()));

        List<DrugDiscontinued> result = new ArrayList<>();
        studyDrugs.forEach(drug -> {
            List<DoseDiscRaw> doseDiscs = drug == null ? doseDiscsByDrug.get("") : doseDiscsByDrug.get(drug);
            if (doseDiscs != null && !doseDiscs.isEmpty()) {
                DoseDiscRaw lastDoseDisc = doseDiscs.stream().max(Comparator.comparing(DoseDiscRaw::getDiscDate)).get();
                result.add(DrugDiscontinued.builder().subjectId(subject.getSubjectId())
                        .drug(drug)
                        .discontinued("Yes")
                        .discDate(lastDoseDisc.getDiscDate())
                        .discReason(lastDoseDisc.getDiscReason()).build());
            } else {
                result.add(DrugDiscontinued.builder().subjectId(subject.getSubjectId()).drug(drug).discontinued("No").build());
            }
        });
        return result;
    }
}
