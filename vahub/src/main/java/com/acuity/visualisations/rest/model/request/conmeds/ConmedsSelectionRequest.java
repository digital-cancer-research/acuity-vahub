package com.acuity.visualisations.rest.model.request.conmeds;

import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItem;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ConmedGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Conmed;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class ConmedsSelectionRequest extends ConmedsRequest {
    @NotNull
    private ChartSelection<Conmed, ConmedGroupByOptions,
            ChartSelectionItem<Conmed, ConmedGroupByOptions>> selection;
}
