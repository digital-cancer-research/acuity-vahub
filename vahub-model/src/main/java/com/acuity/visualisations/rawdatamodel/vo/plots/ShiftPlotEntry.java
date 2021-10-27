package com.acuity.visualisations.rawdatamodel.vo.plots;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

import static com.acuity.visualisations.rawdatamodel.util.ObjectUtil.allNull;

@Data
@AllArgsConstructor
@EqualsAndHashCode
public final class ShiftPlotEntry implements Serializable {
    private Double x;
    private Double low;
    private Double high;

    private ShiftPlotEntry() {
    }

    public static ShiftPlotEntry empty() {
        return new ShiftPlotEntry();
    }

    public boolean isNotEmpty() {
        return !allNull(x, low, high);
    }
}
