package com.acuity.visualisations.rawdatamodel.vo.compatibility;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class ColoredOutputBarChartData extends OutputBarChartData implements Serializable {
    private String color;

    public ColoredOutputBarChartData(String name, List<String> categories, List<OutputBarChartEntry> series, String color) {
        super(name, categories, series);
        this.color = color;
    }
}
