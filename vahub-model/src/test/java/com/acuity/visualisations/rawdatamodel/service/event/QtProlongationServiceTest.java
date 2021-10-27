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

import com.acuity.visualisations.rawdatamodel.axes.AxisOption;
import com.acuity.visualisations.rawdatamodel.axes.AxisOptions;
import com.acuity.visualisations.rawdatamodel.axes.CountType;
import com.acuity.visualisations.rawdatamodel.dataproviders.PopulationDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.QtProlongationDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.filters.QtProlongationFilters;
import com.acuity.visualisations.rawdatamodel.service.dod.DoDCommonService;
import com.acuity.visualisations.rawdatamodel.test.TestConfig;
import com.acuity.visualisations.rawdatamodel.trellis.TrellisOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptionsFiltered;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItem;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.QtProlongationGroupByOptions;
import com.acuity.visualisations.rawdatamodel.util.DateUtils;
import com.acuity.visualisations.rawdatamodel.vo.QtProlongationRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.ColoredOutputBarChartData;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedBarChart;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.QtProlongation;
import com.acuity.va.security.acl.domain.Datasets;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.acuity.visualisations.rawdatamodel.Constants.DATASETS;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.COLOR_BY;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.X_AXIS;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.QtProlongationGroupByOptions.ALERT_LEVEL;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.toSet;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class QtProlongationServiceTest {
    private static final String ALERT_LEVEL_NONE = "None";
    private static final String ALERT_LEVEL_LOW = "Low";
    private static final String ALERT_LEVEL_MEDIUM = "Medium";
    private static final String ALERT_LEVEL_HIGH = "High";

    private static final Subject SUBJECT_1 = Subject.builder().subjectCode("E01").subjectId("01").studyPart("B").build();
    private static final Subject SUBJECT_2 = Subject.builder().subjectCode("E02").subjectId("02").build();
    private static final Subject SUBJECT_3 = Subject.builder().subjectCode("E03").subjectId("03").build();

    private static final QtProlongation QT_PROLONGATION_0
            = new QtProlongation(QtProlongationRaw.builder()
                                                  .id("0")
                                                  .alertLevel(ALERT_LEVEL_NONE)
                                                  .build(), SUBJECT_1);
    private static final QtProlongation QT_PROLONGATION_1
            = new QtProlongation(QtProlongationRaw.builder()
                                                  .id("1")
                                                  .alertLevel(ALERT_LEVEL_LOW)
                                                  .build(), SUBJECT_1);
    private static final QtProlongation QT_PROLONGATION_2
            = new QtProlongation(QtProlongationRaw.builder()
                                                  .id("2")
                                                  .alertLevel(ALERT_LEVEL_MEDIUM)
                                                  .build(), SUBJECT_1);
    private static final QtProlongation QT_PROLONGATION_3
            = new QtProlongation(QtProlongationRaw.builder()
                                                  .id("3")
                                                  .alertLevel(ALERT_LEVEL_LOW)
                                                  .build(), SUBJECT_1);
    private static final QtProlongation QT_PROLONGATION_4
            = new QtProlongation(QtProlongationRaw.builder()
                                                  .id("4")
                                                  .measurementCategory("ECG")
                                                  .measurementName("Summary (Mean) QT Duration")
                                                  .measurementTimePoint(DateUtils.toDateTime("2002-06-30T23:59:59"))
                                                  .daysOnStudy(126)
                                                  .visitNumber(1.)
                                                  .resultValue(335.)
                                                  .sourceName("algorithm")
                                                  .sourceVersion("1.2")
                                                  .sourceType("software")
                                                  .alertLevel(ALERT_LEVEL_HIGH)
                                                  .build(), SUBJECT_1);
    private static final QtProlongation QT_PROLONGATION_5
            = new QtProlongation(QtProlongationRaw.builder()
                                                  .id("5")
                                                  .alertLevel(ALERT_LEVEL_MEDIUM)
                                                  .build(), SUBJECT_2);
    private static final QtProlongation QT_PROLONGATION_6
            = new QtProlongation(QtProlongationRaw.builder()
                                                  .id("6")
                                                  .alertLevel(ALERT_LEVEL_HIGH)
                                                  .build(), SUBJECT_2);
    private static final QtProlongation QT_PROLONGATION_7
            = new QtProlongation(QtProlongationRaw.builder()
                                                  .id("7")
                                                  .alertLevel(ALERT_LEVEL_MEDIUM)
                                                  .build(), SUBJECT_2);
    private static final QtProlongation QT_PROLONGATION_8
            = new QtProlongation(QtProlongationRaw.builder()
                                                  .id("8")
                                                  .alertLevel(ALERT_LEVEL_LOW)
                                                  .build(), SUBJECT_2);
    private static final QtProlongation QT_PROLONGATION_9
            = new QtProlongation(QtProlongationRaw.builder()
                                                  .id("9")
                                                  .alertLevel(ALERT_LEVEL_HIGH)
                                                  .build(), SUBJECT_2);


    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();
    @Autowired
    private QtProlongationService qtProlongationService;
    @MockBean
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;
    @MockBean
    private QtProlongationDatasetsDataProvider qtProlongationDatasetsDataProvider;
    @Autowired
    private DoDCommonService dodCommonService;

    private static List<Subject> getSubjects() {
        return newArrayList(SUBJECT_1, SUBJECT_2, SUBJECT_3);
    }

    private static List<QtProlongation> getQtProlongationEvents() {
        return newArrayList(
                QT_PROLONGATION_0,
                QT_PROLONGATION_1,
                QT_PROLONGATION_2,
                QT_PROLONGATION_3,
                QT_PROLONGATION_4,
                QT_PROLONGATION_5,
                QT_PROLONGATION_6,
                QT_PROLONGATION_7,
                QT_PROLONGATION_8,
                QT_PROLONGATION_9
        );
    }

    @Before
    public void setUp() {
        when(qtProlongationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(getQtProlongationEvents());
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(getSubjects());
    }


    @Test
    public void shouldAvailableBarChartXAxisOptions() {
        //When
        AxisOptions<QtProlongationGroupByOptions> result
                = qtProlongationService.getAvailableBarChartXAxis(DATASETS, QtProlongationFilters.empty(), PopulationFilters.empty());
        //Tnen
        softly.assertThat(result.getOptions()).extracting(AxisOption::getGroupByOption).containsOnly(ALERT_LEVEL);
    }

    @Test
    public void shouldGetBarChartCountingEvents() throws Exception {
        //Given
        final ChartGroupByOptions.ChartGroupBySettingsBuilder<QtProlongation, QtProlongationGroupByOptions> settings = ChartGroupByOptions.builder();
        settings.withOption(COLOR_BY, QtProlongationGroupByOptions.ALERT_LEVEL.getGroupByOptionAndParams());
        settings.withOption(X_AXIS, QtProlongationGroupByOptions.ALERT_LEVEL.getGroupByOptionAndParams());
        final ChartGroupByOptionsFiltered.ChartGroupBySettingsFilteredBuilder<QtProlongation, QtProlongationGroupByOptions> settingsWithFilterBy =
                ChartGroupByOptionsFiltered.builder(settings.build());
        //When
        List<TrellisedBarChart<QtProlongation, QtProlongationGroupByOptions>> result = qtProlongationService.getBarChart(DATASETS, settingsWithFilterBy.build(),
                new QtProlongationFilters(), new PopulationFilters(), CountType.COUNT_OF_EVENTS);
        //Then
        softly.assertThat(result.size()).isEqualTo(1);
        List<ColoredOutputBarChartData> data = (List<ColoredOutputBarChartData>) result.get(0).getData();
        softly.assertThat(data).hasSize(4);
        softly.assertThat(data).extracting(ColoredOutputBarChartData::getName).containsOnly(ALERT_LEVEL_NONE, ALERT_LEVEL_LOW, ALERT_LEVEL_MEDIUM, ALERT_LEVEL_HIGH);
        softly.assertThat(data).extracting(ColoredOutputBarChartData::getColor).hasSize(4);
        softly.assertThat(data.get(0).getCategories()).containsExactlyInAnyOrder(ALERT_LEVEL_NONE, ALERT_LEVEL_LOW, ALERT_LEVEL_MEDIUM, ALERT_LEVEL_HIGH);
    }

    @Test
    public void shouldGetBarChartSelection() {
        // Given
        final ChartGroupByOptions.ChartGroupBySettingsBuilder<QtProlongation, QtProlongationGroupByOptions> settings = ChartGroupByOptions.builder();
        settings.withOption(X_AXIS, QtProlongationGroupByOptions.ALERT_LEVEL.getGroupByOptionAndParams());
        settings.withOption(COLOR_BY, QtProlongationGroupByOptions.ALERT_LEVEL.getGroupByOptionAndParams());
        final HashMap<QtProlongationGroupByOptions, Object> selectedTrellises = new HashMap<>();
        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItems = new HashMap<>();
        selectedItems.put(COLOR_BY, ALERT_LEVEL_LOW);
        selectedItems.put(X_AXIS, ALERT_LEVEL_LOW);
        final ChartSelectionItem<QtProlongation, QtProlongationGroupByOptions> selectionItem = ChartSelectionItem.of(selectedTrellises, selectedItems);
        // When
        final SelectionDetail result = qtProlongationService.getSelectionDetails(DATASETS,
                new QtProlongationFilters(), new PopulationFilters(), ChartSelection.of(settings.build(), Collections.singleton(selectionItem)));
        // Then
        softly.assertThat(result.getEventIds()).containsExactlyInAnyOrder(
                QT_PROLONGATION_1.getId(),
                QT_PROLONGATION_3.getId(),
                QT_PROLONGATION_8.getId()
        );
        softly.assertThat(result.getSubjectIds()).containsExactlyInAnyOrder("01", "02");
        softly.assertThat(result.getTotalEvents()).isEqualTo(10);
        softly.assertThat(result.getTotalSubjects()).isEqualTo(3);
    }

    @Test
    public void testGetTrellisOptions() {
        //When
        List<TrellisOptions<QtProlongationGroupByOptions>> result =
                qtProlongationService.getTrellisOptions(DATASETS, QtProlongationFilters.empty(), PopulationFilters.empty());
        //Then
        softly.assertThat(result.get(0)).isEqualTo(new TrellisOptions<>(ALERT_LEVEL,
                newArrayList(ALERT_LEVEL_HIGH, ALERT_LEVEL_LOW, ALERT_LEVEL_MEDIUM, ALERT_LEVEL_NONE)));
    }

    @Test
    public void shouldGetDetectDetailsOnDemand() {
        List<QtProlongation> qtProlongationList = newArrayList(QT_PROLONGATION_4, QT_PROLONGATION_2, QT_PROLONGATION_3);
        Set<String> qtProlongationIds = qtProlongationList.stream().map(QtProlongation::getId).collect(toSet());
        List<Map<String, String>> doDData = qtProlongationService.getDetailsOnDemandData(DATASETS, qtProlongationIds,
                Collections.emptyList(), 0, Integer.MAX_VALUE);
        QtProlongation qtProlongation = qtProlongationList.get(0);
        Map<String, String> dod = doDData.get(2);
        softly.assertThat(doDData).hasSize(qtProlongationList.size());
        softly.assertThat(dod.size()).isEqualTo(14);
        softly.assertThat(qtProlongation.getStudyId()).isEqualTo(dod.get("studyId"));
        softly.assertThat(qtProlongation.getStudyPart()).isEqualTo(dod.get("studyPart"));
        softly.assertThat(qtProlongation.getSubjectCode()).isEqualTo(dod.get("subjectId"));
        softly.assertThat(qtProlongation.getEvent().getMeasurementCategory()).isEqualTo(dod.get("measurementCategory"));
        softly.assertThat(qtProlongation.getEvent().getMeasurementName()).isEqualTo(dod.get("measurementName"));
        softly.assertThat("2002-06-30T23:59:59").isEqualTo(dod.get("measurementTimePoint"));
        softly.assertThat(qtProlongation.getEvent().getDaysOnStudy()).isEqualTo(Integer.parseInt(dod.get("daysOnStudy")));
        softly.assertThat(qtProlongation.getEvent().getVisitNumber()).isEqualTo(Double.parseDouble(dod.get("visitNumber")));
        softly.assertThat(qtProlongation.getEvent().getResultValue()).isEqualTo(Double.parseDouble(dod.get("resultValue")));
        softly.assertThat(qtProlongation.getEvent().getAlertLevel()).isEqualTo(dod.get("alertLevel"));
        softly.assertThat(qtProlongation.getEvent().getSourceName()).isEqualTo(dod.get("sourceName"));
        softly.assertThat(qtProlongation.getEvent().getSourceVersion()).isEqualTo(dod.get("sourceVersion"));
        softly.assertThat(qtProlongation.getEvent().getSourceType()).isEqualTo(dod.get("sourceType"));
    }
}
