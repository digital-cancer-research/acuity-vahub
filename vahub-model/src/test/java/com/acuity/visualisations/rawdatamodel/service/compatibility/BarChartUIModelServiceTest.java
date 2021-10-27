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
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ExacerbationGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.GroupByKey;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.OutputBarChartData;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedBarChart;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Exacerbation;
import com.google.common.collect.ImmutableSet;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BarChartUIModelServiceTest {

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();
    private BarChartUIModelService barChartUIModelService = new BarChartUIModelService();

    Set<String> subjectSet1;
    Set<String> subjectSet2;
    Set<String> subjectSet3;
    Set<String> subjectSet4;
    Set<String> subjectSet5;

    @Before
    public void init() {
        String SUBJECT1 = "E01";
        String SUBJECT2 = "E02";
        String SUBJECT3 = "E03";
        String SUBJECT4 = "E04";
        String SUBJECT5 = "E05";
        String SUBJECT6 = "E06";
        String SUBJECT7 = "E07";
        String SUBJECT8 = "E08";
        String SUBJECT9 = "E09";
        String SUBJECT10 = "E10";
        String SUBJECT11 = "E11";

        subjectSet1 = ImmutableSet.of(SUBJECT1);
        subjectSet2 = ImmutableSet.of(SUBJECT2, SUBJECT3, SUBJECT4, SUBJECT5);
        subjectSet3 = ImmutableSet.of(SUBJECT6, SUBJECT7);
        subjectSet4 = ImmutableSet.of(SUBJECT8, SUBJECT9, SUBJECT10);
        subjectSet5 = ImmutableSet.of(SUBJECT11);
    }


    @Test
    public void testToTrellisedBarChartIfCountBySubjects() {
        Map<GroupByKey<Exacerbation, ExacerbationGroupByOptions>, BarChartCalculationObject<Exacerbation>> barChart = new HashMap<>();

        barChart.put(getBarChartKey("t1", "x1", "ca"), new BarChartCalculationObject<>(subjectSet1, Collections.emptySet(), 1, 11.0, 1));
        barChart.put(getBarChartKey("t1", "x2", "ca"), new BarChartCalculationObject<>(subjectSet2, Collections.emptySet(), 2, 12.0, 2));
        barChart.put(getBarChartKey("t1", "x3", "cb"), new BarChartCalculationObject<>(subjectSet3, Collections.emptySet(), 3, 13.0, 3));
        barChart.put(getBarChartKey("t2", "x1", "ca"), new BarChartCalculationObject<>(subjectSet4, Collections.emptySet(), 4, 14.0, 4));
        barChart.put(getBarChartKey("t2", "x2", "cc"), new BarChartCalculationObject<>(subjectSet5, Collections.emptySet(), 5, 15.0, 5));
        //When
        List<TrellisedBarChart<Exacerbation, ExacerbationGroupByOptions>> result = barChartUIModelService.toTrellisedBarChart(barChart, CountType.COUNT_OF_SUBJECTS);

        //Then
        softly.assertThat(result.size()).isEqualTo(2);
        softly.assertThat(result).extracting(e -> e.getTrellisedBy().get(0).getTrellisOption()).containsExactly("t1", "t2");
        List<? extends OutputBarChartData> data = result.stream()
                .filter(d -> "t1".equals(d.getTrellisedBy().get(0).getTrellisOption())).findFirst().get().getData();
        softly.assertThat(data).hasSize(2);
        softly.assertThat(data.get(0).getCategories()).containsExactly("x2", "x1", "x3");
    }

    @Test
    public void testToTrellisedBarChartIfCountByEvents() {
        Map<GroupByKey<Exacerbation, ExacerbationGroupByOptions>, BarChartCalculationObject<Exacerbation>> barChart = new HashMap<>();

        barChart.put(getBarChartKey("t1", "x1", "ca"), new BarChartCalculationObject<>(subjectSet1,  Collections.emptySet(),1, 11.0, 1));
        barChart.put(getBarChartKey("t1", "x2", "ca"), new BarChartCalculationObject<>(subjectSet2, Collections.emptySet(), 2, 12.0, 2));
        barChart.put(getBarChartKey("t1", "x3", "cb"), new BarChartCalculationObject<>(subjectSet3, Collections.emptySet(), 33, 13.0, 3));
        barChart.put(getBarChartKey("t2", "x1", "ca"), new BarChartCalculationObject<>(subjectSet4, Collections.emptySet(), 4, 14.0, 4));
        barChart.put(getBarChartKey("t2", "x2", "cc"), new BarChartCalculationObject<>(subjectSet5, Collections.emptySet(), 5, 15.0, 5));
        //When
        List<TrellisedBarChart<Exacerbation, ExacerbationGroupByOptions>> result = barChartUIModelService.toTrellisedBarChart(barChart, CountType.COUNT_OF_EVENTS);

        //Then
        softly.assertThat(result.size()).isEqualTo(2);
        softly.assertThat(result).extracting(e -> e.getTrellisedBy().get(0).getTrellisOption()).containsExactly("t1", "t2");
        List<? extends OutputBarChartData> data = result.stream()
                .filter(d -> "t1".equals(d.getTrellisedBy().get(0).getTrellisOption())).findFirst().get().getData();
        softly.assertThat(data).hasSize(2);
        softly.assertThat(data.get(0).getCategories()).containsExactly("x3", "x2", "x1");
    }

    @Test
    public void shouldSortCategoriesByNonUniqueSubjects() {
        Map<GroupByKey<Exacerbation, ExacerbationGroupByOptions>, BarChartCalculationObject<Exacerbation>> barChart = new HashMap<>();

        barChart.put(getBarChartKey("t1", "x2", "ca"), new BarChartCalculationObject<>(subjectSet3, Collections.emptySet(), 2, 11.0, 2));
        barChart.put(getBarChartKey("t1", "x2", "cb"), new BarChartCalculationObject<>(subjectSet3, Collections.emptySet(), 2, 12.0, 2));
        barChart.put(getBarChartKey("t1", "x1", "ca"), new BarChartCalculationObject<>(subjectSet4, Collections.emptySet(), 3, 13.0, 3));

        //When
        List<TrellisedBarChart<Exacerbation, ExacerbationGroupByOptions>> result = barChartUIModelService.toTrellisedBarChart(barChart, CountType.COUNT_OF_SUBJECTS);

        //Then
        List<? extends OutputBarChartData> data = result.stream().filter(d -> "t1".equals(d.getTrellisedBy().get(0).getTrellisOption())).findFirst().get().getData();
        softly.assertThat(data.get(0).getCategories()).containsExactly("x2", "x1");
    }

    private GroupByKey<Exacerbation, ExacerbationGroupByOptions> getBarChartKey(String trellisBy, String xAxisValue, String colorBy) {
        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> values = new HashMap<>();
        final HashMap<ExacerbationGroupByOptions, Object> trellisByValues = new HashMap<>();
        values.put(ChartGroupByOptions.ChartGroupBySetting.X_AXIS, xAxisValue);
        values.put(ChartGroupByOptions.ChartGroupBySetting.COLOR_BY, colorBy);
        trellisByValues.put(ExacerbationGroupByOptions.WITHDRAWAL, trellisBy);
        return new GroupByKey<>(values, trellisByValues);
    }
}
