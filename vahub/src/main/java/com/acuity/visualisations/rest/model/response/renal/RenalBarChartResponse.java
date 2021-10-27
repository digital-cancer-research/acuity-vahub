package com.acuity.visualisations.rest.model.response.renal;

import com.acuity.visualisations.rawdatamodel.trellis.grouping.RenalGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedBarChart;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Renal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RenalBarChartResponse implements Serializable {
    private List<TrellisedBarChart<Renal, RenalGroupByOptions>> barChartData;
}
