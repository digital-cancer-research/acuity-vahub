package com.acuity.visualisations.rawdatamodel.vo.plots;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class LineChartData implements Serializable {

    private Object seriesBy;
    private List<LineChartEntry> series;
}
