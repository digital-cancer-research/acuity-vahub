package com.acuity.visualisations.rest.model.request.labs;

import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItemRange;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.LabGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Lab;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class LabSelectionRequest extends LabsRequest {

    @NotNull
    private ChartSelection<Lab, LabGroupByOptions, ChartSelectionItemRange<Lab, LabGroupByOptions, Double>> selection;

}
