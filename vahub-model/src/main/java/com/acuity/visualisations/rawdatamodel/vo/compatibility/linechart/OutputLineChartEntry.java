package com.acuity.visualisations.rawdatamodel.vo.compatibility.linechart;

import com.acuity.visualisations.rawdatamodel.vo.plots.LineChartEntry;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class OutputLineChartEntry implements Serializable {

    public static final String NO_COLOR = "";

    private Object x;
    private Object y;
    private Object name;
    private String color;


    public OutputLineChartEntry(LineChartEntry entry, String color) {
        this.x = entry.getX();
        this.y = entry.getY();
        this.name = entry.getName();
        this.color = color;
    }
}
