package com.acuity.visualisations.rest.model.request.renal;

import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptionsFiltered;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.RenalGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Renal;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class RenalPlotValuesRequest extends RenalRequest {
    @NotNull
    private ChartGroupByOptionsFiltered<Renal, RenalGroupByOptions> settings;
}