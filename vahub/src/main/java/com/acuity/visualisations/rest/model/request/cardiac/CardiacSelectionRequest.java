package com.acuity.visualisations.rest.model.request.cardiac;

import com.acuity.visualisations.rawdatamodel.trellis.grouping.CardiacGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItemRange;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Cardiac;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class CardiacSelectionRequest extends CardiacRequest {
    @NotNull
    private ChartSelection<Cardiac, CardiacGroupByOptions,
            ChartSelectionItemRange<Cardiac, CardiacGroupByOptions, Double>> selection;
}
