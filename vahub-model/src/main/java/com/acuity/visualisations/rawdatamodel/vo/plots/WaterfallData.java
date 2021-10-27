package com.acuity.visualisations.rawdatamodel.vo.plots;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@EqualsAndHashCode
public class WaterfallData implements Serializable {
    private List<String> xCategories;
    private  List<WaterfallEntry> data;
}
