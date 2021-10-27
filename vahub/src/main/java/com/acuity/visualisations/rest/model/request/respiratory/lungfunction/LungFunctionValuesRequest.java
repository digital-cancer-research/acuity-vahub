package com.acuity.visualisations.rest.model.request.respiratory.lungfunction;

import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptionsFiltered;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.LungFunctionGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.LungFunction;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * Request for lung function values
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class LungFunctionValuesRequest extends LungFunctionRequest {
    @NotNull
    private ChartGroupByOptionsFiltered<LungFunction, LungFunctionGroupByOptions> settings;
}

