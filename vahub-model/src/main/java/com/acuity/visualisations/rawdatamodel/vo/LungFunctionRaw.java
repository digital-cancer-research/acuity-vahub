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
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Date;

@Getter
@EqualsAndHashCode(of = "id")
@ToString
@NoArgsConstructor
@Builder(toBuilder = true)
@AllArgsConstructor
@AcuityEntity(version = 0)
public class LungFunctionRaw implements HasStringId, HasSubjectId, HasEventDate, HasValueAndBaseline, HasVisitDate,
        PrecalculationSupport<LungFunctionRaw>, Serializable {
    private String id;
    private String subjectId;
    private String measurementNameRaw;
    private Date measurementTimePoint;
    private Integer daysSinceFirstDose;
    private String visitDescription;
    @Column(displayName = "Protocol schedule timepoint", order = 7, datasetType = Column.DatasetType.ACUITY)
    private String protocolScheduleTimepoint;
    private Double visit;
    private Date visitDate;
    private Double value;
    @Column(displayName = "Result unit", columnName = "resultUnit", order = 10, datasetType = Column.DatasetType.ACUITY)
    private String unit;
    private Double baselineValue;
    private Date baselineDate;
    @Column(displayName = "Baseline flag", order = 14, datasetType = Column.DatasetType.ACUITY)
    private String baselineFlag;
    @Builder.Default
    private Boolean calcChangeFromBaselineIfNull = true;
    @Builder.Default
    private Boolean calcDaysSinceFirstDoseIfNull = true;
    @Column(displayName = "Percent change from baseline", order = 13, datasetType = Column.DatasetType.ACUITY)
    private Double percentChangeFromBaseline;
    @Column(displayName = "Change from baseline", order = 12, datasetType = Column.DatasetType.ACUITY)
    private Double changeFromBaseline;

    @Override
    public LungFunctionRaw runPrecalculations() {
        LungFunctionRawBuilder builder = this.toBuilder();
        builder.percentChangeFromBaseline(HasValueAndBaseline.super.getPercentChangeFromBaseline());
        builder.changeFromBaseline(HasValueAndBaseline.super.getChangeFromBaseline());
        return builder.build();
    }

    @Override
    @Column(displayName = "Result value", columnName = "resultValue", order = 9, datasetType = Column.DatasetType.ACUITY)
    public Double getResultValue() {
        return value;
    }

    @Override
    @Column(displayName = "Baseline value", columnName = "baselineValue", order = 11, datasetType = Column.DatasetType.ACUITY)
    public Double getBaselineValue() {
        return baselineValue;
    }

    @Override
    public Double getChangeFromBaselineRaw() {
        return null;
    }

    public String getUnit() {
        return StringUtils.substringBetween(measurementNameRaw, "(", ")");
    }

    @Column(displayName = "Measurement name", columnName = "measurementName", order = 4, datasetType = Column.DatasetType.ACUITY)
    public String getMeasurementName() {
        return measurementNameRaw.substring(0, measurementNameRaw.indexOf("(") - 1);
    }

    @Override
    public Date getEventDate() {
        return measurementTimePoint;
    }
}
