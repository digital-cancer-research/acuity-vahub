
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

import com.acuity.visualisations.rawdatamodel.dao.DrugDoseRepository;
import com.acuity.visualisations.rawdatamodel.dataproviders.common.SubjectAwareDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.vo.AeRaw;
import com.acuity.visualisations.rawdatamodel.vo.DrugDoseRaw;
import com.acuity.visualisations.rawdatamodel.vo.ExacerbationRaw;
import com.acuity.visualisations.rawdatamodel.vo.StudyInfo;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.timeline.dose.PeriodType;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.DoseDisc;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.DrugDose;
import com.acuity.va.security.acl.domain.Dataset;
import com.acuity.va.security.acl.domain.Datasets;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import one.util.streamex.StreamEx;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.keyvalue.MultiKey;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Component
public class DrugDoseDatasetsDataProvider extends SubjectAwareDatasetsDataProvider<DrugDoseRaw, DrugDose> {

    @Autowired
    private AeIncidenceDatasetsDataProvider aeIncidenceDatasetsDataProvider;

    @Autowired
    private DrugDoseRepository drugDoseRepository;

    @Autowired
    private ExacerbationDatasetsDataProvider exacerbationDatasetsDataProvider;

    @Autowired
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;

    @Autowired
    private DoseDiscDatasetsDataProvider doseDiscDatasetsDataProvider;

    @Autowired
    private StudyInfoDataProvider studyInfoDataProvider;

    private static final Comparator<? super Comparable> NULLS_FIRST_NATURAL_ORDER_COMPARATOR = Comparator.nullsFirst(Comparator.naturalOrder());

    private static final Comparator<DrugDoseIntermediate> SEPARATING_FOR_NEW_INTERVAL_MARK_COMPARATOR = Comparator.comparing(DrugDoseIntermediate::getSubjectId)
            .thenComparing(DrugDoseIntermediate::getDrug, NULLS_FIRST_NATURAL_ORDER_COMPARATOR)
            .thenComparing(DrugDoseIntermediate::getPeriodType, NULLS_FIRST_NATURAL_ORDER_COMPARATOR)
            .thenComparing(DrugDoseIntermediate::getDose)
            .thenComparing(DrugDoseIntermediate::getDoseUnit, NULLS_FIRST_NATURAL_ORDER_COMPARATOR)
            .thenComparing(DrugDoseIntermediate::getFrequencyRank, NULLS_FIRST_NATURAL_ORDER_COMPARATOR);

    private static final Comparator<DrugDoseIntermediate> SUBJECT_AND_DRUG_COMPARATOR = Comparator.comparing(DrugDoseIntermediate::getSubjectId)
            .thenComparing(DrugDoseIntermediate::getDrug);

    private static final Comparator<DrugDoseIntermediate> SUBJECT_DRUG_AND_START_DATE_COMPARATOR = SUBJECT_AND_DRUG_COMPARATOR
            .thenComparing(DrugDoseIntermediate::getStartDate);

    private static final Comparator<Map.Entry<DrugDoseIntermediate, Integer>> SUBJECT_DRUG_AND_INTERVAL_RANK_COMPARATOR = Comparator
            .comparing((Map.Entry<DrugDoseIntermediate, Integer> entry) -> entry.getKey().getSubjectId(), NULLS_FIRST_NATURAL_ORDER_COMPARATOR)
            .thenComparing(entry -> entry.getKey().getDrug(), NULLS_FIRST_NATURAL_ORDER_COMPARATOR)
            .thenComparing(Map.Entry::getValue, NULLS_FIRST_NATURAL_ORDER_COMPARATOR);

    private static final Comparator<DrugDoseIntermediate> START_DATE_AND_PERIOD_TYPE_COMPARATOR = Comparator.comparing(DrugDoseIntermediate::getStartDate)
            .thenComparing(DrugDoseIntermediate::getPeriodType);

