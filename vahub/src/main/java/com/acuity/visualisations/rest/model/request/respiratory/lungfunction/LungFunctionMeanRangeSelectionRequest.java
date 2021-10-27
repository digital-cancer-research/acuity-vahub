package com.acuity.visualisations.rest.model.request.respiratory.lungfunction;

import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItem;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.LungFunctionGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.LungFunction;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;


@Data
@EqualsAndHashCode(callSuper = true)
public class LungFunctionMeanRangeSelectionRequest extends LungFunctionRequest {
    @NotNull
    private ChartSelection<LungFunction, LungFunctionGroupByOptions,
            ChartSelectionItem<LungFunction, LungFunctionGroupByOptions>> selection;

}

