package com.acuity.visualisations.rest.model.request.labs;

import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItem;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.LabGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Lab;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class LabMeanRangeSelectionRequest extends LabsRequest {

    @NotNull
    private ChartSelection<Lab, LabGroupByOptions, ChartSelectionItem<Lab, LabGroupByOptions>> selection;

}
