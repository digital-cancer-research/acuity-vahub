package com.acuity.visualisations.rawdatamodel.vo.compatibility;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OutputColumnRangeChartEntry implements Serializable {

    private int x;
    private Number high;
    private Number low;
    private String color;
    private boolean noStartDate;
    private List<String> therapies;
    private String name;
}
