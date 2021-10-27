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

package com.acuity.visualisations.rawdatamodel.service.compatibility;


import com.acuity.visualisations.rawdatamodel.axes.CountType;
import com.acuity.visualisations.rawdatamodel.service.plots.vo.BarChartCalculationObject;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.GroupByKey;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.RenalGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.OutputBarChartData;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedBarChart;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Renal;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RenalBarChartUIModelServiceTest {
    private RenalBarChartUIModelService renalBarChartUIModelService = new RenalBarChartUIModelService(new BarChartColoringService());

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test
    public void testToTrellisedBarChart() {
        Map<GroupByKey<Renal, RenalGroupByOptions>, BarChartCalculationObject<Renal>> barChart = new HashMap<>();

        barChart.put(getBarChartKey("t1", "x1", "(Empty)"), new BarChartCalculationObject<>(Collections.emptySet(), Collections.emptySet(), 1,11.0, 1));
        barChart.put(getBarChartKey("t1", "x1", "ca"), new BarChartCalculationObject<>(Collections.emptySet(), Collections.emptySet(), 1,11.0, 1));
        barChart.put(getBarChartKey("t1", "x2", "ca"), new BarChartCalculationObject<>(Collections.emptySet(), Collections.emptySet(), 1,12.0, 2));
        barChart.put(getBarChartKey("t1", "x3", "cb"), new BarChartCalculationObject<>(Collections.emptySet(), Collections.emptySet(), 1,13.0, 3));
        barChart.put(getBarChartKey("t1", "x1", "ca"), new BarChartCalculationObject<>(Collections.emptySet(), Collections.emptySet(), 1,14.0, 4));
        barChart.put(getBarChartKey("t1", "x2", "cc"), new BarChartCalculationObject<>(Collections.emptySet(), Collections.emptySet(), 1,15.0, 5));
        //When
        List<TrellisedBarChart<Renal, RenalGroupByOptions>> result = renalBarChartUIModelService.toTrellisedBarChart(barChart, CountType.COUNT_OF_SUBJECTS);

        //Then
        softly.assertThat(result.size()).isEqualTo(1);
        softly.assertThat(result).extracting(e -> e.getTrellisedBy().get(0).getTrellisOption()).containsExactlyInAnyOrder("t1");
        List<? extends OutputBarChartData> data = result.get(0).getData();
        softly.assertThat(data).hasSize(4);
        softly.assertThat(data.get(0).getCategories()).containsExactlyInAnyOrder("x1", "x2", "x3");
        softly.assertThat(data).extracting(OutputBarChartData::getName).containsOnly("cc", "cb", "ca", "(Empty)");
    }

    private GroupByKey<Renal, RenalGroupByOptions> getBarChartKey(String t, String x, String c) {
        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> values = new HashMap<>();
        final HashMap<RenalGroupByOptions, Object> trellisByVaues = new HashMap<>();
        values.put(ChartGroupByOptions.ChartGroupBySetting.X_AXIS, x);
        values.put(ChartGroupByOptions.ChartGroupBySetting.COLOR_BY, c);
        trellisByVaues.put(RenalGroupByOptions.ACTUAL_VALUE, t);
        return new GroupByKey<>(values, trellisByVaues);
    }
}
