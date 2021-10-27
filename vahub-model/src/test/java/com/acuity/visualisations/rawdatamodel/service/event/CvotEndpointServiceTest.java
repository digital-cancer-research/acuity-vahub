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
import com.acuity.visualisations.rawdatamodel.dataproviders.CvotEndpointDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.PopulationDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.filters.CvotEndpointFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.test.TestConfig;
import com.acuity.visualisations.rawdatamodel.trellis.TrellisOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptionsFiltered;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItem;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.CvotEndpointGroupByOptions;
import com.acuity.visualisations.rawdatamodel.util.DateUtils;
import com.acuity.visualisations.rawdatamodel.vo.CvotEndpointRaw;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.ColoredOutputBarChartData;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.OutputBarChartEntry;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.OutputOvertimeLineChartData;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedOvertime;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.CvotEndpoint;
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

import static com.acuity.visualisations.rawdatamodel.Constants.DATASETS;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.COLOR_BY;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.X_AXIS;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.CvotEndpointGroupByOptions.CATEGORY_1;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.CvotEndpointGroupByOptions.CATEGORY_2;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.CvotEndpointGroupByOptions.CATEGORY_3;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.CvotEndpointGroupByOptions.DESCRIPTION_1;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.CvotEndpointGroupByOptions.DESCRIPTION_2;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.CvotEndpointGroupByOptions.DESCRIPTION_3;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.CvotEndpointGroupByOptions.START_DATE;
import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class CvotEndpointServiceTest {

    private static final Subject SUBJECT1;
    private static final Subject SUBJECT2;
    private static final Subject SUBJECT3;
    private static final Subject SUBJECT1_WITH_RAND;
    private static final CvotEndpointRaw RAW_EVENT1 = CvotEndpointRaw.builder().id("id1")
            .startDate(DateUtils.toDate("01.09.2015")).aeNumber(1)
            .category1("cat1").category2("cat2").term("term1")
            .description1("d1").description2("d2").description3("d3").subjectId("sid1").build();
    private static final CvotEndpointRaw RAW_EVENT2 = CvotEndpointRaw.builder().id("id2")
            .startDate(DateUtils.toDate("01.11.2015")).aeNumber(2).category1("cat2").term("term2").subjectId("sid1").build();
    private static final CvotEndpointRaw RAW_EVENT3 = CvotEndpointRaw.builder().id("id3")
            .startDate(null).aeNumber(3).category1("cat1").subjectId("sid1").build();
    private static final CvotEndpointRaw RAW_EVENT4 = CvotEndpointRaw.builder().id("id4")
            .startDate(DateUtils.toDate("12.01.2015")).aeNumber(2).category1("cat1").subjectId("sid2").build();
    private static final CvotEndpointRaw RAW_EVENT5 = CvotEndpointRaw.builder().id("id5")
            .startDate(DateUtils.toDate("15.01.2015")).aeNumber(2).category1("cat1").term("term1").subjectId("sid2").build();
    private static final CvotEndpointRaw RAW_EVENT6 = CvotEndpointRaw.builder().id("id6")
            .startDate(DateUtils.toDate("18.01.2015")).aeNumber(2).category1("cat2").term("term2").subjectId("sid2").build();
    private static final CvotEndpointRaw RAW_EVENT7 = CvotEndpointRaw.builder().id("id7")
            .startDate(DateUtils.toDate("18.01.2015")).aeNumber(2).category1("cat2").term("term2").subjectId("sid3").build();
    private static final CvotEndpointRaw RAW_EVENT8 = CvotEndpointRaw.builder().id("id8")
            .startDate(DateUtils.toDate("20.01.2015")).aeNumber(2).category1("cat3").term("term3").subjectId("sid3").build();
    private static final CvotEndpointRaw RAW_EVENT9 = CvotEndpointRaw.builder().id("id9")
            .startDate(DateUtils.toDate("01.07.2015")).aeNumber(2).category1("cat3").term("term1").subjectId("sid1").build();

    static {
        final HashMap<String, Date> drugFirstDoseDate1 = new HashMap<>();
        drugFirstDoseDate1.put("drug1", DateUtils.toDate("01.08.2015"));
        drugFirstDoseDate1.put("drug2", DateUtils.toDate("01.10.2015"));
        final HashMap<String, Date> drugFirstDoseDate3 = new HashMap<>();
        drugFirstDoseDate3.put("drug1", DateUtils.toDate("01.03.2015"));
        drugFirstDoseDate3.put("drug2", null);

        SUBJECT1 = Subject.builder().subjectId("sid1").subjectCode("E01").datasetId("test")
                .firstTreatmentDate(DateUtils.toDate("01.08.2015"))
                .studyLeaveDate(DateUtils.toDate("10.10.2015"))
                .drugFirstDoseDate(drugFirstDoseDate1).build();
        SUBJECT2 = Subject.builder().subjectId("sid2").subjectCode("E02").datasetId("test")
                .firstTreatmentDate(DateUtils.toDate("01.01.2015"))
                .studyLeaveDate(DateUtils.toDate("01.11.2015"))
                .drugFirstDoseDate(drugFirstDoseDate1).build();
        SUBJECT3 = Subject.builder().subjectId("sid3").subjectCode("E03").datasetId("test")
                .firstTreatmentDate(DateUtils.toDate("01.03.2015"))
                .studyLeaveDate(DateUtils.toDate("01.05.2015"))
                .drugFirstDoseDate(drugFirstDoseDate3).build();
        SUBJECT1_WITH_RAND = Subject.builder().subjectId("sid1").subjectCode("E01").datasetId("test")
                .firstTreatmentDate(DateUtils.toDate("01.08.2015"))
                .studyLeaveDate(DateUtils.toDate("15.05.2016"))
                .dateOfRandomisation(DateUtils.toDate("15.05.2014"))
                .drugFirstDoseDate(drugFirstDoseDate1).build();
    }

    private static final CvotEndpoint EVENT1_SUBJECT1 = new CvotEndpoint(RAW_EVENT1, SUBJECT1);
    private static final CvotEndpoint EVENT2_SUBJECT1 = new CvotEndpoint(RAW_EVENT2, SUBJECT1);
    private static final CvotEndpoint EVENT3_SUBJECT1 = new CvotEndpoint(RAW_EVENT3, SUBJECT1);
    private static final CvotEndpoint EVENT9_SUBJECT1_NEG = new CvotEndpoint(RAW_EVENT9, SUBJECT1);
    private static final CvotEndpoint EVENT4_SUBJECT2 = new CvotEndpoint(RAW_EVENT4, SUBJECT2);
    private static final CvotEndpoint EVENT5_SUBJECT2 = new CvotEndpoint(RAW_EVENT5, SUBJECT2);
    private static final CvotEndpoint EVENT6_SUBJECT2 = new CvotEndpoint(RAW_EVENT6, SUBJECT2);
    private static final CvotEndpoint EVENT7_SUBJECT3 = new CvotEndpoint(RAW_EVENT7, SUBJECT3);
    private static final CvotEndpoint EVENT8_SUBJECT3 = new CvotEndpoint(RAW_EVENT8, SUBJECT3);
    private static final CvotEndpoint EVENT1_SUBJECT1R = new CvotEndpoint(RAW_EVENT1, SUBJECT1_WITH_RAND);
    private static final CvotEndpoint EVENT2_SUBJECT1R = new CvotEndpoint(RAW_EVENT2, SUBJECT1_WITH_RAND);
    private static final CvotEndpoint EVENT3_SUBJECT1R = new CvotEndpoint(RAW_EVENT3, SUBJECT1_WITH_RAND);

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();
    @Autowired
    private CvotEndpointService cvotEndpointService;
    @MockBean
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;
    @MockBean
    private CvotEndpointDatasetsDataProvider cvotEndpointDatasetsDataProvider;

    @Test
    public void shouldgetAssociatedAeNumbersFromEventIds() throws Exception {
        //Given
        final List<CvotEndpoint> events = Arrays.asList(EVENT1_SUBJECT1, EVENT2_SUBJECT1, EVENT3_SUBJECT1);
        when(cvotEndpointDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events.stream().map(e -> e.getSubject()).collect(Collectors.toList()));

        List<String> aes = cvotEndpointService.getAssociatedAeNumbersFromEventIds(DATASETS, PopulationFilters.empty(), newArrayList("id1", "id2", "id3"));
        softly.assertThat(aes).containsExactly("E01-1", "E01-2", "E01-3");

        List<String> aesMultiple = cvotEndpointService.getAssociatedAeNumbersFromEventIds(DATASETS, PopulationFilters.empty(), newArrayList("id1", "id2"));
        softly.assertThat(aesMultiple).containsExactly("E01-1", "E01-2");
    }

    @Test
    public void shouldGetAvailableOverTimeXAxisOptionsNoRand() {
        final List<CvotEndpoint> events = Arrays.asList(EVENT1_SUBJECT1, EVENT2_SUBJECT1, EVENT3_SUBJECT1);
        when(cvotEndpointDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events.stream().map(e -> e.getSubject()).collect(Collectors.toList()));

        final AxisOptions<CvotEndpointGroupByOptions> availableOvertimeXAxis = cvotEndpointService.getAvailableOverTimeChartXAxis(DATASETS,
                CvotEndpointFilters.empty(), PopulationFilters.empty());

        softly.assertThat(availableOvertimeXAxis.getDrugs()).isNotEmpty();
        softly.assertThat(availableOvertimeXAxis.getDrugs()).containsExactlyInAnyOrder("drug1", "drug2");
        softly.assertThat(availableOvertimeXAxis.isHasRandomization()).isFalse();
        softly.assertThat(availableOvertimeXAxis.getOptions())
                .extracting(AxisOption::getGroupByOption, AxisOption::isBinableOption, AxisOption::isTimestampOption, AxisOption::isSupportsDuration)
                .containsExactly(
                        tuple(START_DATE, true, true, false)
                );
    }

    @Test
    public void shouldGetAvailableOverTimeXAxisOptionsNoRand1Drug() {
        final List<CvotEndpoint> events = Arrays.asList(EVENT7_SUBJECT3, EVENT8_SUBJECT3);
        when(cvotEndpointDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events.stream().map(e -> e.getSubject()).collect(Collectors.toList()));

        final AxisOptions<CvotEndpointGroupByOptions> availableOvertimeXAxis = cvotEndpointService.getAvailableOverTimeChartXAxis(DATASETS,
                CvotEndpointFilters.empty(), PopulationFilters.empty());

        softly.assertThat(availableOvertimeXAxis.getDrugs()).isNotEmpty();
        softly.assertThat(availableOvertimeXAxis.getDrugs()).containsExactlyInAnyOrder("drug1");
        softly.assertThat(availableOvertimeXAxis.isHasRandomization()).isFalse();
        softly.assertThat(availableOvertimeXAxis.getOptions())
                .extracting(AxisOption::getGroupByOption, AxisOption::isBinableOption, AxisOption::isTimestampOption, AxisOption::isSupportsDuration)
                .containsExactly(
                        tuple(START_DATE, true, true, false)
                );
      /*  softly.assertThat(availableOvertimeXAxis).filteredOn(o -> o.getValue() == START_DATE).extracting(AxisOption::getIntarg)
                .hasSameElementsAs(IntStream.rangeClosed(1, 2).boxed().collect(toList()));
        softly.assertThat(availableOvertimeXAxis).filteredOn(o -> o.getValue() == DAYS_SINCE_FIRST_DOSE).extracting(AxisOption::getIntarg)
                .hasSameElementsAs(IntStream.rangeClosed(1, 2).boxed().collect(toList()));
        softly.assertThat(availableOvertimeXAxis).filteredOn(o -> o.getValue() == WEEKS_SINCE_FIRST_DOSE).extracting(AxisOption::getIntarg)
                .containsExactly((Integer) null);
        softly.assertThat(availableOvertimeXAxis).filteredOn(o -> o.getValue() == DAYS_SINCE_FIRST_DOSE_OF_DRUG).extracting(AxisOption::getIntarg)
                .hasSameElementsAs(IntStream.rangeClosed(1, 2).boxed().collect(toList()));
        softly.assertThat(availableOvertimeXAxis).filteredOn(o -> o.getValue() == DAYS_SINCE_FIRST_DOSE_OF_DRUG)
                .extracting(AxisOption::getStringarg).containsOnly("drug1");
        softly.assertThat(availableOvertimeXAxis).filteredOn(o -> o.getValue() == WEEKS_SINCE_FIRST_DOSE_OF_DRUG).extracting(AxisOption::getIntarg)
                .containsExactly((Integer) null);
        softly.assertThat(availableOvertimeXAxis).filteredOn(o -> o.getValue() == WEEKS_SINCE_FIRST_DOSE_OF_DRUG)
                .extracting(AxisOption::getStringarg).containsOnly("drug1");*/

    }

    @Test
    public void shouldGetAvailableOverTimeXAxisOptionsWithRand() {
        final List<CvotEndpoint> events = Arrays.asList(EVENT1_SUBJECT1R, EVENT2_SUBJECT1R, EVENT3_SUBJECT1R);
        when(cvotEndpointDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events.stream().map(e -> e.getSubject()).collect(Collectors.toList()));

        final AxisOptions<CvotEndpointGroupByOptions> availableOvertimeXAxis = cvotEndpointService.getAvailableOverTimeChartXAxis(DATASETS,
                CvotEndpointFilters.empty(), PopulationFilters.empty());

        softly.assertThat(availableOvertimeXAxis.getDrugs()).isNotEmpty();
        softly.assertThat(availableOvertimeXAxis.isHasRandomization()).isTrue();
        softly.assertThat(availableOvertimeXAxis.getOptions())
                .extracting(AxisOption::getGroupByOption, AxisOption::isBinableOption, AxisOption::isTimestampOption)
                .containsExactly(
                        tuple(START_DATE, true, true)
                );
   /*     softly.assertThat(availableOvertimeXAxis).filteredOn(o -> o.getValue() == DAYS_SINCE_RANDOMISATION).extracting(AxisOption::getIntarg)
                .hasSameElementsAs(IntStream.rangeClosed(1, 61).boxed().collect(toList()));
        softly.assertThat(availableOvertimeXAxis).filteredOn(o -> o.getValue() == WEEKS_SINCE_RANDOMISATION).extracting(AxisOption::getIntarg)
                .hasSameElementsAs(IntStream.rangeClosed(1, 9).boxed().collect(toList()));*/

    }

    @Test
    public void shouldGetAvailableBarChartXAxisOptions() {
        final List<CvotEndpoint> events = Arrays.asList(EVENT1_SUBJECT1, EVENT2_SUBJECT1, EVENT3_SUBJECT1);
        when(cvotEndpointDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events.stream().map(e -> e.getSubject()).collect(Collectors.toList()));

        final AxisOptions<CvotEndpointGroupByOptions> availableBarChartXAxis = cvotEndpointService.getAvailableBarChartXAxis(DATASETS,
                CvotEndpointFilters.empty(), PopulationFilters.empty());

        softly.assertThat(availableBarChartXAxis.getOptions()).extracting(AxisOption::getGroupByOption)
                .containsExactly(CATEGORY_1, CATEGORY_2, DESCRIPTION_1, DESCRIPTION_2, DESCRIPTION_3);
    }

    @Test
    public void shouldGetBarChartSelection() throws Exception {

        when(cvotEndpointDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Arrays.asList(EVENT1_SUBJECT1, EVENT2_SUBJECT1, EVENT3_SUBJECT1,
                EVENT4_SUBJECT2, EVENT5_SUBJECT2, EVENT6_SUBJECT2));
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Arrays.asList(SUBJECT1, SUBJECT2, SUBJECT3));

        final ChartGroupByOptions.ChartGroupBySettingsBuilder<CvotEndpoint, CvotEndpointGroupByOptions> settings = ChartGroupByOptions.builder();
        settings.withOption(X_AXIS, CvotEndpointGroupByOptions.CATEGORY_1.getGroupByOptionAndParams());
        settings.withOption(COLOR_BY, CvotEndpointGroupByOptions.TERM.getGroupByOptionAndParams());

        final HashMap<CvotEndpointGroupByOptions, Object> selectedTrellises = new HashMap<>();
        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItems = new HashMap<>();
        selectedItems.put(COLOR_BY, "term2");
        selectedItems.put(X_AXIS, "cat2");

        final ChartSelectionItem<CvotEndpoint, CvotEndpointGroupByOptions> selectionItem = ChartSelectionItem.of(selectedTrellises, selectedItems);


        final SelectionDetail result = cvotEndpointService.getSelectionDetails(DATASETS,
                new CvotEndpointFilters(), new PopulationFilters(), ChartSelection.of(settings.build(), Collections.singleton(selectionItem)));

        softly.assertThat(result.getEventIds()).containsExactlyInAnyOrder("id2", "id6");
        softly.assertThat(result.getSubjectIds()).containsExactlyInAnyOrder("sid1", "sid2");
        softly.assertThat(result.getTotalEvents()).isEqualTo(6);
        softly.assertThat(result.getTotalSubjects()).isEqualTo(3);

    }

    @Test
    public void shouldGetBarChartEmptySelection() throws Exception {

        when(cvotEndpointDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Arrays.asList(EVENT1_SUBJECT1, EVENT2_SUBJECT1, EVENT3_SUBJECT1,
                EVENT4_SUBJECT2, EVENT5_SUBJECT2, EVENT6_SUBJECT2));
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Arrays.asList(SUBJECT1, SUBJECT2, SUBJECT3));

        final ChartGroupByOptions.ChartGroupBySettingsBuilder<CvotEndpoint, CvotEndpointGroupByOptions> settings = ChartGroupByOptions.builder();
        settings.withOption(X_AXIS, CvotEndpointGroupByOptions.CATEGORY_1.getGroupByOptionAndParams());
        settings.withOption(COLOR_BY, CvotEndpointGroupByOptions.TERM.getGroupByOptionAndParams());

        final SelectionDetail result = cvotEndpointService.getSelectionDetails(DATASETS,
                new CvotEndpointFilters(), new PopulationFilters(), ChartSelection.of(settings.build(), Collections.emptyList()));

        softly.assertThat(result.getEventIds()).isEmpty();
        softly.assertThat(result.getSubjectIds()).isEmpty();
        softly.assertThat(result.getTotalEvents()).isEqualTo(6);
        softly.assertThat(result.getTotalSubjects()).isEqualTo(3);

    }

    @Test
    public void testGetTrellisOptions() {
        ///Given
        final List<CvotEndpoint> events = getCvotEndpoints();
        when(cvotEndpointDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events.stream().map(e -> e.getSubject()).collect(Collectors.toList()));

        //When
        List<TrellisOptions<CvotEndpointGroupByOptions>> result =
                cvotEndpointService.getTrellisOptions(DATASETS, CvotEndpointFilters.empty(), PopulationFilters.empty());

        //Then
        softly.assertThat(result.get(0)).isEqualTo(new TrellisOptions<>(CATEGORY_1, Arrays.asList("cat1", "(Empty)")));
        softly.assertThat(result.get(1)).isEqualTo(new TrellisOptions<>(CATEGORY_2, Arrays.asList("cat2", "(Empty)")));
        softly.assertThat(result.get(2)).isEqualTo(new TrellisOptions<>(CATEGORY_3, Arrays.asList("cat3", "(Empty)")));
        softly.assertThat(result.get(3)).isEqualTo(new TrellisOptions<>(DESCRIPTION_1, Arrays.asList("desc1", "(Empty)")));
        softly.assertThat(result.get(4)).isEqualTo(new TrellisOptions<>(DESCRIPTION_2, Arrays.asList("desc2", "(Empty)")));
        softly.assertThat(result.get(5)).isEqualTo(new TrellisOptions<>(DESCRIPTION_3, Arrays.asList("desc3", "(Empty)")));

    }

    @Test
    public void testGetLineBarChartByDaysSinceFirstDose() {
        when(cvotEndpointDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Arrays.asList(EVENT1_SUBJECT1, EVENT2_SUBJECT1, EVENT3_SUBJECT1,
                EVENT4_SUBJECT2, EVENT5_SUBJECT2, EVENT6_SUBJECT2));
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Arrays.asList(SUBJECT1, SUBJECT2, SUBJECT3));

        final ChartGroupByOptions.ChartGroupBySettingsBuilder<CvotEndpoint, CvotEndpointGroupByOptions> settings = ChartGroupByOptions.builder();
        settings.withOption(COLOR_BY, CvotEndpointGroupByOptions.DESCRIPTION_2.getGroupByOptionAndParams());
        settings.withOption(X_AXIS, CvotEndpointGroupByOptions.START_DATE.getGroupByOptionAndParams(
                GroupByOption.Params.builder()
                        .with(GroupByOption.Param.BIN_SIZE, 10)
                        .with(GroupByOption.Param.TIMESTAMP_TYPE, GroupByOption.TimestampType.DAYS_SINCE_FIRST_DOSE)
                        .build()));
        final ChartGroupByOptionsFiltered.ChartGroupBySettingsFilteredBuilder<CvotEndpoint, CvotEndpointGroupByOptions> settingsWithFilterBy =
                ChartGroupByOptionsFiltered.builder(settings.build());

        final List<TrellisedOvertime<CvotEndpoint, CvotEndpointGroupByOptions>> result = cvotEndpointService.getLineBarChart(DATASETS,
                settingsWithFilterBy.build(), CvotEndpointFilters.empty(),
                PopulationFilters.empty());

        softly.assertThat(result.size()).isEqualTo(1);

        String[] categories = {"10 - 19",
                "20 - 29",
                "30 - 39",
                "40 - 49",
                "50 - 59",
                "60 - 69",
                "70 - 79",
                "80 - 89",
                "90 - 99"};

        softly.assertThat(result.get(0).getData().getCategories()).containsExactly(categories);

        OutputOvertimeLineChartData line = result.get(0).getData().getLines().get(0);
        softly.assertThat(line.getName()).isEqualTo("SUBJECTS");
        softly.assertThat(line.getColor()).isEqualTo("#000000");
        softly.assertThat(line.getSeries()).hasSize(9);
        softly.assertThat(line.getSeries()).extracting(OutputBarChartEntry::getCategory).containsExactly(categories);
        softly.assertThat(line.getSeries()).extracting(OutputBarChartEntry::getRank)
                .containsExactlyElementsOf(IntStream.rangeClosed(1, 9).boxed().collect(Collectors.toList()));
        softly.assertThat(line.getSeries()).extracting(OutputBarChartEntry::getValue).containsExactly(3.0, 3.0, 3.0, 3.0, 3.0, 3.0, 2.0, 1.0, 1.0);

        List<? extends ColoredOutputBarChartData> bars = (List<? extends ColoredOutputBarChartData>) result.get(0).getData().getSeries();
        softly.assertThat(bars).extracting(ColoredOutputBarChartData::getName).containsExactly("d2", "(Empty)"); //reversed for front-end
        softly.assertThat(bars).extracting(ColoredOutputBarChartData::getColor).containsExactly("#F9DA00", "#B1C8ED");

        softly.assertThat(bars.get(1).getCategories()).containsExactly(
                "10 - 19",
                "20 - 29",
                "30 - 39",
                "40 - 49",
                "50 - 59",
                "60 - 69",
                "70 - 79",
                "80 - 89",
                "90 - 99");
        softly.assertThat(bars.get(1).getSeries()).extracting(OutputBarChartEntry::getCategory).containsExactly("10 - 19", "90 - 99");
        softly.assertThat(bars.get(1).getSeries()).extracting(OutputBarChartEntry::getRank).containsExactly(1, 9);
        softly.assertThat(bars.get(1).getSeries()).extracting(OutputBarChartEntry::getValue).containsExactly(3.0, 1.0);

        softly.assertThat(bars.get(0).getCategories()).containsExactly("10 - 19",
                "20 - 29",
                "30 - 39",
                "40 - 49",
                "50 - 59",
                "60 - 69",
                "70 - 79",
                "80 - 89",
                "90 - 99");
        softly.assertThat(bars.get(0).getSeries()).extracting(OutputBarChartEntry::getCategory).containsExactly("30 - 39");
        softly.assertThat(bars.get(0).getSeries()).extracting(OutputBarChartEntry::getRank).containsExactly(3);
        softly.assertThat(bars.get(0).getSeries()).extracting(OutputBarChartEntry::getValue).containsExactly(1.0);
    }

    @Test
    public void testGetLineBarChartByDaysSinceFirstDoseWithNegativeEvent() {
        when(cvotEndpointDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Arrays.asList(EVENT1_SUBJECT1, EVENT2_SUBJECT1, EVENT3_SUBJECT1,
                EVENT4_SUBJECT2, EVENT5_SUBJECT2, EVENT6_SUBJECT2, EVENT9_SUBJECT1_NEG));
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Arrays.asList(SUBJECT1, SUBJECT2, SUBJECT3));

        final ChartGroupByOptions.ChartGroupBySettingsBuilder<CvotEndpoint, CvotEndpointGroupByOptions> settings = ChartGroupByOptions.builder();
        settings.withOption(COLOR_BY, CvotEndpointGroupByOptions.DESCRIPTION_2.getGroupByOptionAndParams());
        settings.withOption(X_AXIS, CvotEndpointGroupByOptions.START_DATE.getGroupByOptionAndParams(
                GroupByOption.Params.builder()
                        .with(GroupByOption.Param.BIN_SIZE, 10)
                        .with(GroupByOption.Param.TIMESTAMP_TYPE, GroupByOption.TimestampType.DAYS_SINCE_FIRST_DOSE)
                        .build()));
        final ChartGroupByOptionsFiltered.ChartGroupBySettingsFilteredBuilder<CvotEndpoint, CvotEndpointGroupByOptions> settingsWithFilterBy =
                ChartGroupByOptionsFiltered.builder(settings.build());

        final List<TrellisedOvertime<CvotEndpoint, CvotEndpointGroupByOptions>> result = cvotEndpointService.getLineBarChart(DATASETS,
                settingsWithFilterBy.build(), CvotEndpointFilters.empty(),
                PopulationFilters.empty());

        softly.assertThat(result.size()).isEqualTo(1);

        String[] categories = {"-40 - -31",
                "-30 - -21",
                "-20 - -11",
                "-10 - -1",
                "0 - 9",
                "10 - 19",
                "20 - 29",
                "30 - 39",
                "40 - 49",
                "50 - 59",
                "60 - 69",
                "70 - 79",
                "80 - 89",
                "90 - 99"};

        softly.assertThat(result.get(0).getData().getCategories()).containsExactly(categories);

        OutputOvertimeLineChartData line = result.get(0).getData().getLines().get(0);
        softly.assertThat(line.getName()).isEqualTo("SUBJECTS");
        softly.assertThat(line.getColor()).isEqualTo("#000000");
        softly.assertThat(line.getSeries()).hasSize(14);
        softly.assertThat(line.getSeries()).extracting(OutputBarChartEntry::getCategory).containsExactly(categories);
        softly.assertThat(line.getSeries()).extracting(OutputBarChartEntry::getRank)
                .containsExactlyElementsOf(IntStream.rangeClosed(1, 14).boxed().collect(Collectors.toList()));
        softly.assertThat(line.getSeries()).extracting(OutputBarChartEntry::getValue)
                .containsExactly(3.0, 3.0, 3.0, 3.0, 3.0, 3.0, 3.0, 3.0, 3.0, 3.0, 3.0, 2.0, 1.0, 1.0);

        List<? extends ColoredOutputBarChartData> bars = (List<? extends ColoredOutputBarChartData>) result.get(0).getData().getSeries();
        softly.assertThat(bars).extracting(ColoredOutputBarChartData::getName).containsExactly("d2", "(Empty)"); //reversed for front-end
        softly.assertThat(bars).extracting(ColoredOutputBarChartData::getColor).containsExactly("#F9DA00", "#B1C8ED");

        softly.assertThat(bars.get(1).getCategories()).containsExactly("-40 - -31",
                "-30 - -21",
                "-20 - -11",
                "-10 - -1",
                "0 - 9",
                "10 - 19",
                "20 - 29",
                "30 - 39",
                "40 - 49",
                "50 - 59",
                "60 - 69",
                "70 - 79",
                "80 - 89",
                "90 - 99");
        softly.assertThat(bars.get(1).getSeries()).extracting(OutputBarChartEntry::getCategory).containsExactly("-40 - -31", "10 - 19", "90 - 99");
        softly.assertThat(bars.get(1).getSeries()).extracting(OutputBarChartEntry::getRank).containsExactly(1, 6, 14);
        softly.assertThat(bars.get(1).getSeries()).extracting(OutputBarChartEntry::getValue).containsExactly(1.0, 3.0, 1.0);

        softly.assertThat(bars.get(0).getCategories()).containsExactly("-40 - -31",
                "-30 - -21",
                "-20 - -11",
                "-10 - -1",
                "0 - 9",
                "10 - 19",
                "20 - 29",
                "30 - 39",
                "40 - 49",
                "50 - 59",
                "60 - 69",
                "70 - 79",
                "80 - 89",
                "90 - 99");
        softly.assertThat(bars.get(0).getSeries()).extracting(OutputBarChartEntry::getCategory).containsExactly("30 - 39");
        softly.assertThat(bars.get(0).getSeries()).extracting(OutputBarChartEntry::getRank).containsExactly(8);
        softly.assertThat(bars.get(0).getSeries()).extracting(OutputBarChartEntry::getValue).containsExactly(1.0);
    }
