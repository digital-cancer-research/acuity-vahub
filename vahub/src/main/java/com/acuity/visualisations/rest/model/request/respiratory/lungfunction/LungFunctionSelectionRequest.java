package com.acuity.visualisations.rest.model.request.respiratory.lungfunction;

import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItemRange;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.LungFunctionGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.LungFunction;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * Request for lung function selection
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class LungFunctionSelectionRequest extends LungFunctionRequest {
    @NotNull
    private ChartSelection<LungFunction, LungFunctionGroupByOptions,
            ChartSelectionItemRange<LungFunction, LungFunctionGroupByOptions, Double>> selection;

}

