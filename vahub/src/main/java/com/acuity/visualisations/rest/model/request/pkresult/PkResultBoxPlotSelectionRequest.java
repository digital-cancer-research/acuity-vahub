package com.acuity.visualisations.rest.model.request.pkresult;

import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItemRange;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.PkResultGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.PkResult;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class PkResultBoxPlotSelectionRequest extends PkResultRequest {
    @NotNull
    private ChartSelection<PkResult, PkResultGroupByOptions,
            ChartSelectionItemRange<PkResult, PkResultGroupByOptions, Double>> selection;
}
