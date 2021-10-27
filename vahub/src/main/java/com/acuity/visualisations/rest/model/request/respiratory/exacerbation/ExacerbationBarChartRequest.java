package com.acuity.visualisations.rest.model.request.respiratory.exacerbation;


import com.acuity.visualisations.rawdatamodel.axes.CountType;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptionsFiltered;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ExacerbationGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Exacerbation;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class ExacerbationBarChartRequest extends ExacerbationRequest {
    @NotNull
    private ChartGroupByOptionsFiltered<Exacerbation, ExacerbationGroupByOptions> settings;
    @NotNull
    private CountType countType;
}
