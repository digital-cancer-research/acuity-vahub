package com.acuity.visualisations.rest.model.request.cievents;

import com.acuity.visualisations.rawdatamodel.trellis.grouping.CIEventGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItem;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.CIEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class BarChartSelectionRequest extends CIEventRequest {
    @NotNull
    private ChartSelection<CIEvent, CIEventGroupByOptions,
            ChartSelectionItem<CIEvent, CIEventGroupByOptions>> selection;
}
