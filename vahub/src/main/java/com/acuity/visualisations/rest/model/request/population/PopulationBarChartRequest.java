package com.acuity.visualisations.rest.model.request.population;

import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptionsFiltered;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.PopulationGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class PopulationBarChartRequest extends PopulationRequest {
    @NotNull
    private ChartGroupByOptionsFiltered<Subject, PopulationGroupByOptions> settings;
    @NotNull
    private com.acuity.visualisations.rawdatamodel.axes.CountType countType;
}
