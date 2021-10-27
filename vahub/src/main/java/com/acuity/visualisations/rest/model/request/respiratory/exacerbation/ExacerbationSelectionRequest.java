package com.acuity.visualisations.rest.model.request.respiratory.exacerbation;

import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItem;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ExacerbationGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Exacerbation;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class ExacerbationSelectionRequest extends ExacerbationRequest {
    @NotNull
    private ChartSelection<Exacerbation, ExacerbationGroupByOptions,
            ChartSelectionItem<Exacerbation, ExacerbationGroupByOptions>> selection;
}
