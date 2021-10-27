package com.acuity.visualisations.rawdatamodel.vo.plots;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public final class RangeChartCalculationObject implements Serializable {
    private Integer dataPoints;
    private Double mean;
    private Double median;
    private Double min;
    private Double max;
    private Double stdDev;
    private Double stdErr;
    private String name;
}
