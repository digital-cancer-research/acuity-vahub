package com.acuity.visualisations.rawdatamodel.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class SelectionBox {
    private Double xMin;
    private Double xMax;
    private Double yMin;
    private Double yMax;
}
