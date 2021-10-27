package com.acuity.visualisations.rawdatamodel.vo;

import com.acuity.visualisations.rawdatamodel.dataproviders.RenalDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.util.Column;
import com.acuity.visualisations.rawdatamodel.util.Constants;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"ckdStageRaw", "ckdStageNameRaw"})
@Builder(toBuilder = true)
@AcuityEntity(version = 9)
public final class RenalRaw implements HasStringId, HasSubjectId, HasValueAndBaseline, HasReferenceRange, HasEventDate,
        PrecalculationSupport<RenalRaw> {
    private String id;
    private String subjectId;

    @Column(displayName = "Measurement name", columnName = "measurementName", order = 1)
    private String labCode;

    @Builder.Default
    private RenalDatasetsDataProvider.CrClCalculateMethods method = RenalDatasetsDataProvider.CrClCalculateMethods.DIRECT;

    @Column(displayName = "Result value", columnName = "resultValue", order = 5, datasetType = Column.DatasetType.ACUITY)
    private Double value;
    @Column(displayName = "Result unit", columnName = "resultUnit", order = 6, datasetType = Column.DatasetType.ACUITY)
    private String unit;
    @Column(displayName = "Visit number", order = 4, datasetType = Column.DatasetType.ACUITY)
    private Double visitNumber;
    private String visitDescription;
    private Date measurementTimePoint;
    private Double analysisVisit;
    private Double avgWeight;

    private Double baselineValue;
    private Double changeFromBaselineRaw;
    private String baselineFlag;

    private Double refHigh;
    private Double refLow;

    private String studyPeriods;
    private Integer daysSinceFirstDose;

    @Getter(AccessLevel.PRIVATE)
    private Integer ckdStageRaw;
    @Getter(AccessLevel.PRIVATE)
    private String ckdStageNameRaw;


    @Builder.Default
    private Boolean calcChangeFromBaselineIfNull = true;
    @Builder.Default
    private Boolean calcDaysSinceFirstDoseIfNull = true;
    @Builder.Default
    private Boolean calcCkgStageIfNull = true;

    @Override
    public Double getResultValue() {
        return value;
    }

    @Override
    public Double getBaselineValue() {
        return baselineValue;
    }

    @Getter
    private Integer ckdStage;

    @Getter
    @Column(displayName = "Ckd stage", columnName = "ckdStage", order = 9, datasetType = Column.DatasetType.ACUITY)
    private String ckdStageName;

    @Getter
    private Double percentChangeFromBaseline;

    @Getter
    private Double changeFromBaseline;

    @Getter
    private Double referenceRangeNormalisedValue;

    @Getter
    private Double timesUpperReferenceRange;

    @Getter
    private Double timesLowerReferenceRange;

    @Override
    public RenalRaw runPrecalculations() {
        RenalRawBuilder builder = this.toBuilder();
        builder.ckdStage(calculateCkdStage());
        builder.ckdStageName(calculateCkdStageName());
        builder.percentChangeFromBaseline(HasValueAndBaseline.super.getPercentChangeFromBaseline());
        builder.changeFromBaseline(HasValueAndBaseline.super.getChangeFromBaseline());
        builder.referenceRangeNormalisedValue(HasReferenceRange.super.getReferenceRangeNormalisedValue());
        builder.timesUpperReferenceRange(HasReferenceRange.super.getTimesUpperReferenceRange());
        builder.timesLowerReferenceRange(HasReferenceRange.super.getTimesLowerReferenceRange());
        return builder.build();
    }

    @Override
    public Date getEventDate() {
        return measurementTimePoint;
    }

    private Integer calculateCkdStage() {
        if (calcCkgStageIfNull && ckdStageRaw == null) {
            if (value == null) {
                return null;
            }
            if (value < 15) {
                return 5;
            }
            if (value < 30) {
                return 4;
            }
            if (value < 60) {
                return 3;
            }
            if (value < 90) {
                return 2;
            }
            return 1;
        } else {
            return ckdStageRaw;
        }
    }

    private String calculateCkdStageName() {
        if (calcCkgStageIfNull && ckdStageNameRaw == null) {
            if (value != null) {
                return String.format("CKD Stage %d", calculateCkdStage());
            } else {
                return Constants.EMPTY;
            }
        } else {
            return ckdStageNameRaw;
        }
    }
}
