package com.acuity.visualisations.rawdatamodel.service;

import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItem;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;

/**
 * Created by knml167 on 6/16/2017.
 */
public interface SelectionSupportService<T, G extends Enum<G> & GroupByOption<T>, S extends ChartSelection<T, G, ? extends ChartSelectionItem<T, G>>> {
    SelectionDetail getSelectionDetails(FilterResult<T> filtered, S selection);
}
