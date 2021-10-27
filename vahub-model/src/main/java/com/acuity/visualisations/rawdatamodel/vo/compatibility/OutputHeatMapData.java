package com.acuity.visualisations.rawdatamodel.vo.compatibility;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OutputHeatMapData implements Serializable {

    private List<String> xCategories = new ArrayList<>();
    private List<String> yCategories = new ArrayList<>();
    private List<OutputHeatMapEntry> entries = new ArrayList<>();
}
