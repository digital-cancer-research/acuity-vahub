package com.acuity.visualisations.rest.model.request.renal;

import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItem;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.RenalGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Renal;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class RenalBarChartSelectionRequest extends RenalRequest {
    @NotNull
    private ChartSelection<Renal, RenalGroupByOptions, ChartSelectionItem<Renal, RenalGroupByOptions>> selection;
}
