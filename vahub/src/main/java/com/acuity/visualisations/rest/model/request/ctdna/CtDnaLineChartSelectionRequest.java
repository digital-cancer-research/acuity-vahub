package com.acuity.visualisations.rest.model.request.ctdna;

import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItem;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.CtDnaGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.CtDna;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class CtDnaLineChartSelectionRequest extends CtDnaLineChartRequest {
    @NotNull
    private ChartSelection<CtDna, CtDnaGroupByOptions,
            ChartSelectionItem<CtDna, CtDnaGroupByOptions>> selection;
}
