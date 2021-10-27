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
import com.acuity.visualisations.rawdatamodel.util.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

@EqualsAndHashCode(of = "id")
@ToString
@Getter
@NoArgsConstructor
@Builder(toBuilder = true)
@AllArgsConstructor
@AcuityEntity(version = 7)
public final class VitalRaw implements HasStringId, HasSubjectId, Serializable, HasValueAndBaseline, HasEventDate,
        HasBaselineFlag, PrecalculationSupport<VitalRaw> {

    private String id;
    private String subjectId;

    @Column(columnName = "measurementName", order = 1, displayName = "Measurement name", type = Column.Type.DOD)
    private String vitalsMeasurement;
    private String plannedTimePoint;
    private Date measurementDate;
    private Integer daysSinceFirstDose;
    @Builder.Default
    private Boolean calcDaysSinceFirstDoseIfNull = true;
    @Column(columnName = "visitNumber", order = 5, displayName = "Visit number", type = Column.Type.DOD)
    private Double visitNumber;
    private String scheduleTimepoint;

    @Column(columnName = "resultUnit", order = 8, displayName = "Result unit", type = Column.Type.DOD)
    private String unit;
    private Double changeFromBaselineRaw;
    @Builder.Default
    private Boolean calcChangeFromBaselineIfNull = true;
    @Column(columnName = "baselineValue", order = 9, displayName = "Baseline value", type = Column.Type.DOD)
    private Double baseline;
    @Column(columnName = "baselineFlag", order = 12, displayName = "Baseline flag", type = Column.Type.DOD)
    private String baselineFlag;
    @Column(columnName = "resultValue", order = 7, displayName = "Result value", type = Column.Type.DOD)
    private Double resultValue;
    private Date baselineDate;
    private Double analysisVisit;
    private String studyPeriod;
    private Date lastDoseDate;
    private String lastDoseAmount;
    private String anatomicalLocation;
    private String sidesOfInterest;
    @Column(columnName = "physicalPosition", order = 17, displayName = "Physical position",
            type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    private String physicalPosition;
    private String clinicallySignificant;

    @Override
    public Double getBaselineValue() {
        return baseline;
    }

    @Override
    public Double getChangeFromBaselineRaw() {
        return changeFromBaselineRaw;
    }

    private Double percentChangeFromBaseline;

    private Double changeFromBaseline;

    @Override
    public boolean hasBaselineFlag() {
        return Constants.BASELINE_FLAG_YES.equals(baselineFlag);
    }

    @Override
    public Date getEventDate() {
        return measurementDate;
    }

    @Override
    public VitalRaw runPrecalculations() {
        VitalRawBuilder builder = this.toBuilder();
        builder.percentChangeFromBaseline(HasValueAndBaseline.super.getPercentChangeFromBaseline());
        builder.changeFromBaseline(HasValueAndBaseline.super.getChangeFromBaseline());
        return builder.build();
    }
}
