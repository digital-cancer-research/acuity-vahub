package com.acuity.visualisations.rawdatamodel.vo.compatibility;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
public class ShiftPlotData implements Serializable {
    private String unit;
    private List<OutputShiftPlotEntry> data;
}
