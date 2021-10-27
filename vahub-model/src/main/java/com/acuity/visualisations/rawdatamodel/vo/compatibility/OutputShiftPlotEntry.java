package com.acuity.visualisations.rawdatamodel.vo.compatibility;

import com.acuity.visualisations.rawdatamodel.vo.plots.ShiftPlotCalculationObject;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Comparator;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
@Builder
public final class OutputShiftPlotEntry implements Comparable<OutputShiftPlotEntry>, Serializable {
    private Double x;
    private Double low;
    private Double high;
    private String unit;


    public static OutputShiftPlotEntry of(Double x, ShiftPlotCalculationObject entry) {
        final Object unit = entry.getUnit();
        return new OutputShiftPlotEntry(x, entry.getLow(), entry.getHigh(), unit == null ? "" : unit.toString());
    }

    @Override
    public int compareTo(OutputShiftPlotEntry o) {
        return Comparator.comparing(OutputShiftPlotEntry::getX).compare(this, o);
    }
}
