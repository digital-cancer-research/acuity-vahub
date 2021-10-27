package com.acuity.visualisations.rest.model.request.biomarkers;

import com.acuity.visualisations.rawdatamodel.trellis.grouping.BiomarkerGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItem;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Biomarker;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BiomarkersHeatMapSelectionRequest extends BiomarkerRequest {
    private ChartSelection<Biomarker, BiomarkerGroupByOptions, ChartSelectionItem<Biomarker, BiomarkerGroupByOptions>> selection;
}
