package com.acuity.visualisations.rest.model.request.vitals;

import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptionsFiltered;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.VitalGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Vital;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class VitalsMeanRangeValuesRequest extends VitalsRequest {
    @NotNull
    private ChartGroupByOptionsFiltered<Vital, VitalGroupByOptions> settings;
}
