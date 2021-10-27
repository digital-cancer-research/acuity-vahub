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

@Getter
@EqualsAndHashCode(of = "id")
@ToString
@NoArgsConstructor
@Builder(toBuilder = true)
@AllArgsConstructor
@AcuityEntity(version = 26)
public class LabRaw implements HasStringId, HasSubjectId, Serializable, HasValueAndBaseline, HasReferenceRange, HasEventDate {

    private String id;
    private String subjectId;

    @Column(columnName = "measurementName", order = 2, displayName = "Measurement name", type = Column.Type.DOD)
    @Column(columnName = "code", order = 2, displayName = "Measurement name", type = Column.Type.SSV)
    @Column(columnName = "code", order = 1, displayName = "Lab Type", type = Column.Type.AML)
    private String labCode;
    @Column(columnName = "measurementCategory", order = 1, displayName = "Measurement category")
    private String category;
    @Column(columnName = "resultValue", order = 7, displayName = "Result value", type = Column.Type.DOD)
    @Column(columnName = "value", order = 3, displayName = "Result value", type = Column.Type.SSV)
    @Column(columnName = "value", order = 3, displayName = "Value of Lab", type = Column.Type.AML)
    private Double value;
    @Column(columnName = "resultUnit", order = 8, displayName = "Result unit", type = Column.Type.DOD)
    @Column(columnName = "resultUnit", order = 4, displayName = "Result unit", type = Column.Type.SSV)
    @Column(columnName = "resultUnit", order = 2, displayName = "Unit", type = Column.Type.AML)
    private String unit;
    @Column(columnName = "baselineValue", order = 9, displayName = "Baseline value")
    private Double baseline;
    private Double changeFromBaselineRaw;
    @Builder.Default
    private Boolean calcChangeFromBaselineIfNull = true;
    @Column(columnName = "baselineFlag", order = 12, displayName = "Baseline flag")
    private String baselineFlag;
    @Column(columnName = "upperRefRangeValue", order = 17, displayName = "Upper ref range value", type = Column.Type.DOD)
    @Column(columnName = "upperRefRangeValue", order = 6, displayName = "Upper ref range value", type = Column.Type.SSV)
    @Column(columnName = "upperRefRangeValue", order = 5, displayName = "Normal Range High", type = Column.Type.AML)
    private Double refHigh;
    @Column(columnName = "lowerRefRangeValue", order = 16, displayName = "Lower ref range value", type = Column.Type.DOD)
    @Column(columnName = "lowerRefRangeValue", order = 5, displayName = "Lower ref range value", type = Column.Type.SSV)
    @Column(columnName = "lowerRefRangeValue", order = 4, displayName = "Normal Range Low", type = Column.Type.AML)
    private Double refLow;

    @Column(columnName = "measurementTimePoint", order = 3, displayName = "Measurement time point", type = Column.Type.DOD, defaultSortBy = true)
    @Column(columnName = "measurementTimePoint", order = 1, displayName = "Measurement time point", type = Column.Type.SSV)
    private Date measurementTimePoint;
    @Column(columnName = "visitNumber", order = 6, displayName = "Visit number")
    @Column(columnName = "visitNumber", order = 7, displayName = "Visit number", type = Column.Type.AML)
    private Double visitNumber;
    @Column(columnName = "analysisVisit", order = 5, displayName = "Analysis visit", datasetType = Column.DatasetType.DETECT)
    private Double analysisVisit;
    private String visitDescription;
    @Column(columnName = "visitDate", order = 6, displayName = "Date of Visit", type = Column.Type.AML, datasetType = Column.DatasetType.ACUITY)
    private Date visitDate;

    private String normalizedLabCode;
    private String originalLabCode;

    private String comment;
    @Column(columnName = "valueDipstick", order = 19, displayName = "Value dipstick", datasetType = Column.DatasetType.ACUITY)
    private String valueDipstick;
    @Column(columnName = "protocolScheduleTimepoint", order = 18, displayName = "Protocol schedule timepoint", datasetType = Column.DatasetType.ACUITY)
    private String protocolScheduleTimepoint;
    private String studyPeriods;

    @Column(columnName = "daysSinceFirstDose", order = 8, displayName = "Days on study", type = Column.Type.AML)
    private Integer daysSinceFirstDose;
    @Column(columnName = "sourceType", order = 20, displayName = "Source type", type = Column.Type.DOD)
    private String sourceType;
    private String sourceId;
    private Device device;

    @Column(columnName = "deviceName", order = 21, displayName = "Device name", type = Column.Type.DOD)
    public String getDeviceName() {
        return device != null ? device.getName() : null;
    }

    @Column(columnName = "deviceVersion", order = 22, displayName = "Device version", type = Column.Type.DOD)
    public String getDeviceVersion() {
        return device != null ? device.getVersion() : null;
    }

    @Column(columnName = "deviceType", order = 23, displayName = "Device type", type = Column.Type.DOD)
    public String getDeviceType() {
        return device != null ? device.getType() : null;
    }

    @Builder.Default
    private Boolean calcDaysSinceFirstDoseIfNull = true;

    private Boolean usedInTfl;

    public boolean isOutOfNormalRange() {
        return value != null && (refHigh == null || refLow == null || value.compareTo(refHigh) > 0 || value.compareTo(refLow) < 0);
    }

    @Override
    public Double getResultValue() {
        return value;
    }

    @Override
    public Double getBaselineValue() {
        return baseline;
    }

    /* Here goes a block of methods having calcs that need to be cached (according to CPU profiling) */

    @Getter(lazy = true)
    private final Double percentChangeFromBaseline = HasValueAndBaseline.super.getPercentChangeFromBaseline();

    @Getter(lazy = true)
    private final Double changeFromBaseline = HasValueAndBaseline.super.getChangeFromBaseline();

    @Getter(lazy = true)
    private final Double referenceRangeNormalisedValue = HasReferenceRange.super.getReferenceRangeNormalisedValue();

    @Getter(lazy = true)
    private final Double timesUpperReferenceRange = HasReferenceRange.super.getTimesUpperReferenceRange();

    @Getter(lazy = true)
    private final Double timesLowerReferenceRange = HasReferenceRange.super.getTimesLowerReferenceRange();

    @Override
    public Date getEventDate() {
        return measurementTimePoint;
    }
}
