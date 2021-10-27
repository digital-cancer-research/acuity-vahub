package com.acuity.visualisations.rest.model.request.aes;

import com.acuity.visualisations.rawdatamodel.axes.CountType;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.AeGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItem;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Ae;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class AesBarChartSelectionRequest extends AesRequest {
    private CountType countType;
    @NotNull
    private ChartSelection<Ae, AeGroupByOptions,
            ChartSelectionItem<Ae, AeGroupByOptions>> selection;

}
