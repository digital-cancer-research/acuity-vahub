package com.acuity.visualisations.rest.model.request.tumour;

import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptionsFiltered;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.PopulationGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class TumourColumnRangeRequest extends TumourTherapyRequestExtended {
    @NotNull
    private ChartGroupByOptionsFiltered<Subject, PopulationGroupByOptions> tocSettings;
}
