package com.acuity.visualisations.rest.model.request.cvot.endpoint;

import com.acuity.visualisations.rawdatamodel.axes.CountType;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptionsFiltered;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.CvotEndpointGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.CvotEndpoint;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class CvotEndpointBarChartRequest extends CvotEndpointRequest {
    @NotNull
    private CountType countType;
    @NotNull
    private ChartGroupByOptionsFiltered<CvotEndpoint, CvotEndpointGroupByOptions> settings;
}
