package com.acuity.visualisations.rawdatamodel.vo.plots;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ErrorLineChartEntry extends LineChartEntry {
    private Double standardDeviation;

    public ErrorLineChartEntry(Object x, Object y, Object name, Object colorBy, Object sortBy, Double standardDeviation) {
        super(x, y, name, colorBy, sortBy);
        this.standardDeviation = standardDeviation;
    }
}
