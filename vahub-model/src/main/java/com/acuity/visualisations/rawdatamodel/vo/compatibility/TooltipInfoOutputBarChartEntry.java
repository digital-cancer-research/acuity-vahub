package com.acuity.visualisations.rawdatamodel.vo.compatibility;

import com.acuity.visualisations.rawdatamodel.vo.plots.BarChartEntry;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TooltipInfoOutputBarChartEntry extends OutputBarChartEntry implements Serializable {
    private Map<String, Object> tooltip;

    public TooltipInfoOutputBarChartEntry(BarChartEntry entry, int rank) {
        super(Objects.toString(((Map) entry.getCategory()).keySet().iterator().next()),
                rank, entry.getValue(), entry.getTotalSubjects());
        this.tooltip = ((Map) entry.getCategory());
    }

    // TODO this constructor works different than previous one; please fix it when possible.
    public TooltipInfoOutputBarChartEntry(BarChartEntry entry, int rank, Map<String, Object> tooltipData) {
        super(entry.getCategory().toString(), rank, entry.getValue(), entry.getTotalSubjects());
        this.tooltip = tooltipData;
    }
}
