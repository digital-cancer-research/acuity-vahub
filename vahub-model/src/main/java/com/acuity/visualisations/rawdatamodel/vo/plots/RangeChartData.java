package com.acuity.visualisations.rawdatamodel.vo.plots;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"x"})
public final class RangeChartData implements Serializable {
    private Object x;
    private Integer xRank;
    private Integer dataPoints;
    private Double y;
    private Double min;
    private Double max;
    private Double stdDev;
    private Double stdErr;

    private RangeChartData(String x, Integer xRank, Integer numberOfDataPoints) {
        this.x = x;
        this.xRank = xRank;
        this.dataPoints = numberOfDataPoints;
    }

    public static RangeChartData empty(String x, Integer xRank, Integer numberOfDataPoints) {
        return new RangeChartData(x, xRank, numberOfDataPoints);
    }
}