    /*
    This method copies the logic of former doses precalculation SQL script from AdminUI.
    Now the script itself is removed along with precalc tables too,
    but you can find it if needed in DrugDoseLegacyRepository class. This one is
    used only for test purposes
     */
    public Collection<DrugDoseIntermediate> getJavaPrecalculatedData(Dataset dataset) {

        Collection<ExacerbationRaw> exacerbationRaws = exacerbationDatasetsDataProvider.getData(dataset);
        Collection<Subject> subjects = populationDatasetsDataProvider.getData(dataset);

        Collection<String> subjectsWithExacerbationsIds = exacerbationRaws.stream()
                .map(ExacerbationRaw::getSubjectId)
                .collect(Collectors.toList());

        Collection<String> subjectsWithFirstDose = subjects.stream()
                .filter((Subject subject) -> !subject.getDrugFirstDoseDate().isEmpty())
                .map(Subject::getSubjectId)
                .collect(Collectors.toList());

        // "demo"
        Collection<String> subjectsOfInterest = CollectionUtils.union(subjectsWithExacerbationsIds, subjectsWithFirstDose);

        Collection<DrugDoseIntermediate> dosesOfInterests = drugDoseRepository.getRawData(dataset.getId()).stream()
                .filter(dose -> subjectsOfInterest.contains(dose.getSubjectId()))
                .map(dose -> {
                            Optional<DrugDoseRaw> doseOptional = Optional.of(dose).filter(d -> d.getDose() > 0);
                            return DrugDoseIntermediate.builder()
                                    .subjectId(dose.getSubjectId())
                                    .drug(dose.getDrug())
                                    .startDate(dose.getStartDate())
                                    .endDate(dose.getEndDate())
                                    .periodType(doseOptional.map(d -> PeriodType.ACTIVE.name()).orElse(PeriodType.INACTIVE.name()))
                                    .dose(doseOptional.map(DrugDoseRaw::getDose).orElse(0.0d))
                                    .doseUnit(doseOptional.map(DrugDoseRaw::getDoseUnit).orElse(null))
                                    .frequencyName(doseOptional.map(DrugDoseRaw::getFrequencyName).orElse(null))
                                    .frequencyRank(doseOptional.map(DrugDoseRaw::getFrequencyRank).orElse(null))
                                    .build();
                        }
                )
                .collect(Collectors.toList());

        Collection<DoseDisc> discsOfInterest = doseDiscDatasetsDataProvider.loadData(Datasets.toAcuityDataset(dataset.getId())).stream()
                .filter(disc -> subjectsOfInterest.contains(disc.getSubject().getId()))
                .collect(Collectors.toList());

        Date studyLastUpdated = studyInfoDataProvider.getData(dataset).stream()
                .findAny()
                .map(StudyInfo::getLastUpdatedDate)
                .orElseThrow(() -> new IllegalStateException("It's just impossible!"));

        Collection<DrugDoseIntermediate> dosesOfInterestConverted = dosesOfInterests.stream()
                .map(dose -> {
                    Optional<DrugDoseIntermediate> doseOptional = Optional.of(dose).filter(d -> d.getDose() > 0);
                    return DrugDoseIntermediate.builder()
                            .subjectId(dose.getSubjectId())
                            .drug(dose.getDrug())
                            .startDate(dose.getStartDate())
                            .periodType(doseOptional.map(d -> PeriodType.ACTIVE.name()).orElse(PeriodType.INACTIVE.name()))
                            .dose(doseOptional.map(DrugDoseIntermediate::getDose).orElse(0.0d))
                            .doseUnit(doseOptional.map(DrugDoseIntermediate::getDoseUnit).orElse(null))
                            .frequencyName(doseOptional.map(DrugDoseIntermediate::getFrequencyName).orElse(null))
                            .frequencyRank(doseOptional.map(DrugDoseIntermediate::getFrequencyRank).orElse("0"))
                            .build();
                }).collect(Collectors.toList());

        Map<MultiKey<Object>, DoseDisc> discsBySubjectDrugAndDate = StreamEx.of(discsOfInterest)
                .mapToEntry(
                        disc -> new MultiKey<Object>(disc.getSubjectId(), disc.getStudyDrug(), disc.getDiscDate()),
                        Function.identity()
                ).toMap();

        Collection<DrugDoseIntermediate> dosesAfterDoses = dosesOfInterests.stream()
                .filter(dose -> dose.getEndDate() != null)
                .map(dose -> {
                    MultiKey<Object> key = new MultiKey<>(dose.getSubjectId(), dose.getDrug(), dose.getEndDate());
                    DoseDisc disc = discsBySubjectDrugAndDate.get(key);
                    return DrugDoseIntermediate.builder()
                            .subjectId(dose.getSubjectId())
                            .drug(dose.getDrug())
                            .startDate(dose.getEndDate())   // sic!
                            .periodType(disc != null ? PeriodType.DISCONTINUED.name() : PeriodType.INACTIVE.name())
                            .dose(0.0d)
                            .frequencyRank("0")
                            .build();
                })
                .collect(Collectors.toList());

        Collection<DrugDoseIntermediate> discBasedDoses = discsOfInterest.stream()
                .map(disc -> DrugDoseIntermediate.builder()
                        .subjectId(disc.getSubjectId())
                        .drug(disc.getStudyDrug())
                        .startDate(disc.getDiscDate())
                        .periodType(PeriodType.DISCONTINUED.name())
                        .dose(0.0d)
                        .frequencyRank("0")
                        .build())
                .collect(Collectors.toList());

        return StreamEx.of(
                dosesOfInterestConverted.stream(),
                discBasedDoses.stream(),
                dosesAfterDoses.stream())
                .flatMap(Function.identity())
                .distinct()
                .peek(dose -> dose.setEndDate(null))
                .sorted(SUBJECT_AND_DRUG_COMPARATOR)
                .groupRuns((dose1, dose2) -> SUBJECT_AND_DRUG_COMPARATOR.compare(dose1, dose2) == 0)
                .peek(doses -> StreamEx.of(doses)
                        .sorted(START_DATE_AND_PERIOD_TYPE_COMPARATOR)
                        .forPairs((previousDose, nextDose) -> previousDose.setEndDate(nextDose.getStartDate())))
                .flatMap(Collection::stream)
                .sorted(SUBJECT_DRUG_AND_START_DATE_COMPARATOR) // "ticks"
                .filter(Objects::nonNull)
                .sorted(SEPARATING_FOR_NEW_INTERVAL_MARK_COMPARATOR)
                .groupRuns((dose1, dose2) -> SEPARATING_FOR_NEW_INTERVAL_MARK_COMPARATOR.compare(dose1, dose2) == 0)
                .flatMap(doses -> {
                    List<DrugDoseIntermediate> orderedDoses = doses.stream()
                            .sorted(Comparator.comparing(DrugDoseIntermediate::getStartDate))
                            .collect(toList());

                    Date prevMaxEndDate = null;
                    List<Map.Entry<DrugDoseIntermediate, Integer>> entries = new ArrayList<>(doses.size());
                    for (DrugDoseIntermediate dose : orderedDoses) {
                        int doseInterval;
                        if (prevMaxEndDate != null && dose.getStartDate() != null && !dose.getStartDate().after(prevMaxEndDate)) {
                            doseInterval = 0;
                        } else {
                            doseInterval = 1;
                        }
                        entries.add(Pair.of(dose, doseInterval));
                        Date endDate = dose.getEndDate();
                        if (prevMaxEndDate == null || (endDate != null && endDate.after(prevMaxEndDate))) {
                            prevMaxEndDate = endDate;
                        }
                    }
                    return entries.stream();
                })  // "newIntervalMarks"
                .sorted(Map.Entry.comparingByKey(SUBJECT_AND_DRUG_COMPARATOR))
                .groupRuns((e1, e2) -> Map.Entry.<DrugDoseIntermediate, Integer>comparingByKey(SUBJECT_AND_DRUG_COMPARATOR).compare(e1, e2) == 0)
                .flatMap(doseEntries -> {
                    List<Map.Entry<DrugDoseIntermediate, Integer>> orderedDoses = doseEntries.stream()
                            .sorted(Map.Entry.comparingByKey(START_DATE_AND_PERIOD_TYPE_COMPARATOR))
                            .collect(toList());

                    int intervalRank = 0;
                    List<Map.Entry<DrugDoseIntermediate, Integer>> res = new ArrayList<>(orderedDoses.size());
                    for (Map.Entry<DrugDoseIntermediate, Integer> doseEntry : orderedDoses) {
                        intervalRank += doseEntry.getValue();
                        res.add(Pair.of(doseEntry.getKey(), intervalRank));
                    }
                    return res.stream();
                })  // "doseIntervalRanks"
                .sorted(SUBJECT_DRUG_AND_INTERVAL_RANK_COMPARATOR)
                .groupRuns((de1, de2) -> SUBJECT_DRUG_AND_INTERVAL_RANK_COMPARATOR.compare(de1, de2) == 0)
                .flatMap(doseEntries -> {
                    Date startDate = doseEntries.stream()
                            .map(Map.Entry::getKey)
                            .map(DrugDoseIntermediate::getStartDate).min(Comparator.naturalOrder()).orElse(null);
                    Date endDate = doseEntries.stream()
                            .map(Map.Entry::getKey)
                            .map(DrugDoseIntermediate::getEndDate)
                            .filter(Objects::nonNull)
                            .max(Comparator.naturalOrder()).orElse(studyLastUpdated);   // differs "result" and "periods"; no separate "periods" written in Java
                    return doseEntries.stream()
                            .map(Map.Entry::getKey)
                            .peek(dose -> {
                                dose.setStartDate(startDate);
                                dose.setEndDate(endDate);
                            });
                })
                .distinct()
                .sorted(SUBJECT_DRUG_AND_START_DATE_COMPARATOR
                        .thenComparing(DrugDoseIntermediate::getPeriodType))
                .collect(toList());
    }

