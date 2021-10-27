package com.acuity.visualisations.rest.model.request.cardiac;

import com.acuity.visualisations.rawdatamodel.trellis.grouping.CardiacGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptionsFiltered;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Cardiac;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class CardiacValuesRequest extends CardiacRequest {
    @NotNull
    private ChartGroupByOptionsFiltered<Cardiac, CardiacGroupByOptions> settings;
}
