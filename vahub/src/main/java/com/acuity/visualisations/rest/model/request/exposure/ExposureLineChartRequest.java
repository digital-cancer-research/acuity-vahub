package com.acuity.visualisations.rest.model.request.exposure;

import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptionsFiltered;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ExposureGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Exposure;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ExposureLineChartRequest extends ExposureRequest {
    @NonNull
    private ChartGroupByOptionsFiltered<Exposure, ExposureGroupByOptions> settings;
}