    @Override
    protected Collection<DrugDoseRaw> getData(Dataset ds) {
        Collection<DrugDoseRaw> drugDoses = drugDoseRepository.getRawData(ds.getId());
        if (ds.thisAcuityType()) {
            Map<AeKey, List<String>> aePtsMap = aeIncidenceDatasetsDataProvider.getData(ds).stream()
                    .collect(Collectors.groupingBy(ae -> new AeKey(ae.getSubjectId(), ae.getAeNumber()),
                            Collectors.mapping(AeRaw::getPt, Collectors.toList())));

            Map<String, List<Integer>> aeNumCausedActionTaken =
                    drugDoseRepository.getAeNumCausedActionTaken(ds.getId()).stream()
                            .collect(Collectors.groupingBy(DrugDoseRaw.AeNumCausedActionTaken::getDrugDoseId,
                                    Collectors.mapping(DrugDoseRaw.AeNumCausedActionTaken::getAeNumCausedActionTaken,
                                            Collectors.toList())));

            Map<String, List<String>> aePtCausedActionTaken =
                    drugDoseRepository.getAePtCausedActionTaken(ds.getId()).stream().filter(e -> e.getAeNum() != null)
                            .collect(Collectors.toMap(DrugDoseRaw.AePtCausedActionTaken::getDrugDoseId,
                                    ae -> Optional.ofNullable(aePtsMap.get(new AeKey(ae.getSubjectId(), ae.getAeNum())))
                                            .orElse(new ArrayList<>()), (v1, v2) -> {
                                        v1.addAll(v2);
                                        return v1;
                                    }, HashMap::new));

            Map<String, List<Integer>> aeNumCausedTreatmentCycleDelayed =
                    drugDoseRepository.getAeNumCausedTreatmentCycleDelayed(ds.getId()).stream()
                            .collect(Collectors.groupingBy(DrugDoseRaw.AeNumCausedTreatmentCycleDelayed::getDrugDoseId,
                                    Collectors.mapping(DrugDoseRaw.AeNumCausedTreatmentCycleDelayed::getAeNumCausedTreatmentCycleDelayed,
                                            Collectors.toList())));

            Map<String, List<String>> aePtCausedTreatmentCycleDelayed =
                    drugDoseRepository.getAePtCausedTreatmentCycleDelayed(ds.getId()).stream().filter(e -> e.getAeNumDel() != null)
                            .collect(Collectors.toMap(DrugDoseRaw.AePtCausedTreatmentCycleDelayed::getDrugDoseId,
                                    ae -> Optional.ofNullable(aePtsMap.get(new AeKey(ae.getSubjectId(), ae.getAeNumDel())))
                                            .orElse(new ArrayList<>()), (v1, v2) -> {
                                        v1.addAll(v2);
                                        return v1;
                                    }, HashMap::new));

            return drugDoses.stream().map(dose -> {
                        DrugDoseRaw.DrugDoseRawBuilder builder = dose.toBuilder();
                        builder
                                .clearAeNumCausedActionTaken()
                                .clearAeNumCausedTreatmentCycleDelayed()
                                .clearAePtCausedActionTaken()
                                .clearAePtCausedTreatmentCycleDelayed();
                        Optional<List<Integer>> aeNumsCausedActionTaken = Optional.ofNullable(aeNumCausedActionTaken.get(dose.getId()));
                        Optional<List<String>> aePtsCausedActionTaken = Optional.ofNullable(aePtCausedActionTaken.get(dose.getId()));
                        Optional<List<Integer>> aeNumsCausedTreatmentCycleDelayed = Optional.ofNullable(aeNumCausedTreatmentCycleDelayed.get(dose.getId()));
                        Optional<List<String>> aePtsCausedTreatmentCycleDelayed = Optional.ofNullable(aePtCausedTreatmentCycleDelayed.get(dose.getId()));
                        aeNumsCausedActionTaken.ifPresent(builder::aeNumCausedActionTaken);
                        aePtsCausedActionTaken.ifPresent(builder::aePtCausedActionTaken);
                        aeNumsCausedTreatmentCycleDelayed.ifPresent(builder::aeNumCausedTreatmentCycleDelayed);
                        aePtsCausedTreatmentCycleDelayed.ifPresent(builder::aePtCausedTreatmentCycleDelayed);
                        return builder.build();
                    }
            ).collect(Collectors.toList());
        } else {
            return drugDoses;
        }
    }

