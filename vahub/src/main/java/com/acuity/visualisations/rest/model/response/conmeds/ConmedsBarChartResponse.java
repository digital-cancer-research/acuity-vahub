package com.acuity.visualisations.rest.model.response.conmeds;


import com.acuity.visualisations.rawdatamodel.trellis.grouping.ConmedGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedBarChart;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Conmed;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConmedsBarChartResponse implements Serializable {
    private List<TrellisedBarChart<Conmed, ConmedGroupByOptions>> barChartData;
}
