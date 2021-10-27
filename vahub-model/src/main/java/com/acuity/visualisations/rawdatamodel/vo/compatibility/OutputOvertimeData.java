package com.acuity.visualisations.rawdatamodel.vo.compatibility;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class OutputOvertimeData implements Serializable {

    private List<OutputOvertimeLineChartData> lines;
    private List<String> categories;
    private List<? extends OutputBarChartData> series;

}
