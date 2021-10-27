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
import com.acuity.visualisations.rawdatamodel.trellis.grouping.CIEventGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.GroupByKey;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.ColoredOutputBarChartData;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.OutputBarChartData;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.OutputBarChartEntry;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedBarChart;
import com.acuity.visualisations.rawdatamodel.vo.plots.BarChartData;
import com.acuity.visualisations.rawdatamodel.vo.plots.BarChartEntry;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.CIEvent;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ColoredBarChartUIModelServiceTest {

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();
    private ColoredBarChartUIModelService coloredBarChartUIModelService = new ColoredBarChartUIModelService(new BarChartColoringService());

    @Test
    public void testToTrellisedBarChart() {


        Map<GroupByKey<CIEvent, CIEventGroupByOptions>, BarChartCalculationObject<CIEvent>> barChart = new HashMap<>();

        barChart.put(getBarChartKey("t1", "x1", "ca"), new BarChartCalculationObject<>(Collections.emptySet(), Collections.emptySet(), 1,11.0, 1));
        barChart.put(getBarChartKey("t1", "x2", "ca"), new BarChartCalculationObject<>(Collections.emptySet(), Collections.emptySet(), 1,12.0, 2));
        barChart.put(getBarChartKey("t1", "x3", "cb"), new BarChartCalculationObject<>(Collections.emptySet(), Collections.emptySet(), 1,13.0, 3));
        barChart.put(getBarChartKey("t2", "x1", "ca"), new BarChartCalculationObject<>(Collections.emptySet(), Collections.emptySet(), 1,14.0, 4));
        barChart.put(getBarChartKey("t2", "x2", "cc"), new BarChartCalculationObject<>(Collections.emptySet(), Collections.emptySet(), 1,15.0, 5));
        //When
        List<TrellisedBarChart<CIEvent, CIEventGroupByOptions>> result = coloredBarChartUIModelService.toTrellisedBarChart(barChart, CountType.COUNT_OF_SUBJECTS);

        //Then
        softly.assertThat(result.size()).isEqualTo(2);
        softly.assertThat(result).extracting(e -> e.getTrellisedBy().get(0).getTrellisOption()).containsExactlyInAnyOrder("t1", "t2");
        List<? extends ColoredOutputBarChartData> data = (List<? extends ColoredOutputBarChartData>) result.stream()
                .filter(d -> "t1".equals(d.getTrellisedBy().get(0).getTrellisOption())).findFirst().get().getData();
        softly.assertThat(data).hasSize(2);
        softly.assertThat(data).extracting(ColoredOutputBarChartData::getColor).containsOnly("#CC6677", "#F9DA00");
        softly.assertThat(data.get(0).getCategories()).containsExactlyInAnyOrder("x1", "x2", "x3");
    }

    private GroupByKey<CIEvent, CIEventGroupByOptions> getBarChartKey(String t, String x, String c) {
        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> values = new HashMap<>();
        final HashMap<CIEventGroupByOptions, Object> trellisByVaues = new HashMap<>();
        values.put(ChartGroupByOptions.ChartGroupBySetting.X_AXIS, x);
        values.put(ChartGroupByOptions.ChartGroupBySetting.COLOR_BY, c);
        trellisByVaues.put(CIEventGroupByOptions.CI_SYMPTOMS_DURATION, t);
        return new GroupByKey<>(values, trellisByVaues);
    }

    @Test
    public void testToTrellisedBarChartOnlyWithCounter() {
        //When
        String result = coloredBarChartUIModelService.getColor(4, "something");

        //Then
        softly.assertThat(result).isEqualTo("#006480");
    }

    @Test
    public void testToTrellisedBarChartOnlyWhenBarNameIsYes() {
        //When
        String result = coloredBarChartUIModelService.getColor(1, "Yes");

        //Then
        softly.assertThat(result).isEqualTo("#FE8C01");
    }

    @Test
    public void testToTrellisedBarChartOnlyWhenBarNameIsNo() {
        //When
        String result = coloredBarChartUIModelService.getColor(1, "No");

        //Then
        softly.assertThat(result).isEqualTo("#4363D8");
    }

    @Test
    public void testToTrellisedBarChartOnlyWhenBarNameIsEmpty() {
        //When
        String result = coloredBarChartUIModelService.getColor(1, "(Empty)");

        //Then
        softly.assertThat(result).isEqualTo("#B1C8ED");
    }

    @Test
    public void testGetColorWithAllTrellis() {
        //When
        String color = coloredBarChartUIModelService.getColor(1, "All");

        //Then
        softly.assertThat(color).isEqualTo("#DE606C");
    }

    @Test
    public void testCollectToOutputData() {
        BarChartEntry barChartEntry1_1 = new BarChartEntry("category1", 1.3, 3, Collections.emptySet());
        BarChartEntry barChartEntry1_2 = new BarChartEntry("category2", 1.4, 5, Collections.emptySet());
        BarChartData barChartData1 = new BarChartData(
                "(b) moderate", //YELLOW
                Arrays.asList("category1", "category2"),
                Arrays.asList(barChartEntry1_2, barChartEntry1_1)); // reversed, should be sorted inside service
        BarChartEntry barChartEntry2_1 = new BarChartEntry("category3", 1.3, 3, Collections.emptySet());
        BarChartEntry barChartEntry2_2 = new BarChartEntry("category4", 1.4, 5, Collections.emptySet());
        BarChartData barChartData2 = new BarChartData(
                "(c) severe", //ORANGE
                Arrays.asList("category3", "category4"),
                Arrays.asList(barChartEntry2_1, barChartEntry2_2)); // sorted already
        List<BarChartData> data = Arrays.asList(barChartData1, barChartData2);

        //When
        List<OutputBarChartData> outputBarChartData = coloredBarChartUIModelService.collectToOutputData(data, null);

        //Then series are sorted according to categories order
        ColoredOutputBarChartData coloredOutputBarChartData1 = (ColoredOutputBarChartData) outputBarChartData.get(0);
        softly.assertThat(coloredOutputBarChartData1.getSeries()).extracting(OutputBarChartEntry::getCategory)
                .containsExactly("category1", "category2");
        softly.assertThat(coloredOutputBarChartData1.getColor()).isEqualTo(AeColoringService.YELLOW);
        ColoredOutputBarChartData coloredOutputBarChartData2 = (ColoredOutputBarChartData) outputBarChartData.get(1);
        softly.assertThat(coloredOutputBarChartData2.getSeries()).extracting(OutputBarChartEntry::getCategory)
                .containsExactly("category3", "category4");
        softly.assertThat(coloredOutputBarChartData2.getColor()).isEqualTo(AeColoringService.ORANGE);
    }
}
