package com.acuity.visualisations.rawdatamodel.vo.compatibility;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OutputOvertimeLineChartData implements Serializable {

    private String name;
    private List<OutputBarChartEntry> series;
    private String color;

}
