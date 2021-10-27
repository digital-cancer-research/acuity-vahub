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
import com.acuity.visualisations.rawdatamodel.util.DodUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Singular;
import lombok.ToString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@EqualsAndHashCode//(of = "id")
@ToString
@Getter
@Setter
@NoArgsConstructor
@Builder(toBuilder = true)
@AllArgsConstructor
@AcuityEntity(version = 7)
public class DrugDoseRaw implements HasStringId, HasSubjectId, Serializable {
    private String id;
    private String subjectId;

    @Column(order = 4, displayName = "Study drug", type = Column.Type.SSV)
    private String drug;
    private String doseDescription;

    @Column(order = 7, displayName = "Dose per administration", columnName = "dosePerAdmin")
    @Column(order = 5, displayName = "Dose per administration", type = Column.Type.SSV)
    private Double dose;

    @Column(order = 8, displayName = "Dose unit")
    @Column(order = 6, displayName = "Dose unit", type = Column.Type.SSV)
    private String doseUnit;

    @Column(order = 5, displayName = "Start date", defaultSortBy = true, columnName = "startDate")
    @Column(order = 2, displayName = "Start date", type = Column.Type.SSV)
    private Date startDate;

    @Column(order = 6, displayName = "End date")
    @Column(order = 3, displayName = "End date", type = Column.Type.SSV)
    private Date endDate;

    private Integer frequency;

    @Column(order = 9, displayName = "Dose frequency", columnName = "doseFreq")
    @Column(order = 7, displayName = "Dose frequency", type = Column.Type.SSV)
    private String frequencyName;

    private String frequencyRank;

    @Column(order = 16, displayName = "Action taken")
    @Column(order = 8, displayName = "Action taken", type = Column.Type.SSV)
    private String actionTaken;

    @Column(order = 17, displayName = "Main reason for action taken", columnName = "mainReasonForActionTaken")
    @Column(order = 9, displayName = "Main reason for action taken", type = Column.Type.SSV)
    private String reasonForActionTaken;

    private String periodType;
    private String subsequentPeriodType;

    @Column(order = 4, displayName = "Study drug category")
    private String studyDrugCategory;

    @Column(order = 10, displayName = "Total daily dose")
    private Double totalDailyDose;

    @Column(order = 11, displayName = "Planned dose")
    private Double plannedDose;

    @Column(order = 12, displayName = "Planned dose units")
    private String plannedDoseUnits;

    @Column(order = 13, displayName = "Planned No. of days treatment")
    private Integer plannedNoDaysTreatment;

    @Column(order = 14, displayName = "Formulation")
    private String formulation;

    @Column(order = 15, displayName = "Route")
    private String route;

    @Column(order = 18, displayName = "Main reason for action taken, Specification")
    private String mainReasonForActionTakenSpec;

    @Singular("aeNumCausedActionTaken")
    private List<Integer> aeNumCausedActionTaken = new ArrayList<>();
    @Singular("aePtCausedActionTaken")
    private List<String> aePtCausedActionTaken = new ArrayList<>();

    @Column(order = 21, displayName = "Reason for therapy")
    private String reasonForTherapy;
    @Column(order = 22, displayName = "Treatment cycle delayed")
    private String treatmentCycleDelayed;
    @Column(order = 23, displayName = "Reason treatment cycle delayed")
    private String reasonTreatmentCycleDelayed;
    @Column(order = 24, displayName = "Reason treatment cycle delayed, Other")
    private String reasonTreatmentCycleDelayedOther;

    @Singular("aeNumCausedTreatmentCycleDelayed")
    private List<Integer> aeNumCausedTreatmentCycleDelayed = new ArrayList<>();
    @Singular("aePtCausedTreatmentCycleDelayed")
    private List<String> aePtCausedTreatmentCycleDelayed = new ArrayList<>();

    @Column(order = 27, displayName = "Medication Code")
    private String medicationCode;

    @Column(order = 28, displayName = "Medication dictionary text")
    private String medicationDictionaryText;

    @Column(order = 29, displayName = "ATC code")
    private String atcCode;

    @Column(order = 30, displayName = "ATC dictionary text")
    private String atcDictionaryText;

    @Column(order = 31, displayName = "Medication PT")
    private String medicationPt;

    @Column(order = 32, displayName = "Medication grouping name")
    private String medicationGroupingName;

    @Column(order = 33, displayName = "Active ingredients")
    private String activeIngredient;

    private Boolean usedInTfl;

    @Column(order = 19, displayName = "AE number caused action taken", columnName = "aeNumCausedActionTaken")
    public String getAeNumCausedActionTakenAsStrng() {
        return DodUtil.toString(aeNumCausedActionTaken, ", ");
    }

    @Column(order = 20, displayName = "AE PT caused action taken", columnName = "aePtCausedActionTaken")
    public String getAePtCausedActionTakenAsString() {
        return DodUtil.toString(aePtCausedActionTaken, ", ");
    }

    @Column(order = 25, displayName = "AE number caused treatment cycle delayed", columnName = "aeNumCausedTreatmentCycleDelayed")
    public String getAeNumCausedTreatmentCycleDelayedAsString() {
        return DodUtil.toString(aeNumCausedTreatmentCycleDelayed, ", ");
    }

    @Column(order = 26, displayName = "AE PT caused treatment cycle delayed", columnName = "aePtCausedTreatmentCycleDelayed")
    public String getAePtCausedTreatmentCycleDelayedAsString() {
        return DodUtil.toString(aePtCausedTreatmentCycleDelayed, ", ");
    }

    @Column(order = 3, displayName = "Study drug", columnName = "studyDrug")
    public String getDrugName() {
        return Objects.toString(getDoseDescription(), getDrug());
    }

    //following inner classes are only used for temp objects inside data provider,
    //so it's fine to have them mutable

    @Data
    public static class AeNumCausedActionTaken {
        private String drugDoseId;
        private Integer aeNumCausedActionTaken;
    }

    @Data
    public static class AeNumCausedTreatmentCycleDelayed {
        private String drugDoseId;
        private Integer aeNumCausedTreatmentCycleDelayed;
    }

    @Data
    public static class AePtCausedActionTaken {
        private String drugDoseId;
        private String subjectId;
        private Integer aeNum;
    }

    @Data
    public static class AePtCausedTreatmentCycleDelayed {
        private String drugDoseId;
        private String subjectId;
        private Integer aeNumDel;
    }
}
