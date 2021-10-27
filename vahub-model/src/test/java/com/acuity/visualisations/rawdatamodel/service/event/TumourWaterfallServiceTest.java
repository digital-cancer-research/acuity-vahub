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

import com.acuity.visualisations.rawdatamodel.dataproviders.AssessedTargetLesionDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.PopulationDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.filters.AssessedTargetLesionFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.generators.AssessedTargetLesionGenerator;
import com.acuity.visualisations.rawdatamodel.test.TestConfig;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ATLGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.AssessmentAxisOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.AssessmentAxisOptions.AssessmentType;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptionsFiltered;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItem;
import com.acuity.visualisations.rawdatamodel.vo.AssessedTargetLesionRaw;
import com.acuity.visualisations.rawdatamodel.vo.AssessmentRaw;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.OutputWaterfallData;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.OutputWaterfallEntry;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedWaterfallChart;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.AssessedTargetLesion;
import com.acuity.va.security.acl.domain.Datasets;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_2_ACUITY_DATASETS;
import static com.acuity.visualisations.rawdatamodel.service.event.TumourWaterfallService.WITH_BEST_RESPONSE_EVENTS;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.COLOR_BY;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.X_AXIS;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.Y_AXIS;
import static com.acuity.visualisations.rawdatamodel.vo.GroupByOption.Param.VALUE;
import static com.acuity.visualisations.rawdatamodel.vo.GroupByOption.Param.ASSESSMENT_TYPE;
import static com.acuity.visualisations.rawdatamodel.vo.GroupByOption.Param.WEEK_NUMBER;
import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class TumourWaterfallServiceTest {

    @Autowired
    private TumourWaterfallService tumourService;
    @MockBean
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;
    @MockBean
    private AssessedTargetLesionDatasetsDataProvider tumourDatasetsDataProvider;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(tumourDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(tumours);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(population);
    }

    private List<AssessedTargetLesion> tumours = AssessedTargetLesionGenerator.generateTumours();
    private List<Subject> population = AssessedTargetLesionGenerator.generateTumourPopulation();

    @Test
    public void shouldGetTumoursWeek4OnWaterfall() {

        List<TrellisedWaterfallChart<AssessedTargetLesion, ATLGroupByOptions>> waterfallChart =
                tumourService.getTumourDataOnWaterfall(DUMMY_2_ACUITY_DATASETS, AssessedTargetLesionFilters.empty(), PopulationFilters.empty(),
                        getWaterfallSettingsFiltered(AssessmentAxisOptions.AssessmentType.WEEK, 4, false));

        softly.assertThat(waterfallChart).hasSize(1);
        final OutputWaterfallData data = waterfallChart.get(0).getData();
        softly.assertThat(data.getXCategories()).containsExactly("subject1", "subject2");
        softly.assertThat(data.getEntries()).extracting(OutputWaterfallEntry::getX, OutputWaterfallEntry::getY)
                .containsExactly(
                        tuple(0, -25.0),
                        tuple(1, -30.0));
    }

    @Test
    public void testGetWaterfallSelectionForBestChange() {

        final HashMap<ATLGroupByOptions, Object> selectedTrellises = new HashMap<>();
        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItem1 = new HashMap<>();
        selectedItem1.put(X_AXIS, "subject1"); // best assessment and best percentage change happened at one visit, so 1 event

        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItem2 = new HashMap<>();
        selectedItem2.put(X_AXIS, "subject2"); // best assessment and best percentage change happened at different visits, so 2 events

        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItem3 = new HashMap<>();
        selectedItem3.put(X_AXIS, "subject3"); // missing target lesions, so 1 event

        SelectionDetail selectionDetails = tumourService.getWaterfallSelectionDetails(DUMMY_2_ACUITY_DATASETS,
                AssessedTargetLesionFilters.empty(), PopulationFilters.empty(),
                ChartSelection.of(getDefaultWaterfallSelectionSettings(), newArrayList(ChartSelectionItem.of(selectedTrellises, selectedItem1),
                        ChartSelectionItem.of(selectedTrellises, selectedItem2), ChartSelectionItem.of(selectedTrellises, selectedItem3))),
                getWaterfallSettingsFiltered(AssessmentAxisOptions.AssessmentType.BEST_CHANGE, 0, true));

        softly.assertThat(selectionDetails).extracting(s -> s.getEventIds().size(), s -> s.getSubjectIds().size(), SelectionDetail::getTotalEvents,
                SelectionDetail::getTotalSubjects).containsExactly(4, 3, 5, 4);
    }

    @Test
    public void testGetWaterfallSelectionForWeek4() {

        final HashMap<ATLGroupByOptions, Object> selectedTrellises = new HashMap<>();
        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItem1 = new HashMap<>();
        selectedItem1.put(X_AXIS, "subject1"); // best assessment and percentage change measured at one visit, so 1 event

        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItem2 = new HashMap<>();
        selectedItem2.put(X_AXIS, "subject2"); // best assessment and percentage change measured at different visits, so 2 events

        SelectionDetail selectionDetails = tumourService.getWaterfallSelectionDetails(DUMMY_2_ACUITY_DATASETS,
                AssessedTargetLesionFilters.empty(), PopulationFilters.empty(),
                ChartSelection.of(getDefaultWaterfallSelectionSettings(), newArrayList(ChartSelectionItem.of(selectedTrellises, selectedItem1),
                        ChartSelectionItem.of(selectedTrellises, selectedItem2))),
                getWaterfallSettingsFiltered(AssessmentAxisOptions.AssessmentType.WEEK, 4, true));

        softly.assertThat(selectionDetails).extracting(s -> s.getEventIds().size(), s -> s.getSubjectIds().size(), SelectionDetail::getTotalEvents,
                SelectionDetail::getTotalSubjects).containsExactly(3, 2, 3, 4);
    }

    private ChartGroupByOptionsFiltered<AssessedTargetLesion, ATLGroupByOptions> getWaterfallSettingsFiltered(AssessmentType yAxisParam,
                                                                                                              int week, boolean isNotPlotRequest) {
        ChartGroupByOptions<AssessedTargetLesion, ATLGroupByOptions> settings = ChartGroupByOptions.<AssessedTargetLesion, ATLGroupByOptions>builder()
                .withOption(Y_AXIS,
                        ATLGroupByOptions.PERCENTAGE_CHANGE.getGroupByOptionAndParams(GroupByOption.Params.builder()
                                .with(GroupByOption.Param.ASSESSMENT_TYPE, yAxisParam)
                                .with(GroupByOption.Param.WEEK_NUMBER, week)
                                .with(VALUE, isNotPlotRequest ? WITH_BEST_RESPONSE_EVENTS : "")
                                .build()))
                .withOption(X_AXIS, ATLGroupByOptions.SUBJECT.getGroupByOptionAndParams())
                .withOption(COLOR_BY, ATLGroupByOptions.BEST_RESPONSE.getGroupByOptionAndParams())
                .build();
        return ChartGroupByOptionsFiltered.builder(settings).build();
    }

    private ChartGroupByOptions<AssessedTargetLesion, ATLGroupByOptions> getDefaultWaterfallSelectionSettings() {
        return ChartGroupByOptions.<AssessedTargetLesion, ATLGroupByOptions>builder()
                .withOption(X_AXIS, ATLGroupByOptions.SUBJECT.getGroupByOptionAndParams())
                .build();
    }

    @Test
    public void testOptional() {
        List<AssessedTargetLesionRaw> atlsPerAssessment = new ArrayList<>();
        AssessmentRaw.Response response = atlsPerAssessment.stream().findFirst()
                .map(atl -> AssessmentRaw.Response.getInstance(atl.getResponse())).orElse(AssessmentRaw.Response.NO_ASSESSMENT);
        softly.assertThat(response == AssessmentRaw.Response.NO_ASSESSMENT);
        atlsPerAssessment.add(AssessedTargetLesionRaw.builder().response("Unknown string").build());
        response = atlsPerAssessment.stream().findFirst()
                .map(atl -> AssessmentRaw.Response.getInstance(atl.getResponse())).orElse(AssessmentRaw.Response.NO_ASSESSMENT);
        softly.assertThat(response == AssessmentRaw.Response.NO_ASSESSMENT);
    }

    @Test
    public void testGetColorByBestChange(){
        ChartGroupByOptions<AssessedTargetLesion, ATLGroupByOptions> settingsBestResponse = ChartGroupByOptions.<AssessedTargetLesion, ATLGroupByOptions>builder()
                .withOption(Y_AXIS, ATLGroupByOptions.PERCENTAGE_CHANGE.getGroupByOptionAndParams(GroupByOption.Params.builder()
                        .with(ASSESSMENT_TYPE, AssessmentAxisOptions.AssessmentType.BEST_CHANGE)
                        .with(WEEK_NUMBER,  0)
                        .build()))
                .build();

        ChartGroupByOptionsFiltered<AssessedTargetLesion, ATLGroupByOptions> settingsBestResponseFiltered = ChartGroupByOptionsFiltered.builder(settingsBestResponse).build();
        softly.assertThat(
                tumourService.getWaterfallColorBy(DUMMY_2_ACUITY_DATASETS, PopulationFilters.empty(), AssessedTargetLesionFilters.empty(), settingsBestResponseFiltered).stream()
                        .map(to -> to.getTrellisedBy()))
                .containsExactly(ATLGroupByOptions.BEST_RESPONSE);
    }

    @Test
    public void testGetColorByWeek() {
        ChartGroupByOptions<AssessedTargetLesion, ATLGroupByOptions> settingsAssessmentResponse = ChartGroupByOptions.<AssessedTargetLesion, ATLGroupByOptions>builder()
                .withOption(Y_AXIS, ATLGroupByOptions.PERCENTAGE_CHANGE.getGroupByOptionAndParams(GroupByOption.Params.builder()
                        .with(ASSESSMENT_TYPE, AssessmentAxisOptions.AssessmentType.WEEK)
                        .with(WEEK_NUMBER,  0)
                        .build()))
                .build();

        ChartGroupByOptionsFiltered<AssessedTargetLesion, ATLGroupByOptions> settingsAssessmentResponseFiltered = ChartGroupByOptionsFiltered.builder(settingsAssessmentResponse).build();
        softly.assertThat(
                tumourService.getWaterfallColorBy(DUMMY_2_ACUITY_DATASETS, PopulationFilters.empty(), AssessedTargetLesionFilters.empty(), settingsAssessmentResponseFiltered).stream()
                .map(to -> to.getTrellisedBy()))
                .containsExactly(ATLGroupByOptions.ASSESSMENT_RESPONSE, ATLGroupByOptions.BEST_RESPONSE);
    }

}
