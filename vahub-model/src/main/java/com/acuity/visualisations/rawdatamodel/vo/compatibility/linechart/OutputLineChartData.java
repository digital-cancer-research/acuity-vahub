package com.acuity.visualisations.rawdatamodel.vo.compatibility.linechart;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
public class OutputLineChartData implements Serializable {

    private Object seriesBy;
    private List<OutputLineChartEntry> series;

}
