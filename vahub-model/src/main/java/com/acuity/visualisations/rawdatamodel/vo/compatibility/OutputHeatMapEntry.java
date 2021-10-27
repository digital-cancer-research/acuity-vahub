package com.acuity.visualisations.rawdatamodel.vo.compatibility;

import com.acuity.visualisations.rawdatamodel.vo.plots.HeatMapEntry;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OutputHeatMapEntry implements Serializable {

    private int x;
    private int y;
    private String name;
    private Object value;
    private String color;

    public OutputHeatMapEntry(HeatMapEntry innerData, String color) {
        this(innerData.getX(), innerData.getY(), innerData.getName(), innerData.getValue(), color);
    }
}
