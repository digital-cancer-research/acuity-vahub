package com.acuity.visualisations.rest.model.response.renal;

import com.acuity.visualisations.rawdatamodel.trellis.grouping.RenalGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedRangePlot;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Renal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RenalMeanRangeChartResponse implements Serializable {
    @NotNull
    private List<TrellisedRangePlot<Renal, RenalGroupByOptions>> meanRangeChart;
}
