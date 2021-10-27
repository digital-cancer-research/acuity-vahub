package com.acuity.visualisations.rest.model.request.renal;

import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItemRange;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.RenalGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Renal;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class RenalSelectionRequest extends RenalRequest {
    @NotNull
    private ChartSelection<Renal, RenalGroupByOptions, ChartSelectionItemRange<Renal, RenalGroupByOptions, Double>> selection;
}
