package com.acuity.visualisations.rest.model.request.tumour;

import com.acuity.visualisations.rawdatamodel.trellis.grouping.ATLGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptionsFiltered;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.AssessedTargetLesion;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Request for "Target Lesion Diameters", "Target Lesion Diameters over time" and "Waterfall" plots
 * *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TumourTldRequest extends TumourRequest {
    private ChartGroupByOptionsFiltered<AssessedTargetLesion, ATLGroupByOptions> settings;
}
