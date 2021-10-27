package com.acuity.visualisations.rest.model.request.cerebrovascular;

import com.acuity.visualisations.rawdatamodel.trellis.grouping.CerebrovascularGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItem;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Cerebrovascular;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class CerebrovascularBarChartSelectionRequest extends CerebrovascularRequest {
    @NotNull
    private ChartSelection<Cerebrovascular, CerebrovascularGroupByOptions,
            ChartSelectionItem<Cerebrovascular, CerebrovascularGroupByOptions>> selection;
}
