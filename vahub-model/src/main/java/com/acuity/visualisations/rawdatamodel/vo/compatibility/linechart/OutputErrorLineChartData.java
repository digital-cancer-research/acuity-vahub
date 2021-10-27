package com.acuity.visualisations.rawdatamodel.vo.compatibility.linechart;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


import static java.util.stream.Collectors.toList;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class OutputErrorLineChartData extends OutputLineChartData {
    public OutputErrorLineChartData(Object seriesBy, List<OutputErrorLineChartEntry> series) {
        super(seriesBy, series.stream().map(ser -> (OutputLineChartEntry) ser).collect(toList()));
    }
}
