package com.acuity.visualisations.rest.model.request.cvot.endpoint;

import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItem;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.CvotEndpointGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.CvotEndpoint;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class CvotEndpointBarChartSelectionRequest extends CvotEndpointRequest {
    @NotNull
    private ChartSelection<CvotEndpoint, CvotEndpointGroupByOptions,
            ChartSelectionItem<CvotEndpoint, CvotEndpointGroupByOptions>> selection;
}
