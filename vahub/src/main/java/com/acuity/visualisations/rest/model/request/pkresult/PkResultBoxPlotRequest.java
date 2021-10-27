package com.acuity.visualisations.rest.model.request.pkresult;

import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptionsFiltered;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.PkResultGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.PkResult;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class PkResultBoxPlotRequest extends PkResultRequest {
    @NotNull
    private ChartGroupByOptionsFiltered<PkResult, PkResultGroupByOptions> settings;

}
