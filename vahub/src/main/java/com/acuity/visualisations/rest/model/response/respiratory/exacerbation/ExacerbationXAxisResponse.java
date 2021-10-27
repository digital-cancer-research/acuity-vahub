package com.acuity.visualisations.rest.model.response.respiratory.exacerbation;

import com.acuity.visualisations.rawdatamodel.axes.AxisOption;
import com.acuity.visualisations.rawdatamodel.axes.AxisOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ExacerbationGroupByOptions;

import java.util.List;

/**
 * Response for get x axis request
 */
public class ExacerbationXAxisResponse extends AxisOptions<ExacerbationGroupByOptions> {
    public ExacerbationXAxisResponse() {
        super(null, false, null);
    }

    public ExacerbationXAxisResponse(List<AxisOption<ExacerbationGroupByOptions>> axisOptions, boolean hasRandomization, List<String> drugs) {
        super(axisOptions, hasRandomization, drugs);
    }

    public ExacerbationXAxisResponse(AxisOptions<ExacerbationGroupByOptions> axisOptions) {
        super(axisOptions.getOptions(), axisOptions.isHasRandomization(), axisOptions.getDrugs());
    }
}
