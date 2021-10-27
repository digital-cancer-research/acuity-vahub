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
import com.acuity.visualisations.rawdatamodel.dataproviders.CerebrovascularDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.PopulationDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.filters.CerebrovascularFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.test.TestConfig;
import com.acuity.visualisations.rawdatamodel.trellis.TrellisOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.CerebrovascularGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptionsFiltered;
import com.acuity.visualisations.rawdatamodel.util.DateUtils;
import com.acuity.visualisations.rawdatamodel.vo.CerebrovascularRaw;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.OutputBarChartData;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.OutputBarChartEntry;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.OutputOvertimeData;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedOvertime;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Cerebrovascular;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_CVOT_DATASETS;
import static com.acuity.visualisations.rawdatamodel.Constants.CEREBRO_DATASETS;
import static com.acuity.visualisations.rawdatamodel.Constants.DATASETS;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.CerebrovascularGroupByOptions.EVENT_TYPE;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.CerebrovascularGroupByOptions.INTRA_HEMORRHAGE_LOC;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.CerebrovascularGroupByOptions.MRS_CURR_VISIT_OR_90D_AFTER;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.CerebrovascularGroupByOptions.MRS_DURING_STROKE_HOSP;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.CerebrovascularGroupByOptions.MRS_PRIOR_STROKE;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.CerebrovascularGroupByOptions.PRIMARY_ISCHEMIC_STROKE;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.CerebrovascularGroupByOptions.Param;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.CerebrovascularGroupByOptions.START_DATE;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.CerebrovascularGroupByOptions.SYMPTOMS_DURATION;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.CerebrovascularGroupByOptions.TRAUMATIC;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.CerebrovascularGroupByOptions.TimestampType;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.COLOR_BY;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.X_AXIS;
import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class CerebrovascularServiceTest {

    @Autowired
    private CerebrovascularService cerebrovascularService;
    @MockBean
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;
    @MockBean
    private CerebrovascularDatasetsDataProvider cerebrovascularDatasetsDataProvider;
    // @MockBean
    //private CerebrovascularFilterService cerebrovascularFilterService;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    private static final Subject SUBJECT1;
    private static final Subject SUBJECT2;

    static {
        final HashMap<String, Date> drugFirstDoseDate1 = new HashMap<>();
        drugFirstDoseDate1.put("drug1", DateUtils.toDate("01.08.2015"));
        drugFirstDoseDate1.put("drug2", DateUtils.toDate("01.10.2015"));
        SUBJECT1 = Subject.builder().subjectId("sid1").subjectCode("E01").datasetId("test")
                .firstTreatmentDate(DateUtils.toDate("01.08.2015"))
                .studyLeaveDate(DateUtils.toDate("01.08.2016"))
                .drugFirstDoseDate(drugFirstDoseDate1).build();
        SUBJECT2 = Subject.builder().subjectId("sid2").subjectCode("E02").datasetId("test")
                .firstTreatmentDate(DateUtils.toDate("02.08.2015"))
                .studyLeaveDate(DateUtils.toDate("09.08.2015"))
                .drugFirstDoseDate(drugFirstDoseDate1).build();
    }

    private static final CerebrovascularRaw RAW_EVENT1 = CerebrovascularRaw.builder().id("id1")
            .startDate(DateUtils.toDate("01.09.2015")).aeNumber(1)
            .subjectId("sid1").build();
    private static final CerebrovascularRaw RAW_EVENT2 = CerebrovascularRaw.builder().id("id2")
            .startDate(DateUtils.toDate("01.11.2015")).aeNumber(2).subjectId("sid1").build();
    private static final CerebrovascularRaw RAW_EVENT3 = CerebrovascularRaw.builder().id("id3")
            .startDate(null).aeNumber(3).subjectId("sid1").build();

    private static final Cerebrovascular EVENT1_SUBJECT1 = new Cerebrovascular(RAW_EVENT1, SUBJECT1);
    private static final Cerebrovascular EVENT2_SUBJECT1 = new Cerebrovascular(RAW_EVENT2, SUBJECT1);
    private static final Cerebrovascular EVENT3_SUBJECT1 = new Cerebrovascular(RAW_EVENT3, SUBJECT1);

    @Test
    public void shouldGetAssociatedAeNumbersFromEventIds() throws Exception {
        List<Cerebrovascular> events = getCerebrovascularEvents();
        when(cerebrovascularDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events.stream().map(e -> e.getSubject()).collect(Collectors.toList()));

        List<String> aes = cerebrovascularService.getAssociatedAeNumbersFromEventIds(DATASETS, PopulationFilters.empty(), newArrayList("e03", "e02", "e01"));

        assertThat(aes).containsExactly("E01-1", "E02-2", "E03-3");

        List<String> aesMultiple = cerebrovascularService.getAssociatedAeNumbersFromEventIds(DATASETS, PopulationFilters.empty(), newArrayList("e02", "e01"));

        assertThat(aesMultiple).containsExactly("E01-1", "E02-2");
    }

    private List<Cerebrovascular> getCerebrovascularEvents() {
        //Given
        Cerebrovascular cerebrovascular1 = new Cerebrovascular(CerebrovascularRaw.builder()
                .subjectId("01")
                .id("e01")
                .aeNumber(1)
                .eventType("eventType1")
                .primaryIschemicStroke("primaryIschemicStroke1")
                .intraHemorrhageLoc("intraHemorrhageLoc1")
                .intraHemorrhageOtherLoc("intraHemorrhageOtherLoc1")
                .build(), Subject.builder().subjectCode("E01").datasetId("Study1").subjectId("01").build());
        Cerebrovascular cerebrovascular2 = new Cerebrovascular(CerebrovascularRaw.builder()
                .subjectId("02")
                .id("e02")
                .aeNumber(2)
                .build(), Subject.builder().subjectCode("E02").datasetId("Study1").subjectId("02").build());
        Cerebrovascular cerebrovascular3 = new Cerebrovascular(CerebrovascularRaw.builder()
                .subjectId("03")
                .id("e03")
                .aeNumber(3)
                .build(), Subject.builder().subjectCode("E03").datasetId("Study3").subjectId("03").build());

        return newArrayList(cerebrovascular1, cerebrovascular2, cerebrovascular3);
    }

    @Test
    public void shouldGetAvailableBarChartXAxisOptions() {
        List<Cerebrovascular> events = getCerebrovascularEvents();
        when(cerebrovascularDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events.stream().map(e -> e.getSubject()).collect(Collectors.toList()));

        final AxisOptions<CerebrovascularGroupByOptions> availableBarChartXAxis = cerebrovascularService.getAvailableBarChartXAxis(DATASETS,
                CerebrovascularFilters.empty(), PopulationFilters.empty());

        softly.assertThat(availableBarChartXAxis.getOptions()).extracting(AxisOption::getGroupByOption)
                .containsOnly(EVENT_TYPE, PRIMARY_ISCHEMIC_STROKE, INTRA_HEMORRHAGE_LOC);
    }

    @Test
    public void shouldGetAvailableOverTimeXAxisOptionsNoRand() {
        final List<Cerebrovascular> events = Arrays.asList(EVENT1_SUBJECT1, EVENT2_SUBJECT1, EVENT3_SUBJECT1);
        when(cerebrovascularDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events.stream().map(e -> e.getSubject()).collect(Collectors.toList()));

        final AxisOptions<CerebrovascularGroupByOptions> availableOvertimeXAxis = cerebrovascularService.getAvailableOverTimeChartXAxis(DATASETS,
                CerebrovascularFilters.empty(), PopulationFilters.empty());

        softly.assertThat(availableOvertimeXAxis.getDrugs()).isNotEmpty();
        softly.assertThat(availableOvertimeXAxis.getDrugs()).containsExactlyInAnyOrder("drug2", "drug1");
        softly.assertThat(availableOvertimeXAxis.isHasRandomization()).isFalse();
        softly.assertThat(availableOvertimeXAxis.getOptions())
                .extracting(AxisOption::getGroupByOption, AxisOption::isBinableOption, AxisOption::isTimestampOption, AxisOption::isSupportsDuration)
                .containsExactly(
                        tuple(START_DATE, true, true, false)
                );
        /*
        softly.assertThat(availableOvertimeXAxis).filteredOn(o -> o.getValue() == CerebrovascularOvertimeOptions.START_DATE).extracting(AxisOption::getIntarg)
                .hasSameElementsAs(IntStream.rangeClosed(1, 61).boxed().collect(Collectors.toList()));
        softly.assertThat(availableOvertimeXAxis).filteredOn(o -> o.getValue() == CerebrovascularOvertimeOptions.DAYS_SINCE_FIRST_DOSE)
                .extracting(AxisOption::getIntarg)
                .hasSameElementsAs(IntStream.rangeClosed(1, 61).boxed().collect(Collectors.toList()));
        softly.assertThat(availableOvertimeXAxis).filteredOn(o -> o.getValue() == CerebrovascularOvertimeOptions.WEEKS_SINCE_FIRST_DOSE)
                .extracting(AxisOption::getIntarg)
                .hasSameElementsAs(IntStream.rangeClosed(1, 9).boxed().collect(Collectors.toList()));
*/
    }

    @Test
    public void shouldGetTrellisOptions() {
        //Given
        Cerebrovascular cerebrovascular1 = new Cerebrovascular(CerebrovascularRaw.builder().id("id1").traumatic("Yes")
                .eventType("type").build(), Subject.builder().subjectCode("E01").datasetId("Study1").subjectId("01").build());
        Cerebrovascular cerebrovascular2 = new Cerebrovascular(CerebrovascularRaw.builder().id("id2").mrsCurrVisitOr90dAfter("No")
                .primaryIschemicStroke("primaryStroke").build(), Subject.builder().subjectCode("E02").datasetId("Study1").subjectId("02").build());
        Cerebrovascular cerebrovascular3 = new Cerebrovascular(CerebrovascularRaw.builder().id("id3").mrsDuringStrokeHosp("No")
                .symptomsDuration("duration").build(), Subject.builder().subjectCode("E03").datasetId("Study2").subjectId("03").build());
        Cerebrovascular cerebrovascular4 = new Cerebrovascular(CerebrovascularRaw.builder().id("id4").mrsPriorToStroke("stroke")
                .intraHemorrhageLoc("intra").build(),
                Subject.builder().subjectCode("E04").datasetId("Study3").subjectId("04").build());
        List<Cerebrovascular> events = Arrays.asList(cerebrovascular1, cerebrovascular2, cerebrovascular3, cerebrovascular4);

        when(cerebrovascularDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events.stream().map(e -> e.getSubject()).collect(Collectors.toList()));

        //When
        List<TrellisOptions<CerebrovascularGroupByOptions>> result
                = cerebrovascularService.getTrellisOptions(CEREBRO_DATASETS, CerebrovascularFilters.empty(), PopulationFilters.empty());

        //Then
        softly.assertThat(result.size()).isEqualTo(8);
        softly.assertThat(result.get(0)).isEqualTo(new TrellisOptions<>(
                CerebrovascularGroupByOptions.EVENT_TYPE, Arrays.asList("type", "(Empty)")));
        softly.assertThat(result.get(1)).isEqualTo(new TrellisOptions<>(
                CerebrovascularGroupByOptions.PRIMARY_ISCHEMIC_STROKE, Arrays.asList("primaryStroke", "(Empty)")));
        softly.assertThat(result.get(2)).isEqualTo(new TrellisOptions<>(
                CerebrovascularGroupByOptions.TRAUMATIC, Arrays.asList("Yes", "(Empty)")));
        softly.assertThat(result.get(3)).isEqualTo(new TrellisOptions<>(
                CerebrovascularGroupByOptions.INTRA_HEMORRHAGE_LOC, Arrays.asList("intra", "(Empty)")));
        softly.assertThat(result.get(4)).isEqualTo(new TrellisOptions<>(
                CerebrovascularGroupByOptions.SYMPTOMS_DURATION, Arrays.asList("duration", "(Empty)")));
        softly.assertThat(result.get(5)).isEqualTo(new TrellisOptions<>(
                CerebrovascularGroupByOptions.MRS_PRIOR_STROKE, Arrays.asList("stroke", "(Empty)")));
        softly.assertThat(result.get(6)).isEqualTo(new TrellisOptions<>(
                CerebrovascularGroupByOptions.MRS_DURING_STROKE_HOSP, Arrays.asList("No", "(Empty)")));
        softly.assertThat(result.get(7)).isEqualTo(new TrellisOptions<>(
                CerebrovascularGroupByOptions.MRS_CURR_VISIT_OR_90D_AFTER, Arrays.asList("No", "(Empty)")));
    }

    @Test
    public void shouldGetTrellisOptionsInCorrectOrder() {
        // Given & When
        Cerebrovascular cerebrovascular1 = new Cerebrovascular(CerebrovascularRaw.builder().id("id1").traumatic("Yes")
                .eventType("type").build(), Subject.builder().subjectCode("E01").datasetId("Study1").subjectId("01").build());
        Cerebrovascular cerebrovascular2 = new Cerebrovascular(CerebrovascularRaw.builder().id("id2").mrsCurrVisitOr90dAfter("No")
                .primaryIschemicStroke("primaryStroke").build(), Subject.builder().subjectCode("E02").datasetId("Study1").subjectId("02").build());
        Cerebrovascular cerebrovascular3 = new Cerebrovascular(CerebrovascularRaw.builder().id("id3").mrsDuringStrokeHosp("No")
                .symptomsDuration("duration").build(), Subject.builder().subjectCode("E03").datasetId("Study2").subjectId("03").build());
        Cerebrovascular cerebrovascular4 = new Cerebrovascular(CerebrovascularRaw.builder().id("id4").mrsPriorToStroke("stroke")
                .intraHemorrhageLoc("intra").build(),
                Subject.builder().subjectCode("E04").datasetId("Study3").subjectId("04").build());
        List<Cerebrovascular> events = Arrays.asList(cerebrovascular1, cerebrovascular2, cerebrovascular3, cerebrovascular4);
        when(cerebrovascularDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events.stream().map(e -> e.getSubject()).collect(Collectors.toList()));
        List<TrellisOptions<CerebrovascularGroupByOptions>> result
                = cerebrovascularService.getTrellisOptions(CEREBRO_DATASETS, CerebrovascularFilters.empty(), PopulationFilters.empty());

        // Then
        softly.assertThat(result)
            .extracting(TrellisOptions::getTrellisedBy)
            .containsExactly(
                    EVENT_TYPE,
                    PRIMARY_ISCHEMIC_STROKE,
                    TRAUMATIC,
                    INTRA_HEMORRHAGE_LOC,
                    SYMPTOMS_DURATION,
                    MRS_PRIOR_STROKE,
                    MRS_DURING_STROKE_HOSP,
                    MRS_CURR_VISIT_OR_90D_AFTER);
    }

    @Test
    public void testGetSubjects() {
        //Given
        Cerebrovascular cerebrovascular1 = new Cerebrovascular(CerebrovascularRaw.builder().id("id1").traumatic("Yes")
                .eventType("type").build(), Subject.builder().subjectCode("E01").datasetId("Study1").subjectId("01").build());
        Cerebrovascular cerebrovascular2 = new Cerebrovascular(CerebrovascularRaw.builder().id("id2").mrsCurrVisitOr90dAfter("No")
                .primaryIschemicStroke("primaryStroke").build(), Subject.builder().subjectCode("E02").datasetId("Study1").subjectId("02").build());
        Cerebrovascular cerebrovascular3 = new Cerebrovascular(CerebrovascularRaw.builder().id("id3").mrsDuringStrokeHosp("No")
                .symptomsDuration("duration").build(), Subject.builder().subjectCode("E03").datasetId("Study2").subjectId("03").build());
        Cerebrovascular cerebrovascular4 = new Cerebrovascular(CerebrovascularRaw.builder().id("id4").mrsPriorToStroke("stroke")
                .intraHemorrhageLoc("intra").build(),
                Subject.builder().subjectCode("E04").datasetId("Study3").subjectId("04").build());
        List<Cerebrovascular> events = Arrays.asList(cerebrovascular1, cerebrovascular2, cerebrovascular3, cerebrovascular4);

        when(cerebrovascularDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events.stream().map(e -> e.getSubject()).collect(Collectors.toList()));

        //When
        List<String> result = cerebrovascularService.getSubjects(CEREBRO_DATASETS, CerebrovascularFilters.empty(), PopulationFilters.empty());

        //Then
        softly.assertThat(result.size()).isEqualTo(4);
    }

    @Test
    public void shouldGetLineBarChartBinnedByDaysSinceFirstDose() {

        Cerebrovascular cerebrovascular1 = new Cerebrovascular(
                CerebrovascularRaw.builder().id("id1").startDate(DateUtils.toDate("05.08.2015")).eventType("Primary Ischemic Stroke").build(),
                SUBJECT1);
        Cerebrovascular cerebrovascular2 = new Cerebrovascular(
                CerebrovascularRaw.builder().id("id2").startDate(DateUtils.toDate("10.08.2015")).eventType("Primary Intracranial Hemorrhage").build(),
                SUBJECT1);
        Cerebrovascular cerebrovascular3 = new Cerebrovascular(
                CerebrovascularRaw.builder().id("id3").startDate(DateUtils.toDate("10.08.2015")).eventType("Primary Ischemic Stroke").build(),
                SUBJECT1);
        Cerebrovascular cerebrovascular4 = new Cerebrovascular(
                CerebrovascularRaw.builder().id("id4").startDate(DateUtils.toDate("10.08.2015")).eventType(null).build(),
                SUBJECT1);
        Cerebrovascular cerebrovascular5 = new Cerebrovascular(
                CerebrovascularRaw.builder().id("id5").startDate(DateUtils.toDate("10.08.2015")).eventType(null).build(),
                SUBJECT1);
        List<Cerebrovascular> cerebrovasculars = Arrays.asList(cerebrovascular1, cerebrovascular2, cerebrovascular3, cerebrovascular4, cerebrovascular5);

        when(cerebrovascularDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(cerebrovasculars);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Arrays.asList(SUBJECT1, SUBJECT2));

        final ChartGroupByOptions.ChartGroupBySettingsBuilder<Cerebrovascular, CerebrovascularGroupByOptions> settings = ChartGroupByOptions.builder();
        settings.withOption(COLOR_BY, CerebrovascularGroupByOptions.EVENT_TYPE.getGroupByOptionAndParams());
        settings.withOption(X_AXIS, CerebrovascularGroupByOptions.START_DATE.getGroupByOptionAndParams(
                GroupByOption.Params.builder()
                        .with(GroupByOption.Param.BIN_SIZE, 1)
                        .with(Param.TIMESTAMP_TYPE, TimestampType.DAYS_SINCE_FIRST_DOSE)
                        .build()));
//        final ChartGroupByOptions.GroupByOptionAndParams<CIEventGroupByOptions> trellis = ARM.getGroupByOptionAndParams();
//        settings.withTrellisOptions(new HashSet<>(Collections.singletonList(trellis)));
        final ChartGroupByOptionsFiltered.ChartGroupBySettingsFilteredBuilder<Cerebrovascular, CerebrovascularGroupByOptions> settingsWithFilterBy =
                ChartGroupByOptionsFiltered.builder(settings.build());
//        settingsWithFilterBy.withFilterByTrellisOption(ARM, Arrays.asList("arm1", "arm2"));


        List<TrellisedOvertime<Cerebrovascular, CerebrovascularGroupByOptions>> overtime = cerebrovascularService.getLineBarChart(DUMMY_CVOT_DATASETS,
                settingsWithFilterBy.build(), CerebrovascularFilters.empty(), PopulationFilters.empty());

        OutputOvertimeData chart = overtime.get(0).getData();

        List<Integer> ranks = IntStream.rangeClosed(1, chart.getCategories().size()).boxed().collect(Collectors.toList());

        softly.assertThat(chart.getCategories()).hasSize(6);
        softly.assertThat(chart.getCategories()).containsExactly("4", "5", "6", "7", "8", "9");

        softly.assertThat(chart.getSeries()).hasSize(3);
        softly.assertThat(chart.getSeries()).extracting(OutputBarChartData::getName).
                containsExactly("Primary Intracranial Hemorrhage", "Primary Ischemic Stroke", "(Empty)");

        softly.assertThat(chart.getSeries().get(2).getSeries()).containsExactly(new OutputBarChartEntry("9", 6, 2.0, 1));
        softly.assertThat(chart.getSeries().get(1).getSeries()).containsExactly(
                new OutputBarChartEntry("4", 1, 1.0, 1), new OutputBarChartEntry("9", 6, 1.0, 1));
        softly.assertThat(chart.getSeries().get(0).getSeries()).containsExactly(new OutputBarChartEntry("9", 6, 1.0, 1));

        List<OutputBarChartEntry> line = chart.getLines().get(0).getSeries();
        softly.assertThat(line).hasSize(6);
        softly.assertThat(line.stream().map(OutputBarChartEntry::getRank).collect(Collectors.toList()))
                .containsExactlyElementsOf(ranks);
        softly.assertThat(line.get(0).getCategory()).isEqualTo("4");
        softly.assertThat(line.get(0).getValue()).isEqualTo(2.0);
        softly.assertThat(line.get(1).getCategory()).isEqualTo("5");
        softly.assertThat(line.get(1).getValue()).isEqualTo(2.0);
        softly.assertThat(line.get(2).getCategory()).isEqualTo("6");
        softly.assertThat(line.get(2).getValue()).isEqualTo(2.0);
        softly.assertThat(line.get(3).getCategory()).isEqualTo("7");
        softly.assertThat(line.get(3).getValue()).isEqualTo(2.0);
        softly.assertThat(line.get(4).getCategory()).isEqualTo("8");
        softly.assertThat(line.get(4).getValue()).isEqualTo(1.0);
        softly.assertThat(line.get(5).getCategory()).isEqualTo("9");
        softly.assertThat(line.get(5).getValue()).isEqualTo(1.0);
    }

    @Test
    public void shouldGetLineBarChartBinnedByDaysSinceFirstDoseWithMoreEvents() {

        Cerebrovascular cerebrovascular1 = new Cerebrovascular(
                CerebrovascularRaw.builder().id("id1").startDate(DateUtils.toDate("5.08.2015")).eventType("Primary Ischemic Stroke").build(),
                SUBJECT1);
        Cerebrovascular cerebrovascular2 = new Cerebrovascular(
                CerebrovascularRaw.builder().id("id2").startDate(DateUtils.toDate("6.08.2015")).eventType("Primary Ischemic Stroke").build(),
                SUBJECT2);
        Cerebrovascular cerebrovascular3 = new Cerebrovascular(
                CerebrovascularRaw.builder().id("id3").startDate(DateUtils.toDate("5.08.2015")).eventType("Primary Intracranial Hemorrhage").build(),
                SUBJECT1);
        Cerebrovascular cerebrovascular4 = new Cerebrovascular(
                CerebrovascularRaw.builder().id("id4").startDate(DateUtils.toDate("10.08.2015")).eventType("Primary Ischemic Stroke").build(),
                SUBJECT1);
        Cerebrovascular cerebrovascular5 = new Cerebrovascular(
                CerebrovascularRaw.builder().id("id5").startDate(DateUtils.toDate("11.08.2015")).eventType(null).build(),
                SUBJECT1);
        Cerebrovascular cerebrovascular6 = new Cerebrovascular(
                CerebrovascularRaw.builder().id("id6").startDate(DateUtils.toDate("10.08.2015")).eventType("Primary Ischemic Stroke").build(),
                SUBJECT2);
        Cerebrovascular cerebrovascular7 = new Cerebrovascular(
                CerebrovascularRaw.builder().id("id7").startDate(DateUtils.toDate("11.08.2015")).eventType("Primary Intracranial Hemorrhage").build(),
                SUBJECT2);
        List<Cerebrovascular> cerebrovasculars = newArrayList(
                cerebrovascular1, cerebrovascular2, cerebrovascular3, cerebrovascular4, cerebrovascular5, cerebrovascular6, cerebrovascular7
        );

        when(cerebrovascularDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(cerebrovasculars);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Arrays.asList(SUBJECT1, SUBJECT2));

        final ChartGroupByOptions.ChartGroupBySettingsBuilder<Cerebrovascular, CerebrovascularGroupByOptions> settings = ChartGroupByOptions.builder();
        settings.withOption(COLOR_BY, CerebrovascularGroupByOptions.EVENT_TYPE.getGroupByOptionAndParams());
        settings.withOption(X_AXIS, CerebrovascularGroupByOptions.START_DATE.getGroupByOptionAndParams(
                GroupByOption.Params.builder()
                        .with(GroupByOption.Param.BIN_SIZE, 1)
                        .with(Param.TIMESTAMP_TYPE, TimestampType.DAYS_SINCE_FIRST_DOSE)
                        .build()));
//        final ChartGroupByOptions.GroupByOptionAndParams<CIEventGroupByOptions> trellis = ARM.getGroupByOptionAndParams();
//        settings.withTrellisOptions(new HashSet<>(Collections.singletonList(trellis)));
        final ChartGroupByOptionsFiltered.ChartGroupBySettingsFilteredBuilder<Cerebrovascular, CerebrovascularGroupByOptions> settingsWithFilterBy =
                ChartGroupByOptionsFiltered.builder(settings.build());
//        settingsWithFilterBy.withFilterByTrellisOption(ARM, Arrays.asList("arm1", "arm2"));


        List<TrellisedOvertime<Cerebrovascular, CerebrovascularGroupByOptions>> overtime = cerebrovascularService.getLineBarChart(DUMMY_CVOT_DATASETS,
                settingsWithFilterBy.build(), CerebrovascularFilters.empty(), PopulationFilters.empty());

        System.out.println(overtime);

        OutputOvertimeData chart = overtime.get(0).getData();

        List<Integer> ranks = IntStream.rangeClosed(1, chart.getCategories().size()).boxed().collect(Collectors.toList());

        softly.assertThat(chart.getCategories()).hasSize(7);
        softly.assertThat(chart.getCategories()).containsExactly("4", "5", "6", "7", "8", "9", "10");

        softly.assertThat(chart.getSeries()).hasSize(3);
        softly.assertThat(chart.getSeries()).extracting(OutputBarChartData::getName).
                containsExactly("Primary Intracranial Hemorrhage", "Primary Ischemic Stroke", "(Empty)");

        softly.assertThat(chart.getSeries().get(2).getCategories()).containsExactly("4", "5", "6", "7", "8", "9", "10");
        softly.assertThat(chart.getSeries().get(2).getSeries()).containsExactly(new OutputBarChartEntry("10", 7, 1.0, 1));
        softly.assertThat(chart.getSeries().get(1).getCategories()).containsExactly("4", "5", "6", "7", "8", "9", "10");
        softly.assertThat(chart.getSeries().get(1).getSeries()).containsExactly(
                new OutputBarChartEntry("4", 1, 2.0, 2),
                new OutputBarChartEntry("8", 5, 1.0, 1),
                new OutputBarChartEntry("9", 6, 1.0, 1)
        );
        softly.assertThat(chart.getSeries().get(0).getCategories()).containsExactly("4", "5", "6", "7", "8", "9", "10");
        softly.assertThat(chart.getSeries().get(0).getSeries()).containsExactly(
                new OutputBarChartEntry("4", 1, 1.0, 1),
                new OutputBarChartEntry("9", 6, 1.0, 1));

        List<OutputBarChartEntry> line = chart.getLines().get(0).getSeries();
        softly.assertThat(line).hasSize(7);
        softly.assertThat(line.stream().map(OutputBarChartEntry::getRank).collect(Collectors.toList()))
                .containsExactlyElementsOf(ranks);
        softly.assertThat(line.get(0).getCategory()).isEqualTo("4");
        softly.assertThat(line.get(0).getValue()).isEqualTo(2.0);
        softly.assertThat(line.get(1).getCategory()).isEqualTo("5");
        softly.assertThat(line.get(1).getValue()).isEqualTo(2.0);
        softly.assertThat(line.get(2).getCategory()).isEqualTo("6");
        softly.assertThat(line.get(2).getValue()).isEqualTo(2.0);
        softly.assertThat(line.get(3).getCategory()).isEqualTo("7");
        softly.assertThat(line.get(3).getValue()).isEqualTo(2.0);
        softly.assertThat(line.get(4).getCategory()).isEqualTo("8");
        softly.assertThat(line.get(4).getValue()).isEqualTo(1.0);
        softly.assertThat(line.get(5).getCategory()).isEqualTo("9");
        softly.assertThat(line.get(5).getValue()).isEqualTo(1.0);
        softly.assertThat(line.get(6).getCategory()).isEqualTo("10");
        softly.assertThat(line.get(6).getValue()).isEqualTo(1.0);
    }
}
