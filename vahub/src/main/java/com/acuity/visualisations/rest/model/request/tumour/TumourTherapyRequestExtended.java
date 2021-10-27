package com.acuity.visualisations.rest.model.request.tumour;

import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptionsFiltered;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.TumourTherapyGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.TumourTherapy;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class TumourTherapyRequestExtended extends TumourTherapyRequest {
    @NotNull
    private ChartGroupByOptionsFiltered<TumourTherapy, TumourTherapyGroupByOptions> therapiesSettings;
}
