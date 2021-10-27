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
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.io.Serializable;
import java.util.Date;

@Value
@EqualsAndHashCode(of = "id")
@Builder(toBuilder = true)
@AcuityEntity(version = 1)
public final class LiverRaw implements HasStringId, HasSubjectId, HasValueAndBaseline, HasReferenceRange, HasEventDate,
        PrecalculationSupport<LiverRaw>, Serializable {
    private String id;
    private String subjectId;

    @Column(columnName = "measurementName", order = 1, displayName = "Measurement name")
    private String labCode;

    private String normalizedLabCode;

    private String category;

    @Column(columnName = "resultValue", order = 5, displayName = "Result value")
    private Double value;

    @Column(columnName = "resultUnit", order = 6, displayName = "Result unit")
    private String unit;

    @Column(columnName = "baselineValue", order = 7, displayName = "Baseline value")
    private Double baseline;

    private Double changeFromBaselineRaw;
    @Builder.Default
    private Boolean calcChangeFromBaselineIfNull = true;

    @Column(columnName = "baselineFlag", order = 10, displayName = "Baseline flag")
    private String baselineFlag;

    @Column(columnName = "upperRefRangeValue", order = 15, displayName = "Upper ref range value")
    private Double refHigh;

    @Column(columnName = "lowerRefRangeValue", order = 14, displayName = "Lower ref range value")
    private Double refLow;

    private Date measurementTimePoint;

    @Column(columnName = "visitNumber", order = 4, displayName = "Visit number")
    private Double visitNumber;

    private Double analysisVisit;

    private String visitDescription;

    private String comment;
    private String valueDipstick;
    private String protocolScheduleTimepoint;
    private String studyPeriods;

    private Integer daysSinceFirstDose;
    @Builder.Default
    private Boolean calcDaysSinceFirstDoseIfNull = true;

    @Override
    public Double getResultValue() {
        return value;
    }

    @Override
    public Double getBaselineValue() {
        return baseline;
    }

    private Double percentChangeFromBaseline;
    private Double changeFromBaseline;
    private Double referenceRangeNormalisedValue;
    private Double timesUpperReferenceRange;
    private Double timesLowerReferenceRange;
    private Double normalizedValue;

    @Override
    public Date getEventDate() {
        return measurementTimePoint;
    }

    private double calculateNormalizedValue() {
        Double referenceRangeHigherLimit = getRefHigh();
        if (referenceRangeHigherLimit == null || referenceRangeHigherLimit == 0) {
            return 0;
        } else {
            return getResultValue() / referenceRangeHigherLimit;
        }
    }

    @Override
    public LiverRaw runPrecalculations() {
        LiverRawBuilder builder = this.toBuilder();
        builder.percentChangeFromBaseline(HasValueAndBaseline.super.getPercentChangeFromBaseline());
        builder.changeFromBaseline(HasValueAndBaseline.super.getChangeFromBaseline());
        builder.referenceRangeNormalisedValue(HasReferenceRange.super.getReferenceRangeNormalisedValue());
        builder.timesUpperReferenceRange(HasReferenceRange.super.getTimesUpperReferenceRange());
        builder.timesLowerReferenceRange(HasReferenceRange.super.getTimesLowerReferenceRange());
        builder.normalizedValue(calculateNormalizedValue());
        return builder.build();
    }
}
