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

package com.acuity.visualisations.rawdatamodel.vo;

import com.acuity.visualisations.rawdatamodel.util.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Singular;
import lombok.ToString;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.acuity.visualisations.rawdatamodel.util.Constants.YES;

@ToString
@Getter
@Builder(toBuilder = true)
@EqualsAndHashCode(of = "subjectId")
@AllArgsConstructor
@NoArgsConstructor
@AcuityEntity(version = 20)
public final class Subject implements HasStringId, HasSubject, Serializable {

    public static final String COHORT_OTHER_FIELD_NAME = "cohortOther";

    public enum Attributes implements GroupByOption<Subject> {

        ID(EntityAttribute.attribute("id", Subject::getSubjectId)),
        // pdm_pat_subject = E5707029
        SUBJECT(EntityAttribute.attribute("subjectCode", Subject::getSubjectCode)),
        //  pdm_pat_id = 4cb79c5236ad46c8897e221dfc617342
        SUBJECT_ID(EntityAttribute.attribute("subjectId", Subject::getSubjectId)),
        STUDY_CODE(EntityAttribute.attribute("clinicalStudyCode", Subject::getClinicalStudyCode)),
        STUDY_NAME(EntityAttribute.attribute("clinicalStudyName", Subject::getClinicalStudyName)),
        DATASET_CODE(EntityAttribute.attribute("datasetCode", Subject::getDatasetCode)),
        DATASET_NAME(EntityAttribute.attribute("datasetName", Subject::getDatasetName)),
        STUDY_PART(EntityAttribute.attribute("studyPart", Subject::getStudyPart)),
        ATTENDED_VISIT_NUMBERS(EntityAttribute.attribute("attendedVisitNumbers", Subject::getAttendedVisitNumbers, String.class)),
        ATTENDED_ANALYSIS_VISITS(EntityAttribute.attribute("attendedAnalysisVisits", Subject::getAttendedAnalysisVisits)),
        DURATION_ON_STUDY(EntityAttribute.attribute("durationOnStudy", Subject::getDurationOnStudy)),
        RANDOMISED(EntityAttribute.attribute("randomised", Subject::getRandomised)),
        DATE_OF_RANDOMISATION(EntityAttribute.attribute("dateOfRandomisation", Subject::getDateOfRandomisation)),
        FIRST_TREATMENT_DATE(EntityAttribute.attribute("firstTreatmentDate", Subject::getFirstTreatmentDate)) {
            @Override
            public EntityAttribute<Subject> getAttribute(Params params) {
                return EntityAttribute.attribute("firstTreatmentDate",
                        (Subject s) -> s.getDrugFirstDoseDate().get(params.getStr(Param.DRUG_NAME)));
            }
        },
        LAST_TREATMENT_DATE(EntityAttribute.attribute("lastTreatmentDate", Subject::getLastTreatmentDate)),
        WITHDRAWAL(EntityAttribute.attribute("withdrawal", Subject::getWithdrawal)),
        DATE_OF_WITHDRAWAL(EntityAttribute.attribute("dateOfWithdrawal", Subject::getDateOfWithdrawal)),
        REASON_FOR_WITHDRAWAL(EntityAttribute.attribute("reasonForWithdrawal", Subject::getReasonForWithdrawal)),
        DEATH_FLAG(EntityAttribute.attribute("deathFlag", Subject::getDeathFlag)),
        DATE_OF_DEATH(EntityAttribute.attribute("dateOfDeath", Subject::getDateOfDeath)),
        PLANNED_ARM(EntityAttribute.attribute("plannedArm", Subject::getPlannedArm)),
        ACTUAL_ARM(EntityAttribute.attribute("actualArm", Subject::getActualArm)),
        DOSE_COHORT(EntityAttribute.attribute("doseCohort", Subject::getDoseCohort)),
        OTHER_COHORT(EntityAttribute.attribute("otherCohort", Subject::getOtherCohort)),
        DOSE_GROUPING(EntityAttribute.attribute("doseGrouping", Subject::getDoseGrouping)),
        OTHER_GROUPING(EntityAttribute.attribute("otherGrouping", Subject::getOtherGrouping)),
        SEX(EntityAttribute.attribute("sex", Subject::getSex)),
        RACE(EntityAttribute.attribute("race", Subject::getRace)),
        ETHNIC_GROUP(EntityAttribute.attribute("ethnicGroup", Subject::getEthnicGroup)),
        AGE(EntityAttribute.attribute("age", Subject::getAge)),
        SITE_ID(EntityAttribute.attribute("siteId", Subject::getSiteId)),
        CENTER_NUMBER(EntityAttribute.attribute("centerNumber", Subject::getCenterNumber)),
        REGION(EntityAttribute.attribute("region", Subject::getRegion)),
        PHASE(EntityAttribute.attribute("phase", Subject::getPhase)),
        MEDICAL_HISTORY(EntityAttribute.attribute("medicalHistories", Subject::getMedicalHistories, String.class)),
        SPEC_ETHNIC_GROUP(EntityAttribute.attribute("specifiedEthnicGroup", Subject::getSpecifiedEthnicGroup)),
        COUNTRY(EntityAttribute.attribute("country", Subject::getCountry)),
        DRUG_MAP(EntityAttribute.attribute("drugsDosed", Subject::getDrugsDosed)),
        DISC_MAP(EntityAttribute.attribute("drugsDiscontinued", Subject::getDrugsDiscontinued)),
        MAX_DOSE_MAP(EntityAttribute.attribute("drugsMaxDoses", Subject::getDrugsMaxDoses)),
        MAX_FREQUENCY_MAP(EntityAttribute.attribute("drugsMaxFrequencies", Subject::getDrugsMaxFrequencies)),
        DISC_REASONS_MAP(EntityAttribute.attribute("drugDiscontinuationMainReason", Subject::getDrugDiscontinuationMainReason)),
        DISC_DATES_MAP(EntityAttribute.attribute("drugDiscontinuationDate", Subject::getDrugDiscontinuationDate)),
        DUR_INCL_MAP(EntityAttribute.attribute("drugTotalDurationInclBreaks", Subject::getDrugTotalDurationInclBreaks)),
        DUR_EXCL_MAP(EntityAttribute.attribute("drugTotalDurationExclBreaks", Subject::getDrugTotalDurationExclBreaks)),
        STUDY_SPECIFIC_FILTERS(EntityAttribute.attribute("studySpecificFilters", Subject::getStudySpecificFilters, String.class)),
        SAFETYPOPULATION(EntityAttribute.attribute("safetyPopulation", Subject::getSafetyPopulation)),
        STUDY_STATUS(EntityAttribute.attribute("studyStatus", Subject::getStudyStatus)),
        WEIGHT(EntityAttribute.attribute("weight", Subject::getWeight)),
        HEIGHT(EntityAttribute.attribute("height", Subject::getHeight));

