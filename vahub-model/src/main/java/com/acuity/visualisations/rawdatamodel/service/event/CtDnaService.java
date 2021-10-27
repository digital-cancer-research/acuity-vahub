/*
 * Copyright 2021 The University of Manchester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.acuity.visualisations.rawdatamodel.service.event;

import com.acuity.visualisations.rawdatamodel.axes.AxisOptions;
import com.acuity.visualisations.rawdatamodel.filters.CtDnaFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.service.BasePlotEventService;
import com.acuity.visualisations.rawdatamodel.service.compatibility.CtDnaLineChartUIModelService;
import com.acuity.visualisations.rawdatamodel.service.plots.CtDnaLineChartService;
import com.acuity.visualisations.rawdatamodel.service.plots.SimpleSelectionSupportService;
import com.acuity.visualisations.rawdatamodel.service.ssv.ColorInitializer;
import com.acuity.visualisations.rawdatamodel.trellis.TrellisOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItem;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.CtDnaGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.GroupByKey;
import com.acuity.visualisations.rawdatamodel.util.TrellisUtil;
import com.acuity.visualisations.rawdatamodel.vo.CtDnaRaw;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.linechart.OutputLineChartData;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.linechart.TrellisedLineFloatChart;
import com.acuity.visualisations.rawdatamodel.vo.plots.LineChartData;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.CtDna;
import com.acuity.va.security.acl.domain.Datasets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.COLOR_BY;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.X_AXIS;

@Service
public class CtDnaService extends BasePlotEventService<CtDnaRaw, CtDna, CtDnaGroupByOptions>
        implements SimpleSelectionSupportService<CtDna, CtDnaGroupByOptions>, ColorInitializer {

    @Autowired
    private CtDnaLineChartService lineChartService;

    @Autowired
    private CtDnaLineChartUIModelService ctDnaLineChartUIModelService;

    public List<TrellisedLineFloatChart<CtDna, CtDnaGroupByOptions, OutputLineChartData>> getLineChart(Datasets datasets,
                                                                                                       ChartGroupByOptions<CtDna, CtDnaGroupByOptions> settings,
                                                                                                       CtDnaFilters filters,
                                                                                                       PopulationFilters populationFilters) {
        final boolean isVisitNumberOption = settings.getOptions().get(X_AXIS).getGroupByOption() == CtDnaGroupByOptions.VISIT_NUMBER;
        FilterResult<CtDna> filtered = isVisitNumberOption
                ? getFilteredData(datasets, filters, populationFilters, null, e -> e.getEvent().getVisitNumber() != null)
                : getFilteredData(datasets, filters, populationFilters);
        Map<GroupByKey<CtDna, CtDnaGroupByOptions>, LineChartData> lineChart = lineChartService.getLineChart(filtered,
                settings);

        return ctDnaLineChartUIModelService.toTrellisedLineFloatChart(lineChart, datasets, settings.getOptions().get(COLOR_BY));
    }

    public AxisOptions<CtDnaGroupByOptions> getAvailableLineChartXAxis(Datasets datasets, CtDnaFilters filters,
                                                                       PopulationFilters populationFilters) {
        return getAxisOptions(datasets, filters, populationFilters, CtDnaGroupByOptions.SAMPLE_DATE, CtDnaGroupByOptions.VISIT_NUMBER,
                CtDnaGroupByOptions.VISIT_DATE);
    }

    public List<TrellisOptions<CtDnaGroupByOptions>> getColorBy(Datasets datasets, CtDnaFilters ctDnaFilters, PopulationFilters populationFilters) {

            FilterResult<CtDna> filtered = getFilteredData(datasets, ctDnaFilters, populationFilters);
            return TrellisUtil.getTrellisOptions(filtered.getFilteredResult(),
                    CtDnaGroupByOptions.SUBJECT, CtDnaGroupByOptions.GENE, CtDnaGroupByOptions.MUTATION);
    }

    public SelectionDetail getLineChartSelectionDetails(
            Datasets datasets,
            CtDnaFilters ctDnaFilters,
            PopulationFilters populationFilters,
            ChartSelection<CtDna, CtDnaGroupByOptions, ChartSelectionItem<CtDna, CtDnaGroupByOptions>> selection) {

        FilterResult<CtDna> filtered = getFilteredData(datasets, ctDnaFilters, populationFilters);

        return lineChartService.getSelectionDetails(filtered, selection);
    }

    @Override
    public void generateColors(Datasets datasets) {
        List<TrellisOptions<CtDnaGroupByOptions>> colorByOptions = getColorBy(datasets, CtDnaFilters.empty(), PopulationFilters.empty());
        ctDnaLineChartUIModelService.generateColors(datasets, colorByOptions);
    }
}
