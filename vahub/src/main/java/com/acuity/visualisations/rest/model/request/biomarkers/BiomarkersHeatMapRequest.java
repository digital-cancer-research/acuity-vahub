package com.acuity.visualisations.rest.model.request.biomarkers;

import com.acuity.visualisations.rawdatamodel.trellis.grouping.BiomarkerGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptionsFiltered;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Biomarker;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BiomarkersHeatMapRequest extends BiomarkerRequest {
    private ChartGroupByOptionsFiltered<Biomarker, BiomarkerGroupByOptions> settings;
}
