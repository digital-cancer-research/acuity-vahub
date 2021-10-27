package com.acuity.visualisations.rawdatamodel.trellis.grouping;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CoordinateSelection {
    private Double minX;
    private Double maxX;
    private Double minY;
    private Double maxY;
    private String measurement;
    private String arm;
}
