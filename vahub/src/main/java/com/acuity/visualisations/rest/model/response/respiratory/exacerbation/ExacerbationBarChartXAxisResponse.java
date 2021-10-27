package com.acuity.visualisations.rest.model.response.respiratory.exacerbation;

import com.acuity.visualisations.rawdatamodel.axes.AxisOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ExacerbationGroupByOptions;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExacerbationBarChartXAxisResponse implements Serializable {
    private AxisOptions<ExacerbationGroupByOptions> xaxis;
}