/*
    @Test
    public void testGetLineBarChartByDateEmptyTrellises() {
        when(cvotEndpointDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Arrays.asList(EVENT1_SUBJECT1, EVENT2_SUBJECT1, EVENT3_SUBJECT1,
                EVENT4_SUBJECT2, EVENT5_SUBJECT2, EVENT6_SUBJECT2));
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Arrays.asList(SUBJECT1, SUBJECT2, SUBJECT3));

        Set<TrellisOptions<CvotEndpointGroupByOptions>> trellis = new HashSet<>();

        List<TrellisedOvertime<CvotEndpointGroupByOptions>> result = cvotEndpointService.getLineBarChart(DATASETS, trellis, CvotEndpointFilters.empty(),
                PopulationFilters.empty(), GroupByOption.Params.builder().with(GroupByOption.Param.BIN_SIZE, 10)
                        .with(GroupByOption.Param.TIMESTAMP_TYPE, GroupByOption.TimestampType.DATE).build());

        softly.assertThat(result.size()).isEqualTo(1);

        String[] categories = {"2015-01-05 - 2015-01-14",
                "2015-01-15 - 2015-01-24",
                "2015-01-25 - 2015-02-03",
                "2015-02-04 - 2015-02-13",
                "2015-02-14 - 2015-02-23",
                "2015-02-24 - 2015-03-05",
                "2015-03-06 - 2015-03-15",
                "2015-03-16 - 2015-03-25",
                "2015-03-26 - 2015-04-04",
                "2015-04-05 - 2015-04-14",
                "2015-04-15 - 2015-04-24",
                "2015-04-25 - 2015-05-04",
                "2015-05-05 - 2015-05-14",
                "2015-05-15 - 2015-05-24",
                "2015-05-25 - 2015-06-03",
                "2015-06-04 - 2015-06-13",
                "2015-06-14 - 2015-06-23",
                "2015-06-24 - 2015-07-03",
                "2015-07-04 - 2015-07-13",
                "2015-07-14 - 2015-07-23",
                "2015-07-24 - 2015-08-02",
                "2015-08-03 - 2015-08-12",
                "2015-08-13 - 2015-08-22",
                "2015-08-23 - 2015-09-01",
                "2015-09-02 - 2015-09-11",
                "2015-09-12 - 2015-09-21",
                "2015-09-22 - 2015-10-01",
                "2015-10-02 - 2015-10-11",
                "2015-10-12 - 2015-10-21",
                "2015-10-22 - 2015-10-31",
                "2015-11-01 - 2015-11-10"};

        softly.assertThat(result.get(0).getData().getCategories()).containsExactly(categories);

        OutputOvertimeLineChartData line = result.get(0).getData().getLines().get(0);
        softly.assertThat(line.getName()).isEqualTo("SUBJECTS");
        softly.assertThat(line.getColor()).isEqualTo("#000000");
        softly.assertThat(line.getSeries()).hasSize(31);
        softly.assertThat(line.getSeries()).extracting(OutputBarChartEntry::getCategory).containsExactly(categories);
        softly.assertThat(line.getSeries()).extracting(OutputBarChartEntry::getRank).containsExactly(
                IntStream.rangeClosed(1, 31).boxed().collect(toList()).toArray(new Integer[0]));

        // number of subjects never must grow, all population must be presented at start event's bin
        softly.assertThat(line.getSeries()).extracting(OutputBarChartEntry::getValue).containsExactly(3.0,
                3.0,
                3.0,
                3.0,
                3.0,
                3.0,
                3.0,
                3.0,
                3.0,
                3.0,
                3.0,
                3.0,
                2.0,
                2.0,
                2.0,
                2.0,
                2.0,
                2.0,
                2.0,
                2.0,
                2.0,
                2.0,
                2.0,
                2.0,
                2.0,
                2.0,
                2.0,
                2.0,
                1.0,
                1.0,
                1.0);

        OutputBarChartData bars = result.get(0).getData().getSeries().get(0);
        softly.assertThat(bars.getName()).isEmpty();
        softly.assertThat(bars.getColor()).isEqualTo("#CC6677");

        softly.assertThat(bars.getCategories()).containsExactly(
                "2015-01-05 - 2015-01-14",
                "2015-01-15 - 2015-01-24",
                "2015-08-23 - 2015-09-01",
                "2015-11-01 - 2015-11-10");
        softly.assertThat(bars.getSeries()).extracting(OutputBarChartEntry::getCategory).containsExactly(
                "2015-01-05 - 2015-01-14",
                "2015-01-15 - 2015-01-24",
                "2015-08-23 - 2015-09-01",
                "2015-11-01 - 2015-11-10");
        softly.assertThat(bars.getSeries()).extracting(OutputBarChartEntry::getRank).containsExactly(1, 2, 24, 31);
        softly.assertThat(bars.getSeries()).extracting(OutputBarChartEntry::getValue).containsExactly(1.0, 2.0, 1.0, 1.0);
    }*/

    private List<CvotEndpoint> getCvotEndpoints() {
        CvotEndpoint cvot1 = new CvotEndpoint(CvotEndpointRaw.builder().category1("cat1").description1("desc1").build(),
                Subject.builder().subjectId("id1").build());
        CvotEndpoint cvot2 = new CvotEndpoint(CvotEndpointRaw.builder().category2("cat2").description2("desc2").build(),
                Subject.builder().subjectId("id2").build());
        CvotEndpoint cvot3 = new CvotEndpoint(CvotEndpointRaw.builder().category3("cat3").description3("desc3").build(),
                Subject.builder().subjectId("id3").build());
        return Arrays.asList(cvot1, cvot2, cvot3);
    }
}
