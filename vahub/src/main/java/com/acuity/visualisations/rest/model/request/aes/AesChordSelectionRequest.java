package com.acuity.visualisations.rest.model.request.aes;

import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItem;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChordGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.plots.ChordCalculationObject;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
public class AesChordSelectionRequest extends AesRequest {
    @NotNull
    private ChartSelection<ChordCalculationObject, ChordGroupByOptions,
            ChartSelectionItem<ChordCalculationObject, ChordGroupByOptions>> selection;
    @NotNull
    private Map<String, String> additionalSettings;
}