    @Override
    protected DrugDose getWrapperInstance(DrugDoseRaw event, Subject subject) {
        return new DrugDose(event, subject);
    }

    @Override
    protected Class<DrugDoseRaw> rawDataClass() {
        return DrugDoseRaw.class;
    }

    @SneakyThrows
    public Collection<DrugDose> loadDosesForTumourColumnRangeService(Datasets datasets) {
        List<DrugDoseRaw> events = datasets.getDatasets().stream()
                .flatMap(dataset -> getJavaPrecalculatedData(dataset).stream())
                .map(dose -> {
                    Optional<DrugDoseIntermediate> intmOptional = Optional.of(dose).filter(d -> d.getDose() > 0);
                    return DrugDoseRaw.builder()
                            .id(UUID.randomUUID().toString())
                            .subjectId(dose.getSubjectId())
                            .drug(dose.getDrug())
                            .startDate(dose.getStartDate())
                            .periodType(intmOptional.map(d -> PeriodType.ACTIVE.name()).orElse(PeriodType.INACTIVE.name()))
                            .dose(intmOptional.map(DrugDoseIntermediate::getDose).orElse(0.0d))
                            .doseUnit(intmOptional.map(DrugDoseIntermediate::getDoseUnit).orElse(null))
                            .frequencyName(intmOptional.map(DrugDoseIntermediate::getFrequencyName).orElse(null))
                            .frequencyRank(intmOptional.map(DrugDoseIntermediate::getFrequencyRank).orElse(null))
                            .build();
                })
                .collect(Collectors.toList());

        return wrap(datasets, events);
    }

    @Data
    @EqualsAndHashCode
    @AllArgsConstructor
    @NoArgsConstructor
    private static class AeKey {
        private String subjectId;
        private Integer aeNum;
    }

    @Builder
    @Data
    @AllArgsConstructor
    public static class DrugDoseIntermediate {
        private String subjectId;
        private String drug;
        private Date startDate;
        private Date endDate;
        private String periodType;
        private Double dose;
        private String doseUnit;
        private String frequencyName;
        private String frequencyRank;

        @SuppressWarnings("java:S107")
        public DrugDoseIntermediate(String subjectId, String drug, Timestamp startDate,
                                    Timestamp endDate, String periodType, BigDecimal dose, String doseUnit,
                                    String frequencyName, String frequencyRank) {
            this.subjectId = subjectId;
            this.drug = drug;
            this.startDate = startDate;
            this.endDate = endDate;
            this.periodType = periodType;
            this.dose = dose.doubleValue();
            this.doseUnit = doseUnit;
            this.frequencyName = frequencyName;
            this.frequencyRank = frequencyRank;
        }
    }
}
