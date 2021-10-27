package com.acuity.visualisations.rawdatamodel.vo.compatibility;

import com.acuity.visualisations.rawdatamodel.vo.plots.BarChartEntry;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OutputBarChartEntry implements Serializable {

    private String category;
    private Integer rank;
    private Double value;
    private Integer totalSubjects;

    public OutputBarChartEntry(BarChartEntry entry, int rank) {
        this(entry.getCategory().toString(), rank, entry.getValue(), entry.getTotalSubjects());
    }
}
