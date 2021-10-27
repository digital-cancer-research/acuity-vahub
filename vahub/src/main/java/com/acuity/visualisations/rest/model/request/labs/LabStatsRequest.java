package com.acuity.visualisations.rest.model.request.labs;

import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptionsFiltered;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.LabGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.StatType;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Lab;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class LabStatsRequest extends LabsRequest {

    @NotNull
    private ChartGroupByOptionsFiltered<Lab, LabGroupByOptions> settings;

    @NotNull
    private StatType statType;

}
