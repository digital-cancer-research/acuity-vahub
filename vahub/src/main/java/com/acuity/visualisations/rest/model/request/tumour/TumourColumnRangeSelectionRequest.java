package com.acuity.visualisations.rest.model.request.tumour;

import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItem;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.TumourTherapyGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.TumourTherapy;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class TumourColumnRangeSelectionRequest extends TumourTherapyRequest {
    @NotNull
    private ChartSelection<TumourTherapy, TumourTherapyGroupByOptions, ChartSelectionItem<TumourTherapy, TumourTherapyGroupByOptions>> selection;
}
