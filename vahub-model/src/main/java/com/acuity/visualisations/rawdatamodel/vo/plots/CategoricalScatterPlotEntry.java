package com.acuity.visualisations.rawdatamodel.vo.plots;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoricalScatterPlotEntry {
    private String name;
    private Object x;
    private Object y;
    private Map<String, Object> tooltip;
}
