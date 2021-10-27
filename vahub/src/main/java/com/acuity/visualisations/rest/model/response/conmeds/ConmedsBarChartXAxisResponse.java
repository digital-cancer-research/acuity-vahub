package com.acuity.visualisations.rest.model.response.conmeds;

import com.acuity.visualisations.rawdatamodel.axes.AxisOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ConmedGroupByOptions;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConmedsBarChartXAxisResponse implements Serializable {
    private AxisOptions<ConmedGroupByOptions> xaxis;
}
