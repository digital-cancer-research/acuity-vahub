package com.acuity.visualisations.rest.model.response.respiratory.exacerbation;


import com.acuity.visualisations.rawdatamodel.trellis.grouping.ExacerbationGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedBarChart;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Exacerbation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExacerbationBarChartResponse implements Serializable {
    private List<TrellisedBarChart<Exacerbation, ExacerbationGroupByOptions>> barChartData;
}