        @Getter
        private final EntityAttribute<Subject> attribute;

        Attributes(EntityAttribute<Subject> attribute) {
            this.attribute = attribute;
        }
    }

    @ToString
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SubjectVisit implements Serializable {
        private String subjectId;
        private String visit;
        private Date date;
    }

    @ToString
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SubjectGroup implements Serializable {
        private String subjectId;
        private String groupingName;
        private GroupType groupType;
        private Integer groupIndex;
        private String groupPreferredName;
        private String groupName;
        private String groupDefaultName;
    }

    @ToString
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SubjectEthnicGroup implements Serializable {
        private String subjectId;
        private String ethnicGroup;
        private String specifiedEthnicGroup;
    }

    @ToString
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SubjectVitalsInfo implements Serializable {
        private String subjectId;
        private String testName;
        private Date testDate;
        private Double testValue;
    }

    @ToString
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SubjectMedicalHistories implements Serializable {
        private String subjectId;
        private String reportedTerm;
    }

    @ToString
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SubjectStudyPeriodsParticipated implements Serializable {
        private String subjectId;
        private String studyPeriod;
    }

    @ToString
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SubjectStudySpecificFilters implements Serializable {
        private String subjectId;
        private String studySpecificFilter;
    }

    // Studies, parts & subjects
    private String subjectId;
    @Column(columnName = "subjectId", order = -1, displayName = "Subject id", defaultSortBy = true, defaultSortOrder = 0)
    private String subjectCode; // raw subject code from RESULT_PATIENT when merged = true
    private boolean merged; // true when subjectCode from one or several datasets
    @Column(columnName = "studyId", order = -3, displayName = "Study id")
    @Column(columnName = "studyId", order = 2, displayName = "Study id", type = Column.Type.STUDY_INFO)
    private String clinicalStudyCode;
    @Column(columnName = "studyName", order = 3, displayName = "Study name", type = Column.Type.STUDY_INFO)
    private String clinicalStudyName;
    private String datasetCode;
    @Column(columnName = "datasetName", order = 5, displayName = "Dataset name", type = Column.Type.STUDY_INFO)
    private String datasetName;
    @Column(columnName = "studyPart", order = -2, displayName = "Study Part id")
    @Column(columnName = "studyPart", order = 4, displayName = "Study Part id", type = Column.Type.STUDY_INFO)
    private String studyPart;
    private String datasetId;
    // Subject status
    private String studyStatus;
    private List<String> attendedVisitNumbers;
    private String lastVisitNumber;
    private String attendedAnalysisVisits; // is not used
    @Column(columnName = "durationOnStudy", order = 4, displayName = "Duration on Study")
    private Integer durationOnStudy;
    @Column(columnName = "randomised", order = 5, displayName = "Randomised")
    private String randomised;
    @Column(columnName = "dateOfRandomisation", order = 6, displayName = "Date of Randomisation")
    @Column(columnName = "dateOfRandomisation", order = 11, displayName = "Date of Randomisation", type = Column.Type.STUDY_INFO)
    private Date dateOfRandomisation;
    @Column(columnName = "firstTreatmentDate", order = 12, displayName = "First treatment date", type = Column.Type.STUDY_INFO)
    private Date firstTreatmentDate;
    private Date lastTreatmentDate;
    @Column(columnName = "withdrawal", order = 7, displayName = "Withdrawal/Completion")
    private String withdrawal;
    @Column(columnName = "dateOfWithdrawal", order = 8, displayName = "Date of Withdrawal/Completion")
    @Column(columnName = "dateOfWithdrawal", order = 16, displayName = "Date of Withdrawal/Completion", type = Column.Type.STUDY_INFO)
    private Date dateOfWithdrawal;
    private Date lastEtlDate;
    private Date studyLeaveDate;
    @Column(columnName = "reasonForWithdrawal", order = 9, displayName = "Main Reason for Withdrawal/Completion")
    @Column(columnName = "reasonForWithdrawal", order = 17, displayName = "Main Reason for Withdrawal/Completion", type = Column.Type.STUDY_INFO)
    private String reasonForWithdrawal;
    @Column(columnName = "deathFlag", order = 10, displayName = "Death")
    private String deathFlag;
    private String phase; // detect specific
    @Column(columnName = "dateOfDeath", order = 11, displayName = "Date of death")
    @Column(columnName = "dateOfDeath", order = 18, displayName = "Date of death", type = Column.Type.STUDY_INFO)
    private Date dateOfDeath;
    private String safetyPopulation; // detect specific
    private Date baselineDate;
    // Arms, cohorts & groups
    private String plannedArm;
    private String actualArm;
    @Column(columnName = "doseCohort", order = 12, displayName = "Cohort(Dose)")
    private String doseCohort;
    @Column(columnName = "otherCohort", order = 13, displayName = "Cohort(Other)")
    @Column(columnName = "otherCohort", order = 19, displayName = "Cohort-Other", type = Column.Type.STUDY_INFO)
    private String otherCohort;
    private String doseGrouping;
    private String otherGrouping;
    // Demography
    @Column(order = 1, displayName = "Sex", type = Column.Type.SSV)
    @Column(order = 2, displayName = "Sex", type = Column.Type.DEMOGRAPHY)
    @Column(order = 20, displayName = "Sex", columnName = "sex")
    private String sex;
    @Column(order = 2, displayName = "Race", type = Column.Type.SSV)
    @Column(order = 21, displayName = "Race", columnName = "race")
    @Column(order = 3, displayName = "Race", type = Column.Type.DEMOGRAPHY)
    private String race;
    @Column(order = 22, displayName = "Ethnic Group", columnName = "ethnicGroup")
    @Column(order = 4, displayName = "Ethnic Group", columnName = "ethnicGroup", type = Column.Type.DEMOGRAPHY)
    private String ethnicGroup;
    @Column(order = 3, displayName = "Age", type = Column.Type.SSV)
    @Column(order = 23, displayName = "Age", columnName = "age")
    @Column(order = 5, displayName = "Age", type = Column.Type.DEMOGRAPHY)
    private Integer age;
    @Column(order = 4, displayName = "Weight", type = Column.Type.SSV)
    @Column(order = 6, displayName = "Weight", type = Column.Type.DEMOGRAPHY)
    private Double weight;
    @Column(order = 5, displayName = "Height", type = Column.Type.SSV)
    @Column(order = 7, displayName = "Height", type = Column.Type.DEMOGRAPHY)
    private Double height;
    private String siteId;
    @Column(order = 24, displayName = "Centre", columnName = "centerNumber")
    @Column(order = 24, displayName = "Centre", columnName = "centerNumber", type = Column.Type.STUDY_INFO)
    private String centerNumber;
    private String region; // detect specific
    @Column(order = 32, displayName = "Country", columnName = "country")
    @Column(order = 25, displayName = "Country", columnName = "country", type = Column.Type.DEMOGRAPHY)
    private String country;
    private String specifiedEthnicGroup;
    private Date dateOfBirth;
    private Date enrollVisitDate;
    // Histories
    private Set<String> medicalHistories; // detect specific
    private List<String> studySpecificFilters; // detect specific

