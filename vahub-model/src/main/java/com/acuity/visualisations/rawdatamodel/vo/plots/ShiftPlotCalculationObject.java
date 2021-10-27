package com.acuity.visualisations.rawdatamodel.vo.plots;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
@Builder
public final class ShiftPlotCalculationObject implements Serializable {
    private Object unit;
    private Double low;
    private Double high;
}
