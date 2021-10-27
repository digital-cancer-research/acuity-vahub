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
