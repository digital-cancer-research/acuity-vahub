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
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

import static com.acuity.visualisations.rawdatamodel.util.Column.DatasetType.ACUITY;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(of = "id")
@AcuityEntity(version = 0)
public class CardiacRaw implements HasStringId, HasSubjectId, HasValueAndBaseline, HasEventDate, PrecalculationSupport<CardiacRaw>, Serializable {

    private String id;
    private String subjectId;

    @Column(order = 1, displayName = "Measurement category", datasetType = ACUITY)
    private String measurementCategory;

    @Column(order = 2, displayName = "Measurement name", datasetType = ACUITY)
    private String measurementName;

    private String shortMeasurementName;
    private Double analysisVisit;

    @Column(order = 5, displayName = "Visit number", datasetType = ACUITY)
    private Double visitNumber;

    @Column(order = 6, displayName = "Result value", datasetType = ACUITY)
    private Double resultValue;

    @Column(order = 7, displayName = "Result unit", datasetType = ACUITY)
    private String resultUnit;

    @Column(order = 8, displayName = "Baseline value", datasetType = ACUITY)
    private Double baselineValue;

    @Column(order = 11, displayName = "Baseline flag", datasetType = ACUITY)
    private String baselineFlag;

    private Date baselineDate;

    @Column(order = 12, displayName = "Clinically significant", datasetType = ACUITY)
    private String clinicallySignificant;

    @Column(order = 13, displayName = "Protocol schedule timepoint", datasetType = ACUITY)
    private String protocolScheduleTimepoint;

    @Column(order = 14, displayName = "Method", datasetType = ACUITY)
    private String method;

    private String measurementType;
    private Date measurementTimePoint;
    private Integer daysSinceFirstDose;
    @Builder.Default
    private Boolean calcDaysSinceFirstDoseIfNull = true;
    private Double changeFromBaselineRaw;
    @Builder.Default
    private Boolean calcChangeFromBaselineIfNull = true;

    @Column(order = 15, displayName = "Date of last drug dose", datasetType = ACUITY)
    private Date dateOfLastDose;

    @Column(order = 16, displayName = "Last drug dose amount", datasetType = ACUITY)
    private String lastDoseAmount;

    @Column(order = 17, displayName = "Atrial fibrillation", datasetType = ACUITY)
    private String atrialFibrillation;

    @Column(order = 18, displayName = "Sinus rhythm", datasetType = ACUITY)
    private String sinusRhythm;

    @Column(order = 19, displayName = "Reason, no sinus rhythm", datasetType = ACUITY)
    private String reasonNoSinusRhythm;

    @Column(order = 20, displayName = "Heart rhythm", datasetType = ACUITY)
    private String heartRhythm;

    @Column(order = 21, displayName = "Heart rhythm, other", datasetType = ACUITY)
    private String heartRhythmOther;

    @Column(order = 22, displayName = "Extra systoles", datasetType = ACUITY)
    private String extraSystoles;

    @Column(order = 23, displayName = "Specify extra systoles", datasetType = ACUITY)
    private String specifyExtraSystoles;

    @Column(order = 24, displayName = "Type of conduction", datasetType = ACUITY)
    private String typeOfConduction;

    @Column(order = 25, displayName = "Conduction", datasetType = ACUITY)
    private String conduction;

    @Column(order = 26, displayName = "Reason, abnormal conduction", datasetType = ACUITY)
    private String reasonAbnormalConduction;

    @Column(order = 27, displayName = "ST-T changes", datasetType = ACUITY)
    private String sttChanges;

    @Column(order = 28, displayName = "ST segment", datasetType = ACUITY)
    private String stSegment;

    @Column(order = 29, displayName = "T-wave", datasetType = ACUITY)
    private String wave;

    @Column(order = 30, displayName = "Beat group number", datasetType = ACUITY)
    private Integer beatGroupNumber;

    @Column(order = 31, displayName = "Beat number within beat group", datasetType = ACUITY)
    private Integer beatNumberWithinBeatGroup;

    @Column(order = 32, displayName = "Number of beats in average beat", datasetType = ACUITY)
    private Integer numberOfBeatsInAverageBeat;

    @Column(order = 33, displayName = "Beat group length (sec)", datasetType = ACUITY)
    private Double beatGroupLengthInSec;

    @Column(order = 34, displayName = "Cardiologist comment", datasetType = ACUITY)
    private String comment;

    private String ecgEvaluation;
    private String studyPeriods;
    private String visitDescription;
    private Double percentChangeFromBaseline;
    private Double changeFromBaseline;

    @Override
    public Date getEventDate() {
        return measurementTimePoint;
    }

    @Override
    public CardiacRaw runPrecalculations() {
        CardiacRawBuilder builder = this.toBuilder();
        builder.percentChangeFromBaseline(HasValueAndBaseline.super.getPercentChangeFromBaseline());
        builder.changeFromBaseline(HasValueAndBaseline.super.getChangeFromBaseline());
        return builder.build();
    }
}