    @Builder.Default
    private Map<String, Set<String>> biomarkerGroups = new HashMap<>(); // is not used
    /**
     * Contains all drugs presented on study as the keys. If subject was dosed by particular drug, the value is 'Yes', 'No' otherwise
     */
    @Column(order = 14, displayName = "Dosed", columnName = "drugsDosed")
    @Singular("drugDosed")
    private Map<String, String> drugsDosed = new HashMap<>();
    @Column(order = 17, displayName = "Discontinued", columnName = "drugsDiscontinued")
    @Singular("drugDiscontinued")
    private Map<String, String> drugsDiscontinued = new HashMap<>();

    @Builder.Default
    private StudyInfo studyInfo = StudyInfo.EMPTY;
    /**
     * Contains all drugs presented on study as the keys. If subject was had discontinuation not dosed by particular drug, the value is '(Empty)'
     */
    @Column(order = 15, displayName = "Max Dose per Admin", columnName = "drugsMaxDoses")
    @Singular
    private Map<String, String> drugsMaxDoses = new HashMap<>();
    @Singular
    private Map<String, Double> drugsRawMaxDoses = new HashMap<>();
    /**
     * Contains all drugs presented on study as the keys. If subject was not dosed by particular drug, the value is '(Empty)'
     */
    @Column(order = 16, displayName = "Max Admin Frequency", columnName = "drugsMaxFrequencies")
    @Builder.Default
    private Map<String, String> drugsMaxFrequencies = new HashMap<>();
    @Column(order = 19, displayName = "Main Reason for Discontinuation", columnName = "drugDiscontinuationMainReason")
    @Singular("drugDiscontinuationMainReason")
    private Map<String, String> drugDiscontinuationMainReason = new HashMap<>();
    @Column(order = 18, displayName = "Date of Discontinuation", columnName = "drugDiscontinuationDate")
    @Builder.Default
    private Map<String, Date> drugDiscontinuationDate = new HashMap<>();
    @Singular("drugFirstDoseDate")
    private Map<String, Date> drugFirstDoseDate = new HashMap<>();
    @Builder.Default
    private Map<String, Integer> drugTotalDurationInclBreaks = new HashMap<>();
    @Builder.Default
    private Map<String, Integer> drugTotalDurationExclBreaks = new HashMap<>();

