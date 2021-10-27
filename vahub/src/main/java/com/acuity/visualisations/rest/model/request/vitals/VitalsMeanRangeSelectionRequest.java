package com.acuity.visualisations.rest.model.request.vitals;

import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItem;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.VitalGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Vital;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;


@Data
@EqualsAndHashCode(callSuper = true)
public class VitalsMeanRangeSelectionRequest extends VitalsRequest {
    @NotNull
    private ChartSelection<Vital, VitalGroupByOptions,
            ChartSelectionItem<Vital, VitalGroupByOptions>> selection;

}

