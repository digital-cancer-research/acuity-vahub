package com.acuity.visualisations.rawdatamodel.vo.compatibility;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class OutputWaterfallData implements Serializable {
    private List<String> xCategories;
    private List<OutputWaterfallEntry> entries = new ArrayList<>();
}
