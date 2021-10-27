package com.acuity.visualisations.rawdatamodel.vo.compatibility;

import com.acuity.visualisations.rawdatamodel.vo.plots.WaterfallEntry;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OutputWaterfallEntry implements Serializable {

    private int x;
    private double y;
    private String name;
    private String color;

    public OutputWaterfallEntry(WaterfallEntry entry, String color) {
        this(entry.getX(), entry.getY(), entry.getName(), color);
        this.color = color;
    }
}