    @Override
    public String getId() {
        return getSubjectId();
    }

    @Override
    public Subject getSubject() {
        return this;
    }

    @Override
    public String getSubjectCode() {
        return isMerged() ? getDatasetId() + "-" + subjectCode : subjectCode;
    }

    public String getRawSubject() {
        return subjectCode;
    }

    @Column(order = 33, displayName = "Medical Histories", columnName = "medicalHistories")
    public String getMedicalHistoriesAsString() {
        if (medicalHistories == null) {
            return null;
        }
        return getMedicalHistories()
                .stream()
                .filter(Objects::nonNull)
                .sorted()
                .collect(Collectors.joining(", "));
    }

    @Column(order = 1, displayName = "Drug project name", columnName = "drugProjectName", type = Column.Type.STUDY_INFO)
    public String getDrugProjectName() {
        return studyInfo.getDrugProject();
    }

    public String getStudyDrugs() {
        return getDrugsDosed().entrySet().stream()
                .filter(d -> YES.equalsIgnoreCase(d.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.joining(", "));
    }

    public Map<String, String> getDrugsDosed() {
        return getUnmodifiableMap(drugsDosed);
    }

    public Map<String, Set<String>> getBiomarkerGroups() {
        return getUnmodifiableMap(biomarkerGroups);
    }

    public Map<String, String> getDrugsDiscontinued() {
        return getUnmodifiableMap(drugsDiscontinued);
    }

    public Map<String, String> getDrugsMaxDoses() {
        return getUnmodifiableMap(drugsMaxDoses);
    }

    public Map<String, String> getDrugsMaxFrequencies() {
        return getUnmodifiableMap(drugsMaxFrequencies);
    }

    public Map<String, String> getDrugDiscontinuationMainReason() {
        return getUnmodifiableMap(drugDiscontinuationMainReason);
    }

    public Map<String, Date> getDrugDiscontinuationDate() {
        return getUnmodifiableMap(drugDiscontinuationDate);
    }

    public Map<String, Date> getDrugFirstDoseDate() {
        return getUnmodifiableMap(drugFirstDoseDate);
    }

    public Map<String, Integer> getDrugTotalDurationInclBreaks() {
        return getUnmodifiableMap(drugTotalDurationInclBreaks);
    }

    public Map<String, Integer> getDrugTotalDurationExclBreaks() {
        return getUnmodifiableMap(drugTotalDurationExclBreaks);
    }

    public String getCountryAndRegion() {
        if (Objects.nonNull(region) && Objects.nonNull(country)) {
            return country.concat(", ").concat(region);
        }
        return null;
    }


    private static <K, V> Map<K, V> getUnmodifiableMap(Map<K, V> map) {
        return map == null ? null : Collections.unmodifiableMap(map);
    }
}
