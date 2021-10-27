package com.acuity.visualisations.rest.model.request.conmeds;


import com.acuity.visualisations.rawdatamodel.axes.CountType;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptionsFiltered;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ConmedGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Conmed;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class ConmedsCountsBarChartRequest extends ConmedsRequest {
    @NotNull
    private ChartGroupByOptionsFiltered<Conmed, ConmedGroupByOptions> settings;
    @NotNull
    private CountType countType;
}
