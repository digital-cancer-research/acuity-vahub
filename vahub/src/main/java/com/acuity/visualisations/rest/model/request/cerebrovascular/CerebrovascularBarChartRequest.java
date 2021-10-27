package com.acuity.visualisations.rest.model.request.cerebrovascular;

import com.acuity.visualisations.rawdatamodel.axes.CountType;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.CerebrovascularGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptionsFiltered;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Cerebrovascular;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class CerebrovascularBarChartRequest extends CerebrovascularRequest {

    @NotNull
    private CountType countType;
    private ChartGroupByOptionsFiltered<Cerebrovascular, CerebrovascularGroupByOptions> settings;
}
