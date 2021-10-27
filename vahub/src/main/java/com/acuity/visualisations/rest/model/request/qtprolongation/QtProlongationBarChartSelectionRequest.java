package com.acuity.visualisations.rest.model.request.qtprolongation;

import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItem;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.QtProlongationGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.QtProlongation;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class QtProlongationBarChartSelectionRequest extends QtProlongationRequest {
    @NotNull
    private ChartSelection<QtProlongation, QtProlongationGroupByOptions,
            ChartSelectionItem<QtProlongation, QtProlongationGroupByOptions>> selection;
}
