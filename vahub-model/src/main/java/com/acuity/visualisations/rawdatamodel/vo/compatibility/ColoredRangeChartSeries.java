package com.acuity.visualisations.rawdatamodel.vo.compatibility;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class ColoredRangeChartSeries extends RangeChartSeries implements Serializable {
    private String color;

    public ColoredRangeChartSeries(String name, List<OutputRangeChartEntry> data, String color) {
        super(name, data);
        this.color = color;
    }
}
