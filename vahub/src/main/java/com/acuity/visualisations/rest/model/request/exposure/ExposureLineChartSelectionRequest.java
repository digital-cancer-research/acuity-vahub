package com.acuity.visualisations.rest.model.request.exposure;

import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItem;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ExposureGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Exposure;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class ExposureLineChartSelectionRequest extends ExposureLineChartRequest {
    @NotNull
    private ChartSelection<Exposure, ExposureGroupByOptions,
            ChartSelectionItem<Exposure, ExposureGroupByOptions>> selection;
}
