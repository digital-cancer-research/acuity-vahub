package com.acuity.visualisations.rest.model.request.qtprolongation;

import com.acuity.visualisations.rawdatamodel.axes.CountType;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptionsFiltered;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.QtProlongationGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.QtProlongation;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class QtProlongationBarChartRequest extends QtProlongationRequest {
    @NotNull
    private CountType countType;
    @NotNull
    private ChartGroupByOptionsFiltered<QtProlongation, QtProlongationGroupByOptions> settings;
}
