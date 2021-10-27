package com.acuity.visualisations.rest.model.request.population;

import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItem;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.PopulationGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class PopulationBarChartSelectionRequest extends PopulationRequest {
    @NotNull
    private ChartSelection<Subject, PopulationGroupByOptions,
            ChartSelectionItem<Subject, PopulationGroupByOptions>> selection;
}
