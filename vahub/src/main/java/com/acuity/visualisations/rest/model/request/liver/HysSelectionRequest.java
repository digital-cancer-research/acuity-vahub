package com.acuity.visualisations.rest.model.request.liver;

import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptionsFiltered;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.LiverGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Liver;
import com.esotericsoftware.kryo.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class HysSelectionRequest extends LiverRequest {
    @NotNull
    private ChartGroupByOptionsFiltered<Liver, LiverGroupByOptions> settings;

    private double minX;
    private double maxX;
    private double minY;
    private double maxY;
}
