package com.acuity.visualisations.rest.model.request.ctdna;

import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptionsFiltered;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.CtDnaGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.CtDna;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CtDnaLineChartRequest extends CtDnaRequest {
    private ChartGroupByOptionsFiltered<CtDna, CtDnaGroupByOptions> settings;
}
