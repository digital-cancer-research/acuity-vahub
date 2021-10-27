package com.acuity.visualisations.rawdatamodel.service.compatibility;

import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.va.security.acl.domain.Datasets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by knml167 on 9/26/2017.
 */
@Service
public class TumorLineChartUIModelService<T, G extends Enum<G> & GroupByOption<T>> extends LineChartUIModelService<T, G> {

    @Autowired
    private TumourChartColoringService coloringService;

    @Override
    protected String getColor(Object colorBy, Datasets datasets, ChartGroupByOptions.GroupByOptionAndParams<T, G> colorByOption) {
        return coloringService.getColor(colorBy);
    }
}
