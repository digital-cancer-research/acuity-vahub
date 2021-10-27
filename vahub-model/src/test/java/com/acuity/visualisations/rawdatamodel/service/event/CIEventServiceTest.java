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
import com.acuity.visualisations.rawdatamodel.dataproviders.CIEventDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.PopulationDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.filters.CIEventFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.test.TestConfig;
import com.acuity.visualisations.rawdatamodel.trellis.TrellisOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.CIEventGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptionsFiltered;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItem;
import com.acuity.visualisations.rawdatamodel.util.DateUtils;
import com.acuity.visualisations.rawdatamodel.vo.CIEventRaw;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.ColoredOutputBarChartData;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.OutputBarChartData;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.OutputBarChartEntry;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.OutputOvertimeData;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedBarChart;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedOvertime;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.CIEvent;
import com.acuity.va.security.acl.domain.Datasets;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_CVOT_DATASETS;
import static com.acuity.visualisations.rawdatamodel.Constants.DATASETS;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.CIEventGroupByOptions.CI_SYMPTOMS_DURATION;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.CIEventGroupByOptions.CORONARY_ANGIOGRAPHY;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.CIEventGroupByOptions.DID_SYMPTOMS_PROMPT_UNS_HOSP;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.CIEventGroupByOptions.ECG_AT_THE_EVENT_TIME;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.CIEventGroupByOptions.EVENT_SUSP_DUE_TO_STENT_THROMB;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.CIEventGroupByOptions.FINAL_DIAGNOSIS;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.CIEventGroupByOptions.ISHEMIC_SYMTOMS;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.CIEventGroupByOptions.PREVIOUS_ECG_AVAILABLE;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.CIEventGroupByOptions.Param;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.CIEventGroupByOptions.Params;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.CIEventGroupByOptions.START_DATE;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.CIEventGroupByOptions.TimestampType;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.CIEventGroupByOptions.WERE_LOCAL_CARDIAC_BIOMARKERS_DRAWN;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.COLOR_BY;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.X_AXIS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class CIEventServiceTest {

    public static final String FINAL_DIAGNOSIS_1 = "finalDiagnosis1";
    public static final String FINAL_DIAGNOSIS_2 = "finalDiagnosis2";
    public static final String FINAL_DIAGNOSIS_3 = "finalDiagnosis3";
    public static final String SYMPTOM = "symptom";
    public static final String CIE_SYMPTOMS_DURATION_1 = "cieSymptomsDuration1";
    public static final String CIE_SYMPTOMS_DURATION_2 = "cieSymptomsDuration2";
    public static final Subject SUBJECT;

    static {
        final HashMap<String, Date> drugFirstDoseDate1 = new HashMap<>();
        drugFirstDoseDate1.put("drug1", DateUtils.toDate("01.01.2000"));
        drugFirstDoseDate1.put("drug2", DateUtils.toDate("01.10.2000"));
        SUBJECT = Subject.builder()
                .subjectId("1")
                .firstTreatmentDate(DateUtils.toDate("01.01.2000"))
                .dateOfRandomisation(DateUtils.toDate("01.12.1999"))
                .studyLeaveDate(DateUtils.toDate("01.12.2001"))
                .drugFirstDoseDate(drugFirstDoseDate1)
                .build();
    }

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();
    @Autowired
    private CIEventService ciEventService;
    @MockBean
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;
    @MockBean
    private CIEventDatasetsDataProvider ciEventDatasetsDataProvider;

    private static List<Subject> getSubjects() {
        return Collections.singletonList(SUBJECT);
    }

    private static List<CIEvent> getCiEvents() {
        CIEvent ciEvent1 = new CIEvent(CIEventRaw.builder()
                .id("id1")
                .ischemicSymptoms(SYMPTOM)
                .coronaryAngiography("Yes")
                .finalDiagnosis(FINAL_DIAGNOSIS_1)
                .cieSymptomsDuration(CIE_SYMPTOMS_DURATION_2)
                .symptPromptUnschedHospit("Yes")
                .localCardiacBiomarkersDrawn("No")
                .startDate(DateUtils.toDate("01.02.2000"))
                .build(), SUBJECT);
        CIEvent ciEvent2 = new CIEvent(CIEventRaw.builder()
                .id("id2")
                .ecgAtTheEventTime("No")
                .coronaryAngiography("No")
                .previousEcgAvailable("Yes")
                .finalDiagnosis(FINAL_DIAGNOSIS_2)
                .symptPromptUnschedHospit("No")
                .eventSuspDueToStentThromb("Yes")
                .startDate(DateUtils.toDate("01.03.2000"))
                .build(), SUBJECT);
        CIEvent ciEvent3 = new CIEvent(CIEventRaw.builder()
                .id("id3")
                .ecgAtTheEventTime("Yes")
                .previousEcgAvailable("No")
                .cieSymptomsDuration(CIE_SYMPTOMS_DURATION_1)
                .eventSuspDueToStentThromb("No")
                .localCardiacBiomarkersDrawn("Yes")
                .finalDiagnosis(FINAL_DIAGNOSIS_3)
                .startDate(DateUtils.toDate("01.04.2000"))
                .build(), SUBJECT);
        return Arrays.asList(ciEvent1, ciEvent2, ciEvent3);
    }

    @Test
    public void shouldGetTrellisOptions() throws Exception {
        //Given
        final List<CIEvent> events = getCiEvents();
        when(ciEventDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events.stream().map(e -> e.getSubject()).collect(Collectors.toList()));

        //When
        List<TrellisOptions<CIEventGroupByOptions>> result
                = ciEventService.getTrellisOptions(DATASETS, CIEventFilters.empty(), PopulationFilters.empty());

        //Then
        softly.assertThat(result.size()).isEqualTo(9);
        softly.assertThat(result.get(0)).isEqualTo(new TrellisOptions<>(FINAL_DIAGNOSIS,
                Arrays.asList(FINAL_DIAGNOSIS_1, FINAL_DIAGNOSIS_2, FINAL_DIAGNOSIS_3)));
        softly.assertThat(result.get(1)).isEqualTo(new TrellisOptions<>(CIEventGroupByOptions.ISHEMIC_SYMTOMS,
                Arrays.asList(SYMPTOM, "(Empty)")));
        softly.assertThat(result.get(2)).isEqualTo(new TrellisOptions<>(CIEventGroupByOptions.CI_SYMPTOMS_DURATION,
                Arrays.asList(CIE_SYMPTOMS_DURATION_1, CIE_SYMPTOMS_DURATION_2, "(Empty)")));
        softly.assertThat(result.get(3)).isEqualTo(new TrellisOptions<>(CIEventGroupByOptions.DID_SYMPTOMS_PROMPT_UNS_HOSP,
                Arrays.asList("No", "Yes", "(Empty)")));
        softly.assertThat(result.get(4)).isEqualTo(new TrellisOptions<>(CIEventGroupByOptions.EVENT_SUSP_DUE_TO_STENT_THROMB,
                Arrays.asList("No", "Yes", "(Empty)")));
        softly.assertThat(result.get(5)).isEqualTo(new TrellisOptions<>(CIEventGroupByOptions.PREVIOUS_ECG_AVAILABLE,
                Arrays.asList("No", "Yes", "(Empty)")));
        softly.assertThat(result.get(6)).isEqualTo(new TrellisOptions<>(CIEventGroupByOptions.ECG_AT_THE_EVENT_TIME,
                Arrays.asList("No", "Yes", "(Empty)")));
        softly.assertThat(result.get(7)).isEqualTo(new TrellisOptions<>(CIEventGroupByOptions.WERE_LOCAL_CARDIAC_BIOMARKERS_DRAWN,
                Arrays.asList("No", "Yes", "(Empty)")));
        softly.assertThat(result.get(8)).isEqualTo(new TrellisOptions<>(CIEventGroupByOptions.CORONARY_ANGIOGRAPHY,
                Arrays.asList("No", "Yes", "(Empty)")));

    }

    @Test
    public void shouldAvailableBarChartXAxisOptions() {
        //Given
        final List<CIEvent> events = getCiEvents();
        when(ciEventDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events.stream().map(e -> e.getSubject()).collect(Collectors.toList()));

        //When
        AxisOptions<CIEventGroupByOptions> result
                = ciEventService.getAvailableBarChartXAxis(DATASETS, CIEventFilters.empty(), PopulationFilters.empty());

        //Tnen
        softly.assertThat(result.getOptions()).extracting(AxisOption::getGroupByOption).containsOnly(
                FINAL_DIAGNOSIS,
                ISHEMIC_SYMTOMS,
                CI_SYMPTOMS_DURATION,
                DID_SYMPTOMS_PROMPT_UNS_HOSP,
                EVENT_SUSP_DUE_TO_STENT_THROMB,
                PREVIOUS_ECG_AVAILABLE,
                ECG_AT_THE_EVENT_TIME,
                WERE_LOCAL_CARDIAC_BIOMARKERS_DRAWN,
                CORONARY_ANGIOGRAPHY);
    }

    @Test
    public void shouldAvailableOvertimeChartXAxisOptions() {
        //Given
        when(ciEventDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(getCiEvents());
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(getSubjects());

        //When
        AxisOptions<CIEventGroupByOptions> availableOvertimeXAxis
                = ciEventService.getAvailableOverTimeChartXAxis(DATASETS, CIEventFilters.empty(), PopulationFilters.empty());

        //Then
        softly.assertThat(availableOvertimeXAxis.getDrugs()).isNotEmpty();
        softly.assertThat(availableOvertimeXAxis.getDrugs()).containsExactlyInAnyOrder("drug1", "drug2");
        softly.assertThat(availableOvertimeXAxis.isHasRandomization()).isTrue();
        softly.assertThat(availableOvertimeXAxis.getOptions())
                .extracting(AxisOption::getGroupByOption, AxisOption::isBinableOption, AxisOption::isTimestampOption, AxisOption::isSupportsDuration)
                .containsExactly(
                        tuple(START_DATE, true, true, false)
                );
    }

    @Test
    public void shouldGetBarChartCountingEvents() throws Exception {
        //Given
        final ChartGroupByOptions.ChartGroupBySettingsBuilder<CIEvent, CIEventGroupByOptions> settings = ChartGroupByOptions.builder();
        settings.withOption(COLOR_BY, CIEventGroupByOptions.FINAL_DIAGNOSIS.getGroupByOptionAndParams());
        settings.withOption(X_AXIS, CIEventGroupByOptions.ECG_AT_THE_EVENT_TIME.getGroupByOptionAndParams());
//        final ChartGroupByOptions.GroupByOptionAndParams<CIEventGroupByOptions> trellis = ARM.getGroupByOptionAndParams();
//        settings.withTrellisOptions(new HashSet<>(Collections.singletonList(trellis)));
        final ChartGroupByOptionsFiltered.ChartGroupBySettingsFilteredBuilder<CIEvent, CIEventGroupByOptions> settingsWithFilterBy =
                ChartGroupByOptionsFiltered.builder(settings.build());
//        settingsWithFilterBy.withFilterByTrellisOption(ARM, Arrays.asList("arm1", "arm2"));


        final List<CIEvent> events = getCiEvents();
        when(ciEventDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events.stream().map(e -> e.getSubject()).collect(Collectors.toList()));

        //When
        List<TrellisedBarChart<CIEvent, CIEventGroupByOptions>> result = ciEventService.getBarChart(DATASETS, settingsWithFilterBy.build(),
                new CIEventFilters(), new PopulationFilters(), CountType.COUNT_OF_EVENTS);

        //Then
        softly.assertThat(result.size()).isEqualTo(1);
        List<ColoredOutputBarChartData> data = (List<ColoredOutputBarChartData>) result.get(0).getData();
        softly.assertThat(data).hasSize(3);
        softly.assertThat(data).extracting(d -> d.getName()).containsExactlyInAnyOrder("finalDiagnosis1", "finalDiagnosis2", "finalDiagnosis3");
        softly.assertThat(data).extracting(ColoredOutputBarChartData::getColor).hasSize(3);
        softly.assertThat(data.get(0).getCategories()).containsExactlyInAnyOrder("Yes", "No", "(Empty)");
    }

    @Test
    public void shouldGetBarChartCountingEventsNoColorBy() throws Exception {
        //Given
        final ChartGroupByOptions.ChartGroupBySettingsBuilder<CIEvent, CIEventGroupByOptions> settings = ChartGroupByOptions.builder();
        settings.withOption(X_AXIS, CIEventGroupByOptions.ECG_AT_THE_EVENT_TIME.getGroupByOptionAndParams());
//        final ChartGroupByOptions.GroupByOptionAndParams<CIEventGroupByOptions> trellis = ARM.getGroupByOptionAndParams();
//        settings.withTrellisOptions(new HashSet<>(Collections.singletonList(trellis)));
        final ChartGroupByOptionsFiltered.ChartGroupBySettingsFilteredBuilder<CIEvent, CIEventGroupByOptions> settingsWithFilterBy =
                ChartGroupByOptionsFiltered.builder(settings.build());
//        settingsWithFilterBy.withFilterByTrellisOption(ARM, Arrays.asList("arm1", "arm2"));


        final List<CIEvent> events = getCiEvents();
        when(ciEventDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events.stream().map(e -> e.getSubject()).collect(Collectors.toList()));

        //When
        List<TrellisedBarChart<CIEvent, CIEventGroupByOptions>> result = ciEventService.getBarChart(DATASETS, settingsWithFilterBy.build(),
                new CIEventFilters(), new PopulationFilters(), CountType.COUNT_OF_EVENTS);

        //Then
        softly.assertThat(result).hasSize(1);
        List<ColoredOutputBarChartData> data = (List<ColoredOutputBarChartData>) result.get(0).getData();
        softly.assertThat(data).hasSize(1);
        softly.assertThat(data).extracting(ColoredOutputBarChartData::getColor).hasSize(1);
        softly.assertThat(data).flatExtracting(d -> d.getSeries()).hasSize(3);
        softly.assertThat(data).extracting(d -> d.getName()).containsOnly("All");
        softly.assertThat(data.get(0).getCategories()).containsExactlyInAnyOrder("Yes", "No", "(Empty)");
    }

    @Test
    public void shouldGetBarChartCountingEventsNoColorByAndNoXAxis() throws Exception {
        //Given
        final ChartGroupByOptions.ChartGroupBySettingsBuilder<CIEvent, CIEventGroupByOptions> settings = ChartGroupByOptions.builder();
        final ChartGroupByOptionsFiltered.ChartGroupBySettingsFilteredBuilder<CIEvent, CIEventGroupByOptions> settingsWithFilterBy =
                ChartGroupByOptionsFiltered.builder(settings.build());


        final List<CIEvent> events = getCiEvents();
        when(ciEventDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events.stream().map(e -> e.getSubject()).collect(Collectors.toList()));

        //When
        List<TrellisedBarChart<CIEvent, CIEventGroupByOptions>> result = ciEventService.getBarChart(DATASETS, settingsWithFilterBy.build(),
                new CIEventFilters(), new PopulationFilters(), CountType.COUNT_OF_EVENTS);

        //Then
        softly.assertThat(result).hasSize(1);
        List<ColoredOutputBarChartData> data = (List<ColoredOutputBarChartData>) result.get(0).getData();
        softly.assertThat(data).hasSize(1);
        softly.assertThat(data).extracting(ColoredOutputBarChartData::getColor).hasSize(1);
        softly.assertThat(data).flatExtracting(d -> d.getSeries()).hasSize(1);
        softly.assertThat(data).extracting(d -> d.getName()).containsOnly("All");
        softly.assertThat(data.get(0).getCategories()).containsOnly("All");
        softly.assertThat(data.get(0).getSeries()).extracting(s -> s.getValue()).containsExactly(3.0);
    }

    @Test
    public void shouldGetLineBarChartData() {

        // Given
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(getSubjects());
        when(ciEventDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(getCiEvents());

        final ChartGroupByOptions.ChartGroupBySettingsBuilder<CIEvent, CIEventGroupByOptions> settings = ChartGroupByOptions.builder();
        //settings.withOption(COLOR_BY, CIEventGroupByOptions.NONE));
        settings.withOption(X_AXIS, CIEventGroupByOptions.START_DATE.getGroupByOptionAndParams(
                GroupByOption.Params.builder()
                        .with(GroupByOption.Param.BIN_SIZE, 1)
                        .with(Param.TIMESTAMP_TYPE, TimestampType.DAYS_SINCE_FIRST_DOSE)
                        .build()));
//        final ChartGroupByOptions.GroupByOptionAndParams<CIEventGroupByOptions> trellis = ARM.getGroupByOptionAndParams();
//        settings.withTrellisOptions(new HashSet<>(Collections.singletonList(trellis)));
        final ChartGroupByOptionsFiltered.ChartGroupBySettingsFilteredBuilder<CIEvent, CIEventGroupByOptions> settingsWithFilterBy =
                ChartGroupByOptionsFiltered.builder(settings.build());
//        settingsWithFilterBy.withFilterByTrellisOption(ARM, Arrays.asList("arm1", "arm2"));

        // When

        List<TrellisedOvertime<CIEvent, CIEventGroupByOptions>> overtime = ciEventService.getLineBarChart(DUMMY_CVOT_DATASETS,
                settingsWithFilterBy.build(), CIEventFilters.empty(), PopulationFilters.empty());

        OutputOvertimeData chart = overtime.get(0).getData();

        List<Integer> ranks = IntStream.rangeClosed(1, chart.getCategories().size()).boxed().collect(Collectors.toList());

        // Then -- x axis categories
        softly.assertThat(chart.getCategories()).hasSize(61).containsExactly(
                IntStream.rangeClosed(31, 91).boxed().map(Object::toString).collect(Collectors.toList()).toArray(new String[0]));

        // Then -- series
        softly.assertThat(chart.getSeries()).extracting(OutputBarChartData::getName).containsExactly("All");
        softly.assertThat(chart.getSeries().get(0).getSeries()).containsExactly(
                new OutputBarChartEntry("31", 1, 1.0, 1),
                new OutputBarChartEntry("60", 30, 1.0, 1),
                new OutputBarChartEntry("91", 61, 1.0, 1));

        // Then -- line
        List<OutputBarChartEntry> line = chart.getLines().get(0).getSeries();
        softly.assertThat(line.stream().map(OutputBarChartEntry::getRank).collect(Collectors.toList())).containsExactlyElementsOf(ranks);
        softly.assertThat(line).extracting(OutputBarChartEntry::getCategory).containsExactly(
                IntStream.rangeClosed(31, 91).boxed().map(Object::toString).collect(Collectors.toList()).toArray(new String[0]));
        softly.assertThat(line).extracting(OutputBarChartEntry::getValue).containsOnly(1.);
    }

    @Test
    public void shouldGetLineBarChartWithExtraTrellisAndBins() {

        // Given
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(getSubjects());
        when(ciEventDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(getCiEvents());
        final ChartGroupByOptions.ChartGroupBySettingsBuilder<CIEvent, CIEventGroupByOptions> settings = ChartGroupByOptions.builder();
        settings.withOption(COLOR_BY, CIEventGroupByOptions.CI_SYMPTOMS_DURATION.getGroupByOptionAndParams());
        settings.withOption(X_AXIS, CIEventGroupByOptions.START_DATE.getGroupByOptionAndParams(
                GroupByOption.Params.builder()
                        .with(GroupByOption.Param.BIN_SIZE, 60)
                        .with(Param.TIMESTAMP_TYPE, TimestampType.DAYS_SINCE_RANDOMISATION)
                        .build()));
        final ChartGroupByOptionsFiltered.ChartGroupBySettingsFilteredBuilder<CIEvent, CIEventGroupByOptions> settingsWithFilterBy =
                ChartGroupByOptionsFiltered.builder(settings.build());

        // When

        List<TrellisedOvertime<CIEvent, CIEventGroupByOptions>> overtime = ciEventService.getLineBarChart(DUMMY_CVOT_DATASETS,
                settingsWithFilterBy.build(), CIEventFilters.empty(), PopulationFilters.empty());


        OutputOvertimeData chart = overtime.get(0).getData();

        // Then -- x axis categories
        softly.assertThat(chart.getCategories()).hasSize(2).containsExactly("60 - 119", "120 - 179");

        // Then -- series
        softly.assertThat(chart.getSeries()).extracting(OutputBarChartData::getName)
                .containsOnly("(Empty)", CIE_SYMPTOMS_DURATION_1, CIE_SYMPTOMS_DURATION_2);
        softly.assertThat(chart.getSeries().get(0).getCategories()).containsOnly("60 - 119", "120 - 179");
        softly.assertThat(chart.getSeries().get(1).getCategories()).containsOnly("60 - 119", "120 - 179");
        softly.assertThat(chart.getSeries().get(2).getCategories()).containsOnly("60 - 119", "120 - 179");

        softly.assertThat(chart.getSeries()).flatExtracting(OutputBarChartData::getSeries).containsExactly(
                new OutputBarChartEntry("120 - 179", 2, 1.0, 1),
                new OutputBarChartEntry("60 - 119", 1, 1.0, 1),
                new OutputBarChartEntry("60 - 119", 1, 1.0, 1)
        );

        // Then -- line
        List<OutputBarChartEntry> line = chart.getLines().get(0).getSeries();
        softly.assertThat(line).extracting(OutputBarChartEntry::getCategory).containsExactly("60 - 119", "120 - 179");
        softly.assertThat(line).extracting(OutputBarChartEntry::getValue).containsExactly(1., 1.);
    }

    @Test
    public void shouldGetBarChartSelection() throws Exception {
        // Given
        when(ciEventDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(getCiEvents());
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Arrays.asList(SUBJECT));

        final ChartGroupByOptions.ChartGroupBySettingsBuilder<CIEvent, CIEventGroupByOptions> settings = ChartGroupByOptions.builder();
        settings.withOption(X_AXIS, CIEventGroupByOptions.START_DATE.getGroupByOptionAndParams(
                Params.builder()
                        .with(Param.TIMESTAMP_TYPE, TimestampType.DAYS_SINCE_FIRST_DOSE)
                        .build()));
        settings.withOption(COLOR_BY, CIEventGroupByOptions.FINAL_DIAGNOSIS.getGroupByOptionAndParams());
        final HashMap<CIEventGroupByOptions, Object> selectedTrellises = new HashMap<>();
        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItems = new HashMap<>();
        selectedItems.put(COLOR_BY, FINAL_DIAGNOSIS_1);
        selectedItems.put(X_AXIS, 31);

        // When
        SelectionDetail selectionDetails = ciEventService.getSelectionDetails(DATASETS, CIEventFilters.empty(),
                PopulationFilters.empty(), ChartSelection.of(settings.build(), Collections.singletonList(
                        ChartSelectionItem.of(selectedTrellises, selectedItems)
                )));

        // Then
        assertThat(selectionDetails.getEventIds()).containsOnly("id1");
        assertThat(selectionDetails.getSubjectIds()).containsOnly("1");
        assertThat(selectionDetails.getTotalEvents()).isEqualTo(3);
        assertThat(selectionDetails.getTotalSubjects()).isEqualTo(1);
    }

    @Test
    public void shouldGetAssociatedAeNumbersFromEventIds() throws Exception {
        //Given
        CIEvent ciEvent1 = new CIEvent(CIEventRaw.builder()
                .subjectId("01")
                .id("e01")
                .aeNumber(1)
                .build(), Subject.builder().subjectCode("E01").datasetId("Study1").subjectId("01").build());
        CIEvent ciEvent2 = new CIEvent(CIEventRaw.builder()
                .subjectId("02")
                .id("e02")
                .aeNumber(2)
                .build(), Subject.builder().subjectCode("E02").datasetId("Study1").subjectId("02").build());
        CIEvent ciEvent3 = new CIEvent(CIEventRaw.builder()
                .subjectId("03")
                .id("e03")
                .aeNumber(3)
                .build(), Subject.builder().subjectCode("E03").datasetId("Study3").subjectId("03").build());

        List<CIEvent> events = Arrays.asList(ciEvent1, ciEvent2, ciEvent3);

        when(ciEventDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events.stream().map(e -> e.getSubject()).collect(Collectors.toList()));

        List<String> aes = ciEventService.getAssociatedAeNumbersFromEventIds(DATASETS, PopulationFilters.empty(), Arrays.asList("e03", "e02", "e01"));

        assertThat(aes).containsExactly("E01-1", "E02-2", "E03-3");

        List<String> aesMultiple = ciEventService.getAssociatedAeNumbersFromEventIds(DATASETS, PopulationFilters.empty(), Arrays.asList("e02", "e01"));

        assertThat(aesMultiple).containsExactly("E01-1", "E02-2");
    }

}
