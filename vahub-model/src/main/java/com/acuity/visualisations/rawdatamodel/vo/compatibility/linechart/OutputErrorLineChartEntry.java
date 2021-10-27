package com.acuity.visualisations.rawdatamodel.vo.compatibility.linechart;

import com.acuity.visualisations.rawdatamodel.vo.plots.ErrorLineChartEntry;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class OutputErrorLineChartEntry extends OutputLineChartEntry {
    private Double standardDeviation;

    public OutputErrorLineChartEntry(ErrorLineChartEntry entry, String color) {
        super(entry, color);
        this.standardDeviation = entry.getStandardDeviation();
    }
}
