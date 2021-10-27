package com.acuity.visualisations.rawdatamodel.vo.compatibility;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class OutputScatterPlotEntry implements Serializable {
    private double x;
    private double y;
    private String name;
    private String color;
    private double measurementValue;
}
