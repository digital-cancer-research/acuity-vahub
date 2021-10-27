package com.acuity.visualisations.rest.model.request.cievents;

import com.acuity.visualisations.rawdatamodel.axes.CountType;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.CIEventGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptionsFiltered;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.CIEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class CIEventBarChartRequest extends CIEventRequest {
    @NotNull
    private CountType countType;
    @NotNull
    private ChartGroupByOptionsFiltered<CIEvent, CIEventGroupByOptions> settings;
}
