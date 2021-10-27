package com.acuity.visualisations.rest.model.request.tumour;

import com.acuity.visualisations.rawdatamodel.trellis.grouping.ATLGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItem;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.AssessedTargetLesion;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class TumourSelectionRequest extends TumourRequest {
    @NotNull
    private ChartSelection<AssessedTargetLesion, ATLGroupByOptions, ChartSelectionItem<AssessedTargetLesion, ATLGroupByOptions>> selection;
}
