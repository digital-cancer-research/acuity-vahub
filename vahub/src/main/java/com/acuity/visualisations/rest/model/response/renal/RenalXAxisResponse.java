package com.acuity.visualisations.rest.model.response.renal;

import com.acuity.visualisations.rawdatamodel.axes.AxisOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.RenalGroupByOptions;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RenalXAxisResponse implements Serializable {
    private AxisOptions<RenalGroupByOptions> xaxis;
}
