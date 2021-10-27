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
import com.acuity.visualisations.rawdatamodel.dataproviders.AeIncidenceDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.AeSeverityChangeDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.PopulationDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.filters.AeFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.service.dod.DoDCommonService;
import com.acuity.visualisations.rawdatamodel.test.TestConfig;
import com.acuity.visualisations.rawdatamodel.trellis.TrellisOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.AeGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptionsFiltered;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItem;
import com.acuity.visualisations.rawdatamodel.util.Column;
import com.acuity.visualisations.rawdatamodel.vo.AeRaw;
import com.acuity.visualisations.rawdatamodel.vo.AeSeverity;
import com.acuity.visualisations.rawdatamodel.vo.AeSeverityRaw;
import com.acuity.visualisations.rawdatamodel.vo.AesTable;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.OutputBarChartData;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.OutputBarChartEntry;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.OutputOvertimeData;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedBarChart;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedOvertime;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Ae;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.SubjectAwareWrapper;
import com.acuity.va.security.acl.domain.Datasets;
import com.google.common.collect.ImmutableMap;
import org.assertj.core.api.JUnitSoftAssertions;
import org.assertj.core.groups.Tuple;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_CVOT_DATASETS;
import static com.acuity.visualisations.config.util.TestConstants.DUMMY_DETECT_DATASETS;
import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_DATASETS;
import static com.acuity.visualisations.rawdatamodel.Constants.CEREBRO_DATASETS;
import static com.acuity.visualisations.rawdatamodel.Constants.DATASETS;
import static com.acuity.visualisations.rawdatamodel.service.ae.chord.AeChordDiagramService.MAX_TIME_FRAME_AE_CHORDS;
import static com.acuity.visualisations.rawdatamodel.service.ae.chord.AeChordDiagramService.TIME_FRAME;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.AeGroupByOptions.ARM;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.AeGroupByOptions.HLT;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.AeGroupByOptions.OVERTIME_DURATION;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.AeGroupByOptions.PT;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.AeGroupByOptions.Param;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.AeGroupByOptions.SOC;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.AeGroupByOptions.TimestampType;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.COLOR_BY;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.X_AXIS;
import static com.acuity.visualisations.rawdatamodel.util.DateUtils.toDate;
import static com.acuity.visualisations.rawdatamodel.util.DateUtils.toDateTime;
import static com.google.common.collect.Lists.newArrayList;
import static java.lang.Boolean.TRUE;
import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;


@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class AeServiceTest {

    public static final Subject SUBJECT1_ARM1;
    public static final Subject SUBJECT2_ARM2;
    private static final Subject SUBJECT3_ARM1;
    private static final Subject SUBJECT_NO_RAD;
    private static final AeSeverity SEVERITY_1 = AeSeverity.builder().severityNum(1).webappSeverity("CTC Grade 1").build();
    private static final AeSeverity SEVERITY_2 = AeSeverity.builder().severityNum(2).webappSeverity("CTC Grade 2").build();
    private static final AeSeverity SEVERITY_3 = AeSeverity.builder().severityNum(3).webappSeverity("CTC Grade 3").build();
    private static final AeSeverity SEVERITY_NULL = AeSeverity.builder().severityNum(null).webappSeverity(null).build();
    private static final HashMap<String, Date> DRUG_FIRST_DOSE_DATE = new HashMap<>();
    private static final Map<String, String> DRUG_CAUSALITY = new HashMap<String, String>() {
        {
            put("drug1", "Yes");
        }
    };
    public static final AeRaw RAW_EVENT1 = AeRaw.builder().id("id1")
            .text("text1")
            .pt("pt1")
            .soc("soc1")
            .hlt("hlt1")
            .drugsCausality(DRUG_CAUSALITY)
            .aeSeverities(
                    newArrayList(
                            AeSeverityRaw.builder().severity(SEVERITY_1).startDate(toDateTime("01.08.2015 03:00:00"))
                                    .endDate(toDateTime("03.08.2015 03:00:00")).build(),
                            AeSeverityRaw.builder().severity(SEVERITY_2).startDate(toDateTime("04.08.2015 03:00:00"))
                                    .endDate(toDateTime("05.08.2015 03:00:00")).build()
                    )
            )
            .aeNumber(1)
            .calcDurationIfNull(true)
            .subjectId("sid1").build();
    public static final AeRaw RAW_EVENT1_2 = RAW_EVENT1.toBuilder()
            .id("id12")
            .aeSeverities(
                    newArrayList(
                            AeSeverityRaw.builder().severity(SEVERITY_1).startDate(toDateTime("03.08.2015 03:00:00"))
                                    .endDate(toDateTime("07.08.2015 03:00:00")).build()
                    )
            )
            .build();
    public static final AeRaw RAW_EVENT1_3 = RAW_EVENT1.toBuilder()
            .id("id13")
            .aeSeverities(
                    newArrayList(
                            AeSeverityRaw.builder().severity(SEVERITY_1).startDate(toDateTime("10.08.2015 03:00:00"))
                                    .endDate(toDateTime("12.08.2015 03:00:00")).build()
                    )
            )
            .build();
    public static final AeRaw RAW_EVENT2 = AeRaw.builder().id("id2")
            .text("text2")
            .pt("pt2")
            .soc("soc2")
            .hlt("hlt2")
            .serious("Yes")
            .immuneMediated("I2")
            .aeSeverities(
                    newArrayList(
                            AeSeverityRaw.builder().severity(SEVERITY_1)
                                    .startDate(toDateTime("03.08.2015 03:00:00"))
                                    .endDate(toDateTime("04.08.2015 03:00:00")).build()
                    )
            )
            .aeNumber(2)
            .calcDurationIfNull(true)
            .subjectId("sid2").build();
    public static final AeRaw RAW_EVENT2_1 = RAW_EVENT2.toBuilder()
            .id("id21")
            .hlt("hlt1")
            .soc("soc1")
            .build();
    public static final AeRaw RAW_EVENT3 = AeRaw.builder().id("id3")
            .text("text3")
            .pt("pt3")
            .soc("soc3")
            .hlt("hlt3")
            .aeSeverities(
                    newArrayList(
                            AeSeverityRaw.builder().severity(SEVERITY_1).startDate(toDateTime("04.08.2015 03:00:00"))
                                    .endDate(toDateTime("05.08.2015 03:00:00")).build(),
                            AeSeverityRaw.builder().severity(SEVERITY_2).startDate(toDateTime("06.08.2015 03:00:00"))
                                    .endDate(toDateTime("07.08.2015 03:00:00")).build()
                    )
            )
            .aeNumber(3)
            .calcDurationIfNull(true)
            .subjectId("sid3").build();
    public static final AeRaw RAW_EVENT4 = AeRaw.builder().id("id4")
            .text("text4")
            .pt("pt4")
            .soc("soc4")
            .hlt("hlt4")
            .aeSeverities(
                    newArrayList(
                            AeSeverityRaw.builder().severity(SEVERITY_1).startDate(toDateTime("08.08.2015 03:00:00"))
                                    .endDate(toDateTime("09.08.2015 03:00:00")).build()
                    )
            )
            .aeNumber(3)
            .calcDurationIfNull(true)
            .subjectId("sid1").build();
    public static final AeRaw RAW_EVENT4_NULL_HLT_AND_SOC = AeRaw.builder().id("id4")
            .text("text4")
            .pt("pt2")
            .aeSeverities(
                    newArrayList(
                            AeSeverityRaw.builder().severity(SEVERITY_1).startDate(toDateTime("05.08.2015 03:00:00"))
                                    .endDate(toDateTime("07.08.2015 03:00:00")).build()
                    )
            )
            .aeNumber(4)
            .calcDurationIfNull(true)
            .subjectId("sid1").build();

    public static final AeRaw RAW_EVENT5_NULL_TERM = AeRaw.builder().id("id4")
            .text("text4")
            .aeSeverities(
                    newArrayList(
                            AeSeverityRaw.builder().severity(SEVERITY_1).startDate(toDateTime("05.08.2015 03:00:00"))
                                    .endDate(toDateTime("07.08.2015 03:00:00")).build()
                    )
            )
            .aeNumber(4)
            .calcDurationIfNull(true)
            .subjectId("sid1").build();
    public static final AeRaw RAW_EVENT6_NULL_START_DATE = AeRaw.builder().id("id4")
            .text("text4")
            .pt("pt2")
            .soc("soc4")
            .hlt("hlt4")
            .aeSeverities(
                    newArrayList(
                            AeSeverityRaw.builder().severity(SEVERITY_1)
                                    .endDate(toDateTime("04.08.2015 03:00:00")).build()
                    )
            )
            .aeNumber(4)
            .calcDurationIfNull(true)
            .subjectId("sid1").build();
    public static final ChartGroupByOptions<Ae, AeGroupByOptions> SETTINGS
            = ChartGroupByOptions.<Ae, AeGroupByOptions>builder()
            .build();

    static {
        DRUG_FIRST_DOSE_DATE.put("drug1", toDate("01.09.2015"));
        DRUG_FIRST_DOSE_DATE.put("drug2", toDate("01.10.2015"));
        SUBJECT1_ARM1 = Subject.builder().subjectId("sid1").subjectCode("E01").datasetId("test")
                .clinicalStudyCode("Study 1")
                .studyPart("A")
                .firstTreatmentDate(toDate("01.08.2015"))
                .lastTreatmentDate(toDate("09.08.2016"))
                .studyLeaveDate(toDate("01.08.2016"))
                .drugFirstDoseDate(DRUG_FIRST_DOSE_DATE)
                .dateOfRandomisation(toDate("01.12.1999"))
                .actualArm("arm1").build();
        SUBJECT2_ARM2 = Subject.builder().subjectId("sid2").subjectCode("E02").datasetId("test")
                .firstTreatmentDate(toDate("01.08.2015"))
                .lastTreatmentDate(toDate("09.08.2015"))
                .studyLeaveDate(toDate("09.08.2015"))
                .drugFirstDoseDate(DRUG_FIRST_DOSE_DATE)
                .dateOfRandomisation(toDate("01.12.1999"))
                .actualArm("arm2").build();
        SUBJECT3_ARM1 = Subject.builder().subjectId("sid3").subjectCode("E03").datasetId("test")
                .firstTreatmentDate(toDate("01.08.2015"))
                .studyLeaveDate(toDate("10.08.2015"))
                .drugFirstDoseDate(DRUG_FIRST_DOSE_DATE)
                .dateOfRandomisation(toDate("01.12.1999"))
                .actualArm("arm1").build();
        SUBJECT_NO_RAD = Subject.builder().subjectId("sid3").subjectCode("E03").datasetId("test")
                .firstTreatmentDate(toDate("02.08.2015"))
                .lastTreatmentDate(toDate("09.08.2015"))
                .drugFirstDoseDate(DRUG_FIRST_DOSE_DATE)
                .actualArm("arm2").build();
    }

    public static final HashMap<String, String> SETTINGS_WITH_MAX_TIME_FRAME= new HashMap<String, String>() { {
        put(TIME_FRAME, MAX_TIME_FRAME_AE_CHORDS.toString());
    } };

    private static final Ae EVENT1_SUBJECT1 = new Ae(RAW_EVENT1, SUBJECT1_ARM1);
    private static final Ae EVENT2_SUBJECT1 = new Ae(RAW_EVENT2, SUBJECT1_ARM1);
    private static final Ae EVENT3_SUBJECT1 = new Ae(RAW_EVENT3, SUBJECT1_ARM1);

    private static final List<Ae> AE_EVENTS = newArrayList(EVENT1_SUBJECT1, EVENT2_SUBJECT1, EVENT3_SUBJECT1);

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();
    @Autowired
    private AeService aeService;
    @MockBean
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;
    @MockBean(name = "aeIncidenceDatasetsDataProvider")
    private AeIncidenceDatasetsDataProvider aeIncidenceDatasetsDataProvider;
    @MockBean(name = "eventDataProvider")
    private AeIncidenceDatasetsDataProvider aeIncidenceDatasetsEventDataProvider;
    @MockBean(name = "aeSeverityChangeDatasetsDataProvider")
    private AeSeverityChangeDatasetsDataProvider aeSeverityChangeDatasetsDataProvider;

    private DoDCommonService doDCommonService = new DoDCommonService();

    @Test
    public void shouldGetAvailableBarChartXAxisOptions() {
        when(aeIncidenceDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(AE_EVENTS);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Arrays.asList(SUBJECT1_ARM1));

        AxisOptions<AeGroupByOptions> availableBarChartXAxis = aeService.getAvailableBarChartXAxis(DATASETS,
                AeFilters.empty(), PopulationFilters.empty());

        softly.assertThat(availableBarChartXAxis.getOptions()).isNotEmpty();
        softly.assertThat(availableBarChartXAxis.getOptions())
                .extracting(AxisOption::getGroupByOption, AxisOption::isBinableOption, AxisOption::isTimestampOption)
                .containsExactly(
                        tuple(PT, false, false),
                        tuple(HLT, false, false),
                        tuple(SOC, false, false)
                );
    }

    @Test
    public void shouldGetAvailableOverTimeXAxisOptionsNoRand() {

        Ae event1SubjectNoRad = new Ae(RAW_EVENT1, SUBJECT_NO_RAD);
        Ae event2SubjectNoRad = new Ae(RAW_EVENT2, SUBJECT_NO_RAD);
        Ae event3SubjectNoRad = new Ae(RAW_EVENT3, SUBJECT_NO_RAD);
        List<Ae> aeEvents = newArrayList(event1SubjectNoRad, event2SubjectNoRad, event3SubjectNoRad);

        when(aeIncidenceDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(aeEvents);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Arrays.asList(SUBJECT_NO_RAD));

        AxisOptions<AeGroupByOptions> availableOvertimeXAxis = aeService.getAvailableOverTimeChartXAxis(DATASETS,
                AeFilters.empty(), PopulationFilters.empty());

        softly.assertThat(availableOvertimeXAxis.getDrugs()).isNotEmpty();
        softly.assertThat(availableOvertimeXAxis.isHasRandomization()).isFalse();
        softly.assertThat(availableOvertimeXAxis.getOptions())
                .extracting(AxisOption::getGroupByOption, AxisOption::isBinableOption, AxisOption::isTimestampOption, AxisOption::isSupportsDuration)
                .containsExactly(
                        tuple(OVERTIME_DURATION, true, true, true)
                );
    }

    @Test
    public void shouldGetTrellisOptions() {
        //Given
        final Subject s1 = Subject.builder().subjectCode("E01").datasetId("Study1").subjectId("01").actualArm("arm1").build();
        Ae ae1 = new Ae(AeRaw.builder().id("id1").pt("pt1").hlt("hlt1").soc("soc1").usedInTfl(TRUE).
                aeSeverities(
                        newArrayList(
                                AeSeverityRaw.builder().severity(SEVERITY_1).startDate(toDate("04.08.2015")).endDate(toDate("05.08.2015")).build(),
                                AeSeverityRaw.builder().severity(SEVERITY_2).startDate(toDate("06.08.2015")).endDate(toDate("07.08.2015")).build()
                        )
                )
                .build(),
                s1
        );
        final Subject s2 = Subject.builder().subjectCode("E02").datasetId("Study1").subjectId("02").actualArm("arm1").build();
        Ae ae2 = new Ae(AeRaw.builder().id("id2").usedInTfl(TRUE)
                .build(), s2);
        final Subject s3 = Subject.builder().subjectCode("E03").datasetId("Study2").subjectId("03").actualArm("arm2").build();
        Ae ae3 = new Ae(AeRaw.builder().id("id3").usedInTfl(TRUE)
                .build(), s3);
        final Subject s4 = Subject.builder().subjectCode("E04").datasetId("Study3").subjectId("04").actualArm("arm2").build();
        Ae ae4 = new Ae(AeRaw.builder().id("id4").usedInTfl(TRUE)
                .build(), s4);
        List<Ae> aes = Arrays.asList(ae1, ae2, ae3, ae4);

        when(aeIncidenceDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(aes);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Arrays.asList(s1, s2, s3, s4));

        //When
        List<TrellisOptions<AeGroupByOptions>> result
                = aeService.getTrellisOptions(CEREBRO_DATASETS, AeFilters.empty(), PopulationFilters.empty());

        //Then
        softly.assertThat(result.size()).isEqualTo(1);
        softly.assertThat(result.get(0)).isEqualTo(new TrellisOptions<>(AeGroupByOptions.ARM,
                Arrays.asList("arm1", "arm2")));
    }

    @Test
    public void shouldGetTrellisOptionsInCorrectOrder() {
        //Given
        when(aeIncidenceDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(AE_EVENTS);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Arrays.asList(SUBJECT1_ARM1));

        // When
        List<TrellisOptions<AeGroupByOptions>> result
                = aeService.getTrellisOptions(CEREBRO_DATASETS, AeFilters.empty(), PopulationFilters.empty());

        // Then
        softly.assertThat(result)
                .extracting(TrellisOptions::getTrellisedBy)
                .containsExactly(
                        ARM);
    }

    @Test
    public void shouldAvailableOvertimeChartXAxisOptions() {
        //Given
        when(aeIncidenceDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(AE_EVENTS);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Arrays.asList(SUBJECT1_ARM1));

        //When
        AxisOptions<AeGroupByOptions> availableOvertimeXAxis
                = aeService.getAvailableOverTimeChartXAxis(DATASETS, AeFilters.empty(), PopulationFilters.empty());

        //Then
        softly.assertThat(availableOvertimeXAxis.getDrugs()).isNotEmpty();
        softly.assertThat(availableOvertimeXAxis.isHasRandomization()).isTrue();
        softly.assertThat(availableOvertimeXAxis.getOptions())
                .extracting(AxisOption::getGroupByOption, AxisOption::isBinableOption, AxisOption::isTimestampOption)
                .containsExactly(
                        tuple(OVERTIME_DURATION, true, true)
                );
    }

    @Test
    public void testGetSubjects() {
        //Given
        final Subject s1 = Subject.builder().subjectCode("E01").datasetId("Study1").subjectId("01").build();
        Ae ae1 = new Ae(AeRaw.builder().id("id1").build(), s1);
        final Subject s2 = Subject.builder().subjectCode("E02").datasetId("Study1").subjectId("02").build();
        Ae ae2 = new Ae(AeRaw.builder().id("id2").build(), s2);
        final Subject s3 = Subject.builder().subjectCode("E03").datasetId("Study2").subjectId("03").build();
        Ae ae3 = new Ae(AeRaw.builder().id("id3").build(), s3);
        final Subject s4 = Subject.builder().subjectCode("E04").datasetId("Study3").subjectId("04").build();
        Ae ae4 = new Ae(AeRaw.builder().id("id4").build(), s4);
        List<Ae> aes = Arrays.asList(ae1, ae2, ae3, ae4);

        when(aeIncidenceDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(aes);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Arrays.asList(s1, s2, s3, s4));

        //When
        List<String> result = aeService.getSubjects(CEREBRO_DATASETS, AeFilters.empty(), PopulationFilters.empty());

        //Then
        softly.assertThat(result.size()).isEqualTo(4);
    }

    @Test
    public void testGetAeTable() {
        //Given
        Subject subject1 = Subject.builder().actualArm("arm1").subjectCode("E01").datasetId("Study1").subjectId("01").build();
        Subject subject2 = Subject.builder().actualArm("arm2").subjectCode("E02").datasetId("Study1").subjectId("02").build();
        Subject subject3 = Subject.builder().actualArm("arm1").subjectCode("E03").datasetId("Study2").subjectId("03").build();
        Subject subject4 = Subject.builder().actualArm("arm2").subjectCode("E04").datasetId("Study3").subjectId("04").build();
        Subject subject5 = Subject.builder().actualArm("arm2").subjectCode("E05").datasetId("Study4").subjectId("05").build();
        Ae ae1 = new Ae(AeRaw.builder().id("id1").pt("pt1").usedInTfl(TRUE).
                aeSeverities(
                        newArrayList(
                                AeSeverityRaw.builder().severity(SEVERITY_1).startDate(toDate("04.08.2015")).endDate(toDate("05.08.2015")).build()
                        )
                ).build(),
                subject1);
        Ae ae2 = new Ae(AeRaw.builder().id("id2").pt("pt1").usedInTfl(TRUE).
                aeSeverities(
                        newArrayList(
                                AeSeverityRaw.builder().severity(SEVERITY_3).startDate(toDate("04.08.2015")).endDate(toDate("05.08.2015")).build()
                        )
                ).build(),
                subject2);
        Ae ae3 = new Ae(AeRaw.builder().id("id3").pt("pt2").usedInTfl(TRUE).
                aeSeverities(
                        newArrayList(
                                AeSeverityRaw.builder().severity(SEVERITY_2).startDate(toDate("04.08.2015")).endDate(toDate("05.08.2015")).build()
                        )
                ).build(),
                subject3);
        Ae ae4 = new Ae(AeRaw.builder().id("id4").pt("pt1").usedInTfl(TRUE).
                aeSeverities(
                        newArrayList(
                                AeSeverityRaw.builder().severity(SEVERITY_1).startDate(toDate("04.08.2015")).endDate(toDate("05.08.2015")).build()
                        )
                ).build(),
                subject4);
        Ae ae5 = new Ae(AeRaw.builder().id("id5").pt(null).usedInTfl(TRUE).
                aeSeverities(
                        newArrayList(
                                AeSeverityRaw.builder().severity(SEVERITY_NULL).startDate(toDate("04.08.2015")).startDate(toDate("05.08.2015")).build()
                        )
                ).build(),
                subject5);
        List<Ae> aes = Arrays.asList(ae1, ae2, ae3, ae4, ae5);
        List<Subject> subjects = Arrays.asList(subject1, subject2, subject3, subject4, subject5);

        when(aeIncidenceDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(aes);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(subjects);

        //When
        List<AesTable> result = aeService.getAesTableData(DUMMY_DETECT_DATASETS, PT, AeFilters.empty(), PopulationFilters.empty());

        //Then
        assertThat(result)
                .hasSize(9)
                .extracting("term", "grade", "treatmentArm", "subjectCountPerGrade", "subjectCountPerTerm",
                        "subjectCountPerArm", "noIncidenceCount")
                .startsWith(tuple("No term recorded", "No severity grade recorded", "All", 1, 1, 5, 4))
                .contains(tuple("pt1", "CTC Grade 1", "arm2", 1, 2, 3, 1))
                .contains(tuple("pt1", "CTC Grade 1", "All", 2, 3, 5, 2))
                .endsWith(tuple("pt2", "CTC Grade 2", "All", 1, 1, 5, 4));
    }

    @Test
    public void testGetAeTableSeverity() {
        //Given
        Subject subject1 = Subject.builder().actualArm("arm1").subjectCode("E01").datasetId("Study1").subjectId("01").build();
        Subject subject2 = Subject.builder().actualArm("arm2").subjectCode("E02").datasetId("Study1").subjectId("02").build();
        Ae ae1 = new Ae(AeRaw.builder().id("id1").specialInterestGroups(newArrayList("SIG1", "SIG2")).usedInTfl(TRUE).
                aeSeverities(
                        newArrayList(
                                AeSeverityRaw.builder().severity(SEVERITY_1).startDate(toDate("04.08.2015")).endDate(toDate("05.08.2015")).build()
                        )
                ).build(),
                subject1);
        Ae ae2 = new Ae(AeRaw.builder().id("id2").specialInterestGroups(newArrayList("SIG3", "SIG2")).usedInTfl(TRUE).
                aeSeverities(
                        newArrayList(
                                AeSeverityRaw.builder().severity(SEVERITY_3).startDate(toDate("04.08.2015")).endDate(toDate("05.08.2015")).build()
                        )
                ).build(),
                subject2);
        List<Ae> aes = Arrays.asList(ae1, ae2);
        List<Subject> subjects = Arrays.asList(subject1, subject2);

        when(aeIncidenceDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(aes);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(subjects);

        //When
        List<AesTable> result = aeService.
                getAesTableData(DUMMY_DETECT_DATASETS, AeGroupByOptions.SPECIAL_INTEREST_GROUP, AeFilters.empty(), PopulationFilters.empty());

        //Then
        result.forEach(System.out::println);

        assertThat(result)
                .hasSize(8)
                .extracting("term", "grade", "treatmentArm", "subjectCountPerGrade", "subjectCountPerTerm",
                        "subjectCountPerArm", "noIncidenceCount")
                .startsWith(tuple("SIG1", "CTC Grade 1", "arm1", 1, 1, 1, 0))
                .contains(tuple("SIG2", "CTC Grade 3", "All", 1, 2, 2, 0))
                .endsWith(tuple("SIG3", "CTC Grade 3", "arm2", 1, 1, 1, 0));
    }


    @Test
    public void shouldGetSortedAeTableData() {
        //Given
        Subject subject1 = Subject.builder().actualArm("arm1").subjectCode("E01").datasetId("Study1").subjectId("01").build();
        Subject subject2 = Subject.builder().actualArm("arm2").subjectCode("E02").datasetId("Study1").subjectId("02").build();
        Subject subject3 = Subject.builder().actualArm("arm1").subjectCode("E03").datasetId("Study2").subjectId("03").build();
        Subject subject4 = Subject.builder().actualArm("arm2").subjectCode("E04").datasetId("Study3").subjectId("04").build();
        Ae ae1 = new Ae(AeRaw.builder().id("id1").pt("RESTLESS LEGS").usedInTfl(TRUE).
                aeSeverities(
                        newArrayList(
                                AeSeverityRaw.builder().severity(SEVERITY_1).build()
                        )
                ).build(),
                subject1);
        Ae ae2 = new Ae(AeRaw.builder().id("id2").pt("RESTLESS LEG_S").usedInTfl(TRUE).
                aeSeverities(
                        newArrayList(
                                AeSeverityRaw.builder().severity(SEVERITY_1).build()
                        )
                ).build(),
                subject2);
        Ae ae3 = new Ae(AeRaw.builder().id("id3").pt("RESTLESS LEG S").usedInTfl(TRUE).
                aeSeverities(
                        newArrayList(
                                AeSeverityRaw.builder().severity(SEVERITY_1).build()
                        )
                ).build(),
                subject3);
        Ae ae4 = new Ae(AeRaw.builder().id("id4").pt("RESTLESS LEGS SYNDROME").usedInTfl(TRUE).
                aeSeverities(
                        newArrayList(
                                AeSeverityRaw.builder().severity(SEVERITY_1).build()
                        )
                ).build(),
                subject4);
        List<Ae> aes = Arrays.asList(ae1, ae2, ae3, ae4);
        List<Subject> subjects = Arrays.asList(subject1, subject2, subject3, subject4);

        when(aeIncidenceDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(aes);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(subjects);

        //When
        List<AesTable> result = aeService.getAesTableData(DUMMY_DETECT_DATASETS, PT, AeFilters.empty(), PopulationFilters.empty());
        //Then
        assertThat(result)
                .extracting("term")
                .asList()
                .containsExactly("RESTLESS LEG S", "RESTLESS LEG S", "RESTLESS LEGS", "RESTLESS LEGS",
                        "RESTLESS LEGS SYNDROME", "RESTLESS LEGS SYNDROME", "RESTLESS LEG_S", "RESTLESS LEG_S");
    }

    @Test
    public void shouldGetLineBarChartBinnedByDaysSinceFirstDose() {

        Ae ae1 = new Ae(
                AeRaw.builder().id("id1").pt("pst1").usedInTfl(TRUE).
                        aeSeverities(
                                newArrayList(
                                        AeSeverityRaw.builder().severity(SEVERITY_1).startDate(toDate("01.08.2015")).endDate(toDate("07.08.2015")).build()
                                )
                        ).build(), SUBJECT1_ARM1);
        Ae ae2 = new Ae(
                AeRaw.builder().id("id2").pt("pnt2").usedInTfl(TRUE).
                        aeSeverities(
                                newArrayList(
                                        AeSeverityRaw.builder().severity(SEVERITY_1).startDate(toDate("10.08.2015")).endDate(toDate("11.08.2015")).build()
                                )
                        ).build(), SUBJECT1_ARM1);
        Ae ae3 = new Ae(
                AeRaw.builder().id("id3").pt("pst1").usedInTfl(TRUE).
                        aeSeverities(
                                newArrayList(
                                        AeSeverityRaw.builder().severity(SEVERITY_1).startDate(toDate("10.08.2015")).endDate(toDate("11.08.2015")).build()
                                )
                        ).build(), SUBJECT1_ARM1);
        Ae ae4 = new Ae(
                AeRaw.builder().id("id4").pt(null).usedInTfl(TRUE).
                        aeSeverities(
                                newArrayList(
                                        AeSeverityRaw.builder().severity(SEVERITY_1).startDate(toDate("10.08.2015")).endDate(toDate("11.08.2015")).build()
                                )
                        ).build(), SUBJECT1_ARM1);
        Ae ae5 = new Ae(
                AeRaw.builder().id("id5").pt(null).usedInTfl(TRUE).
                        aeSeverities(
                                newArrayList(
                                        AeSeverityRaw.builder().severity(SEVERITY_1).startDate(toDate("10.08.2015")).endDate(toDate("11.08.2015")).build()
                                )
                        ).build(), SUBJECT2_ARM2
        );
        Ae ae6 = new Ae(
                AeRaw.builder().id("id6").pt("pst1").usedInTfl(TRUE).
                        aeSeverities(
                                newArrayList(
                                        AeSeverityRaw.builder().severity(SEVERITY_1).startDate(toDate("05.08.2015")).endDate(toDate("12.08.2015")).build()
                                )
                        ).build(), SUBJECT3_ARM1
        );
        List<Ae> aes = Arrays.asList(ae1, ae2, ae3, ae4, ae5, ae6);

        when(aeIncidenceDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(aes);
        when(aeIncidenceDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(aes);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Arrays.asList(SUBJECT1_ARM1, SUBJECT2_ARM2, SUBJECT3_ARM1));

        final ChartGroupByOptions.ChartGroupBySettingsBuilder<Ae, AeGroupByOptions> settings = ChartGroupByOptions.builder();
        settings.withOption(COLOR_BY, AeGroupByOptions.MAX_SEVERITY_GRADE.getGroupByOptionAndParams());
        settings.withOption(X_AXIS, AeGroupByOptions.OVERTIME_DURATION.getGroupByOptionAndParams(
                GroupByOption.Params.builder()
                        .with(GroupByOption.Param.BIN_INCL_DURATION, true)
                        .with(GroupByOption.Param.BIN_SIZE, 1)
                        .with(Param.TIMESTAMP_TYPE, TimestampType.DAYS_SINCE_FIRST_DOSE)
                        .build()));
        final ChartGroupByOptions.GroupByOptionAndParams<Ae, AeGroupByOptions> trellis = ARM.getGroupByOptionAndParams();
        settings.withTrellisOptions(new HashSet<>(Collections.singletonList(trellis)));
        final ChartGroupByOptionsFiltered.ChartGroupBySettingsFilteredBuilder<Ae, AeGroupByOptions> settingsWithFilterBy =
                ChartGroupByOptionsFiltered.builder(settings.build());
        settingsWithFilterBy.withFilterByTrellisOption(ARM, "arm1");
        settingsWithFilterBy.withFilterByTrellisOption(ARM, "arm2");

        List<TrellisedOvertime<Ae, AeGroupByOptions>> overtime = aeService.getLineBarChart(DUMMY_CVOT_DATASETS,
                settingsWithFilterBy.build(), AeFilters.empty(), PopulationFilters.empty());

        OutputOvertimeData arm1chart = overtime.stream().filter(e -> "arm1".equals(e.getTrellisedBy().get(0)
                .getTrellisOption())).findFirst().get().getData();

        softly.assertThat(arm1chart.getCategories()).hasSize(12);
        softly.assertThat(arm1chart.getCategories()).containsExactlyElementsOf(IntStream.rangeClosed(0, 11)
                .boxed().map(Object::toString).collect(Collectors.toList()));

        softly.assertThat(arm1chart.getSeries()).hasSize(1);
        softly.assertThat(arm1chart.getSeries()).extracting(OutputBarChartData::getName).
                containsExactly("CTC Grade 1");

        softly.assertThat(arm1chart.getSeries().get(0).getSeries()).extracting("rank", "category", "value", "totalSubjects").containsExactlyInAnyOrder(
                tuple(1, "0", 1.0, 1),
                tuple(2, "1", 1.0, 1),
                tuple(3, "2", 1.0, 1),
                tuple(4, "3", 1.0, 1),
                tuple(5, "4", 2.0, 2),
                tuple(6, "5", 2.0, 2),
                tuple(7, "6", 2.0, 2),
                tuple(8, "7", 1.0, 1),
                tuple(9, "8", 1.0, 1),
                tuple(10, "9", 4.0, 2),
                tuple(11, "10", 4.0, 2),
                tuple(12, "11", 1.0, 1)
        );

        List<OutputBarChartEntry> line = arm1chart.getLines().get(0).getSeries();
        softly.assertThat(line).hasSize(12);
        softly.assertThat(line).extracting("rank", "category", "value", "totalSubjects").containsExactlyInAnyOrder(
                tuple(1, "0", 2.0, 2),
                tuple(2, "1", 2.0, 2),
                tuple(3, "2", 2.0, 2),
                tuple(4, "3", 2.0, 2),
                tuple(5, "4", 2.0, 2),
                tuple(6, "5", 2.0, 2),
                tuple(7, "6", 2.0, 2),
                tuple(8, "7", 2.0, 2),
                tuple(9, "8", 2.0, 2),
                tuple(10, "9", 2.0, 2),
                tuple(11, "10", 1.0, 1),
                tuple(12, "11", 1.0, 1)
        );
    }

    @Test
    public void shouldGetLineBarChartBinnedByDaysSinceFirstDoseWithMoreEventsInclDuration() {
        final Subject subject1 = Subject.builder().subjectId("sid1").subjectCode("E01").datasetId("test")
                .firstTreatmentDate(toDate("01.08.2015"))
                .lastTreatmentDate(toDate("09.08.2016"))
                .studyLeaveDate(toDate("01.08.2016"))
                .drugFirstDoseDate(DRUG_FIRST_DOSE_DATE)
                .dateOfRandomisation(toDate("01.12.1999"))
                .actualArm("arm1").build();

        final Subject subject2 = Subject.builder().subjectId("sid2").subjectCode("E02").datasetId("test")
                .firstTreatmentDate(toDate("02.08.2015"))
                .lastTreatmentDate(toDate("09.08.2015"))
                .studyLeaveDate(toDate("09.08.2015"))
                .drugFirstDoseDate(DRUG_FIRST_DOSE_DATE)
                .dateOfRandomisation(toDate("01.12.1999"))
                .actualArm("arm2").build();

        Ae ae1 = new Ae(
                AeRaw.builder().id("id1").usedInTfl(TRUE).aeSeverities(
                        newArrayList(
                                AeSeverityRaw.builder().severity(SEVERITY_1).startDate(toDate("5.08.2015")).endDate(toDate("11.08.2015")).build()
                        )
                ).pt("pst1").build(),
                subject1);
        Ae ae2 = new Ae(
                AeRaw.builder().id("id2").usedInTfl(TRUE).aeSeverities(
                        newArrayList(
                                AeSeverityRaw.builder().severity(SEVERITY_1).startDate(toDate("6.08.2015")).endDate(toDate("11.08.2015")).build()
                        )
                ).pt("pst1").build(),
                subject1);
        Ae ae3 = new Ae(
                AeRaw.builder().id("id3").usedInTfl(TRUE).aeSeverities(
                        newArrayList(
                                AeSeverityRaw.builder().severity(SEVERITY_1).startDate(toDate("5.08.2015")).endDate(toDate("11.08.2015")).build()
                        )
                ).pt("pnt2").build(),
                subject1);
        Ae ae4 = new Ae(
                AeRaw.builder().id("id4").usedInTfl(TRUE).aeSeverities(
                        newArrayList(
                                AeSeverityRaw.builder().severity(SEVERITY_1).startDate(toDate("10.08.2015")).endDate(toDate("11.08.2015")).build()
                        )
                ).pt("pst1").build(),
                subject1);
        Ae ae5 = new Ae(
                AeRaw.builder().id("id5").usedInTfl(TRUE).aeSeverities(
                        newArrayList(
                                AeSeverityRaw.builder().severity(SEVERITY_1).startDate(toDate("11.08.2015")).endDate(toDate("12.08.2015")).build()
                        )
                ).pt(null).build(),
                subject1);
        Ae ae6 = new Ae(
                AeRaw.builder().id("id6").usedInTfl(TRUE).aeSeverities(
                        newArrayList(
                                AeSeverityRaw.builder().severity(SEVERITY_1).startDate(toDate("10.08.2015")).endDate(toDate("11.08.2015")).build()
                        )
                ).pt("pst1").build(),
                subject2);
        Ae ae7 = new Ae(
                AeRaw.builder().id("id7").usedInTfl(TRUE).aeSeverities(
                        newArrayList(
                                AeSeverityRaw.builder().severity(SEVERITY_1).startDate(toDate("11.08.2015")).endDate(toDate("12.08.2015")).build()
                        )
                ).pt("pnt2").build(),
                subject2);
        List<Ae> aes = newArrayList(
                ae1, ae2, ae3, ae4, ae5, ae6, ae7
        );

        when(aeIncidenceDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(aes);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Arrays.asList(subject1, subject2));

    /*    Set<TrellisOptions<AeGroupByOptions>> trellis = newHashSet(
                new TrellisOptions<>(
                        PT,
                        newHashSet("(Empty)", "pst1", "pnt2"),
                        TrellisCategories.NON_MANDATORY_SERIES));
*/

        final ChartGroupByOptions.ChartGroupBySettingsBuilder<Ae, AeGroupByOptions> settings = ChartGroupByOptions.builder();
        settings.withOption(COLOR_BY, AeGroupByOptions.PT.getGroupByOptionAndParams());
        settings.withOption(X_AXIS, AeGroupByOptions.OVERTIME_DURATION.getGroupByOptionAndParams(
                GroupByOption.Params.builder()
                        .with(Param.BIN_INCL_DURATION, true)
                        .with(GroupByOption.Param.BIN_SIZE, 1)
                        .with(Param.TIMESTAMP_TYPE, TimestampType.DATE)
                        .build()));
        //final ChartGroupByOptions.GroupByOptionAndParams<AeGroupByOptions> trellis = ARM.getGroupByOptionAndParams();
        //SETTINGS.withTrellisOptions(new HashSet<>(Collections.singletonList(trellis)));
        final ChartGroupByOptionsFiltered.ChartGroupBySettingsFilteredBuilder<Ae, AeGroupByOptions> settingsWithFilterBy =
                ChartGroupByOptionsFiltered.builder(settings.build());
//        settingsWithFilterBy.withFilterByTrellisOption(ARM, Arrays.asList("arm1", "arm2"));

        //no trelis here, let's just test barchart/population line calcs

        List<TrellisedOvertime<Ae, AeGroupByOptions>> overtime = aeService.getLineBarChart(DUMMY_CVOT_DATASETS,
                settingsWithFilterBy.build(), AeFilters.empty(), PopulationFilters.empty());

        System.out.println(overtime);

        OutputOvertimeData chart = overtime.stream()
                //.filter(c -> c.getTrellisedBy().get(0).getTrellisOption().equals("arm1"))
                .findFirst().get().getData();

        softly.assertThat(chart.getCategories()).hasSize(8);
        softly.assertThat(chart.getCategories()).containsExactlyInAnyOrder(
                "2015-08-05",
                "2015-08-06",
                "2015-08-07",
                "2015-08-08",
                "2015-08-09",
                "2015-08-10",
                "2015-08-11",
                "2015-08-12"
        );

        softly.assertThat(chart.getSeries()).hasSize(3);
        softly.assertThat(chart.getSeries()).extracting(OutputBarChartData::getName).
                containsExactly("pnt2", "pst1", "(Empty)");

        softly.assertThat(chart.getSeries().get(2).getCategories()).containsExactly(
                "2015-08-05",
                "2015-08-06",
                "2015-08-07",
                "2015-08-08",
                "2015-08-09",
                "2015-08-10",
                "2015-08-11",
                "2015-08-12"
        );
        softly.assertThat(chart.getSeries().get(2).getSeries()).extracting("rank", "category", "value", "totalSubjects").containsExactlyInAnyOrder(
                tuple(7, "2015-08-11", 1.0, 1),
                tuple(8, "2015-08-12", 1.0, 1)
        );
        softly.assertThat(chart.getSeries().get(1).getCategories()).containsExactly(
                "2015-08-05",
                "2015-08-06",
                "2015-08-07",
                "2015-08-08",
                "2015-08-09",
                "2015-08-10",
                "2015-08-11",
                "2015-08-12"
        );
        softly.assertThat(chart.getSeries().get(1).getSeries()).extracting("rank", "category", "value", "totalSubjects").containsExactlyInAnyOrder(
                tuple(6, "2015-08-10", 4.0, 2),
                tuple(3, "2015-08-07", 2.0, 1),
                tuple(7, "2015-08-11", 4.0, 2),
                tuple(4, "2015-08-08", 2.0, 1),
                tuple(5, "2015-08-09", 2.0, 1),
                tuple(2, "2015-08-06", 2.0, 1),
                tuple(1, "2015-08-05", 1.0, 1)
        );
        softly.assertThat(chart.getSeries().get(0).getCategories()).containsExactly(
                "2015-08-05",
                "2015-08-06",
                "2015-08-07",
                "2015-08-08",
                "2015-08-09",
                "2015-08-10",
                "2015-08-11",
                "2015-08-12");
        softly.assertThat(chart.getSeries().get(0).getSeries()).extracting("rank", "category", "value", "totalSubjects").containsExactlyInAnyOrder(
                tuple(5, "2015-08-09", 1.0, 1),
                tuple(7, "2015-08-11", 2.0, 2),
                tuple(4, "2015-08-08", 1.0, 1),
                tuple(6, "2015-08-10", 1.0, 1),
                tuple(8, "2015-08-12", 1.0, 1),
                tuple(3, "2015-08-07", 1.0, 1),
                tuple(2, "2015-08-06", 1.0, 1),
                tuple(1, "2015-08-05", 1.0, 1)
        );

        List<OutputBarChartEntry> line = chart.getLines().get(0).getSeries();
        softly.assertThat(line).hasSize(8);
        softly.assertThat(line).extracting("rank", "category", "value", "totalSubjects").containsExactlyInAnyOrder(
                tuple(1, "2015-08-05", 2.0, 2),
                tuple(2, "2015-08-06", 2.0, 2),
                tuple(3, "2015-08-07", 2.0, 2),
                tuple(4, "2015-08-08", 2.0, 2),
                tuple(5, "2015-08-09", 2.0, 2),
                tuple(6, "2015-08-10", 1.0, 1),
                tuple(7, "2015-08-11", 1.0, 1),
                tuple(8, "2015-08-12", 1.0, 1)
        );
    }

    @Test
    public void shouldGetLineBarChartBinnedByDaysSinceFirstDoseWithMoreEventsExclDuration() {
        final Subject subject1 = Subject.builder().subjectId("sid1").subjectCode("E01").datasetId("test")
                .firstTreatmentDate(toDate("01.08.2015"))
                .lastTreatmentDate(toDate("09.08.2016"))
                .studyLeaveDate(toDate("01.08.2016"))
                .drugFirstDoseDate(DRUG_FIRST_DOSE_DATE)
                .dateOfRandomisation(toDate("01.12.1999"))
                .actualArm("arm1").build();

        final Subject subject2 = Subject.builder().subjectId("sid2").subjectCode("E02").datasetId("test")
                .firstTreatmentDate(toDate("02.08.2015"))
                .lastTreatmentDate(toDate("09.08.2015"))
                .studyLeaveDate(toDate("09.08.2015"))
                .drugFirstDoseDate(DRUG_FIRST_DOSE_DATE)
                .dateOfRandomisation(toDate("01.12.1999"))
                .actualArm("arm2").build();

        Ae ae1 = new Ae(
                AeRaw.builder().id("id1").usedInTfl(TRUE).aeSeverities(
                        newArrayList(
                                AeSeverityRaw.builder().severity(SEVERITY_1).startDate(toDate("5.08.2015")).endDate(toDate("11.08.2015")).build()
                        )
                ).pt("pst1").build(),
                subject1);
        Ae ae2 = new Ae(
                AeRaw.builder().id("id2").usedInTfl(TRUE).aeSeverities(
                        newArrayList(
                                AeSeverityRaw.builder().severity(SEVERITY_1).startDate(toDate("6.08.2015")).endDate(toDate("11.08.2015")).build()
                        )
                ).pt("pst1").build(),
                subject1);
        Ae ae3 = new Ae(
                AeRaw.builder().id("id3").usedInTfl(TRUE).aeSeverities(
                        newArrayList(
                                AeSeverityRaw.builder().severity(SEVERITY_1).startDate(toDate("5.08.2015")).endDate(toDate("11.08.2015")).build()
                        )
                ).pt("pnt2").build(),
                subject1);
        Ae ae4 = new Ae(
                AeRaw.builder().id("id4").usedInTfl(TRUE).aeSeverities(
                        newArrayList(
                                AeSeverityRaw.builder().severity(SEVERITY_1).startDate(toDate("10.08.2015")).endDate(toDate("11.08.2015")).build()
                        )
                ).pt("pst1").build(),
                subject1);
        Ae ae5 = new Ae(
                AeRaw.builder().id("id5").usedInTfl(TRUE).aeSeverities(
                        newArrayList(
                                AeSeverityRaw.builder().severity(SEVERITY_1).startDate(toDate("11.08.2015")).endDate(toDate("12.08.2015")).build()
                        )
                ).pt(null).build(),
                subject1);
        Ae ae6 = new Ae(
                AeRaw.builder().id("id6").usedInTfl(TRUE).aeSeverities(
                        newArrayList(
                                AeSeverityRaw.builder().severity(SEVERITY_1).startDate(toDate("10.08.2015")).endDate(toDate("11.08.2015")).build()
                        )
                ).pt("pst1").build(),
                subject2);
        Ae ae7 = new Ae(
                AeRaw.builder().id("id7").usedInTfl(TRUE).aeSeverities(
                        newArrayList(
                                AeSeverityRaw.builder().severity(SEVERITY_1).startDate(toDate("11.08.2015")).endDate(toDate("12.08.2015")).build()
                        )
                ).pt("pnt2").build(),
                subject2);
        List<Ae> aes = newArrayList(
                ae1, ae2, ae3, ae4, ae5, ae6, ae7
        );

        when(aeIncidenceDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(aes);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Arrays.asList(subject1, subject2));

        final ChartGroupByOptions.ChartGroupBySettingsBuilder<Ae, AeGroupByOptions> settings = ChartGroupByOptions.builder();
        settings.withOption(COLOR_BY, AeGroupByOptions.PT.getGroupByOptionAndParams());
        settings.withOption(X_AXIS, AeGroupByOptions.OVERTIME_DURATION.getGroupByOptionAndParams(
                GroupByOption.Params.builder()
                        //NO DURACTION, JUST START DATES
                        .with(Param.BIN_INCL_DURATION, false)
                        .with(GroupByOption.Param.BIN_SIZE, 1)
                        .with(Param.TIMESTAMP_TYPE, TimestampType.DATE)
                        .build()));
        final ChartGroupByOptions.GroupByOptionAndParams<Ae, AeGroupByOptions> trellis = ARM.getGroupByOptionAndParams();
        settings.withTrellisOptions(new HashSet<>(Collections.singletonList(trellis)));
        final ChartGroupByOptionsFiltered.ChartGroupBySettingsFilteredBuilder<Ae, AeGroupByOptions> settingsWithFilterBy =
                ChartGroupByOptionsFiltered.builder(settings.build());
        settingsWithFilterBy.withFilterByTrellisOption(ARM, "arm1");
        settingsWithFilterBy.withFilterByTrellisOption(ARM, "arm2");

        List<TrellisedOvertime<Ae, AeGroupByOptions>> overtime = aeService.getLineBarChart(DUMMY_CVOT_DATASETS,
                settingsWithFilterBy.build(), AeFilters.empty(), PopulationFilters.empty());

        System.out.println(overtime);

        OutputOvertimeData arm1chart = overtime.stream()
                .filter(c -> c.getTrellisedBy().get(0).getTrellisOption().equals("arm1"))
                .findFirst().get().getData();

        softly.assertThat(arm1chart.getCategories()).hasSize(7);
        softly.assertThat(arm1chart.getCategories()).containsExactlyInAnyOrder(
                "2015-08-05",
                "2015-08-06",
                "2015-08-07",
                "2015-08-08",
                "2015-08-09",
                "2015-08-10",
                "2015-08-11"
        );

        softly.assertThat(arm1chart.getSeries()).hasSize(3);
        softly.assertThat(arm1chart.getSeries()).extracting(OutputBarChartData::getName).
                containsExactly("pnt2", "pst1", "(Empty)");

        softly.assertThat(arm1chart.getSeries().get(2).getCategories()).containsExactly(
                "2015-08-05",
                "2015-08-06",
                "2015-08-07",
                "2015-08-08",
                "2015-08-09",
                "2015-08-10",
                "2015-08-11"
        );
        softly.assertThat(arm1chart.getSeries().get(2).getSeries()).extracting("rank", "category", "value", "totalSubjects").containsExactlyInAnyOrder(
                tuple(7, "2015-08-11", 1.0, 1)
        );
        softly.assertThat(arm1chart.getSeries().get(1).getCategories()).containsExactly(
                "2015-08-05",
                "2015-08-06",
                "2015-08-07",
                "2015-08-08",
                "2015-08-09",
                "2015-08-10",
                "2015-08-11"
        );
        softly.assertThat(arm1chart.getSeries().get(1).getSeries()).extracting("rank", "category", "value", "totalSubjects").containsExactlyInAnyOrder(
                tuple(2, "2015-08-06", 1.0, 1),
                tuple(1, "2015-08-05", 1.0, 1),
                tuple(6, "2015-08-10", 1.0, 1)
        );
        softly.assertThat(arm1chart.getSeries().get(0).getCategories()).containsExactly(
                "2015-08-05",
                "2015-08-06",
                "2015-08-07",
                "2015-08-08",
                "2015-08-09",
                "2015-08-10",
                "2015-08-11"
        );
        softly.assertThat(arm1chart.getSeries().get(0).getSeries()).extracting("rank", "category", "value", "totalSubjects").containsExactlyInAnyOrder(
                tuple(1, "2015-08-05", 1.0, 1)
        );

        List<OutputBarChartEntry> line = arm1chart.getLines().get(0).getSeries();
        softly.assertThat(line).hasSize(7);
        softly.assertThat(line).extracting("rank", "category", "value", "totalSubjects").containsExactlyInAnyOrder(
                tuple(1, "2015-08-05", 1.0, 1),
                tuple(2, "2015-08-06", 1.0, 1),
                tuple(3, "2015-08-07", 1.0, 1),
                tuple(4, "2015-08-08", 1.0, 1),
                tuple(5, "2015-08-09", 1.0, 1),
                tuple(6, "2015-08-10", 1.0, 1),
                tuple(7, "2015-08-11", 1.0, 1)
        );
    }

    @Test
    public void shouldGetBarChartCountingEvents() throws Exception {
        List<Ae> aes = getBarchartTestAes();

        when(aeIncidenceDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(aes);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(
                aes.stream().map(SubjectAwareWrapper::getSubject).collect(Collectors.toList()));


        final ChartGroupByOptions.ChartGroupBySettingsBuilder<Ae, AeGroupByOptions> settings = ChartGroupByOptions.builder();
        settings.withOption(COLOR_BY, AeGroupByOptions.MAX_SEVERITY_GRADE.getGroupByOptionAndParams());
        settings.withOption(X_AXIS, AeGroupByOptions.PT.getGroupByOptionAndParams());
        final ChartGroupByOptions.GroupByOptionAndParams<Ae, AeGroupByOptions> trellis = ARM.getGroupByOptionAndParams();
        settings.withTrellisOptions(new HashSet<>(Collections.singletonList(trellis)));
        final ChartGroupByOptionsFiltered.ChartGroupBySettingsFilteredBuilder<Ae, AeGroupByOptions> settingsWithFilterBy =
                ChartGroupByOptionsFiltered.builder(settings.build());
        settingsWithFilterBy.withFilterByTrellisOption(ARM, "arm1");
        settingsWithFilterBy.withFilterByTrellisOption(ARM, "arm2");

        //When
        List<TrellisedBarChart<Ae, AeGroupByOptions>> result = aeService.getBarChart(DATASETS, settingsWithFilterBy.build(),
                AeFilters.empty(), PopulationFilters.empty(), CountType.COUNT_OF_EVENTS);

        //Then
        softly.assertThat(result.size()).isEqualTo(2);
        List<? extends OutputBarChartData> arm1chart = result.stream().filter(c -> c.getTrellisedBy().get(0).getTrellisOption()
                .equals("arm1")).findFirst().get().getData();
        softly.assertThat(arm1chart).hasSize(3);
        softly.assertThat(arm1chart).extracting("name", "color").containsExactlyInAnyOrder(
                tuple("CTC Grade 1", "#B4DA50"),
                tuple("CTC Grade 2", "#F7D533"),
                tuple("(Empty)", "#88CCEE")
        );

        softly.assertThat(arm1chart.get(2).getName()).isEqualTo("(Empty)");
        softly.assertThat(arm1chart.get(2).getCategories()).containsExactlyInAnyOrder("pt1", "pt2");
        softly.assertThat(arm1chart.get(2).getSeries()).extracting("value").containsExactly(1.0);
        softly.assertThat(arm1chart.get(2).getSeries()).extracting("totalSubjects").containsExactly(1);
        softly.assertThat(arm1chart.get(2).getSeries()).extracting("category").containsOnly("pt2");
        softly.assertThat(arm1chart.get(0).getName()).isEqualTo("CTC Grade 1");
        softly.assertThat(arm1chart.get(0).getCategories()).containsExactlyInAnyOrder("pt1", "pt2");
        softly.assertThat(arm1chart.get(0).getSeries()).extracting("value").containsExactly(3.0, 1.0);
        softly.assertThat(arm1chart.get(0).getSeries()).extracting("totalSubjects").containsExactly(2, 1);
        softly.assertThat(arm1chart.get(0).getSeries()).extracting("category").containsExactly("pt1", "pt2");
        softly.assertThat(arm1chart.get(0).getSeries()).extracting("rank").containsExactly(1, 2);
        softly.assertThat(arm1chart.get(1).getName()).isEqualTo("CTC Grade 2");
        softly.assertThat(arm1chart.get(1).getCategories()).containsExactlyInAnyOrder("pt1", "pt2");
        softly.assertThat(arm1chart.get(1).getSeries()).extracting("value").containsExactly(1.0);
        softly.assertThat(arm1chart.get(1).getSeries()).extracting("totalSubjects").containsExactly(1);
        softly.assertThat(arm1chart.get(1).getSeries()).extracting("category").containsOnly("pt1");

        List<? extends OutputBarChartData> arm2chart = result.stream().filter(c -> c.getTrellisedBy().get(0).getTrellisOption()
                .equals("arm2")).findFirst().get().getData();
        softly.assertThat(arm2chart).hasSize(2);
        softly.assertThat(arm2chart.get(0).getName()).isEqualTo("CTC Grade 1");
        softly.assertThat(arm2chart.get(0).getCategories()).containsExactlyInAnyOrder("pt1", "pt2");
        softly.assertThat(arm2chart.get(0).getSeries()).extracting("value").containsOnly(1.0);
        softly.assertThat(arm2chart.get(0).getSeries()).extracting("category").containsOnly("pt1");
        softly.assertThat(arm2chart.get(1).getName()).isEqualTo("CTC Grade 2");
        softly.assertThat(arm2chart.get(1).getCategories()).containsExactlyInAnyOrder("pt1", "pt2");
        softly.assertThat(arm2chart.get(1).getSeries()).extracting("value").containsOnly(1.0);
        softly.assertThat(arm2chart.get(1).getSeries()).extracting("category").containsOnly("pt2");
    }

    @Test
    public void shouldGetBarChartCountingSubjects() throws Exception {
        //this thing is really tricky..

        List<Ae> aes = getBarchartTestAes();

        when(aeIncidenceDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(aes);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(
                aes.stream().map(SubjectAwareWrapper::getSubject).collect(Collectors.toList()));


        final ChartGroupByOptions.ChartGroupBySettingsBuilder<Ae, AeGroupByOptions> settings = ChartGroupByOptions.builder();
        settings.withOption(COLOR_BY, AeGroupByOptions.MAX_SEVERITY_GRADE.getGroupByOptionAndParams());
        settings.withOption(X_AXIS, AeGroupByOptions.PT.getGroupByOptionAndParams());
        final ChartGroupByOptions.GroupByOptionAndParams<Ae, AeGroupByOptions> trellis = ARM.getGroupByOptionAndParams();
        settings.withTrellisOptions(new HashSet<>(Collections.singletonList(trellis)));
        final ChartGroupByOptionsFiltered.ChartGroupBySettingsFilteredBuilder<Ae, AeGroupByOptions> settingsWithFilterBy =
                ChartGroupByOptionsFiltered.builder(settings.build());
        settingsWithFilterBy.withFilterByTrellisOption(ARM, "arm1");
        settingsWithFilterBy.withFilterByTrellisOption(ARM, "arm2");

        //When
        List<TrellisedBarChart<Ae, AeGroupByOptions>> result = aeService.getBarChart(DATASETS, settingsWithFilterBy.build(),
                AeFilters.empty(), PopulationFilters.empty(), CountType.COUNT_OF_SUBJECTS);

        //Then
        softly.assertThat(result.size()).isEqualTo(2);
        List<? extends OutputBarChartData> arm1chart = result.stream().filter(c -> c.getTrellisedBy().get(0)
                .getTrellisOption().equals("arm1")).findFirst().get().getData();
        softly.assertThat(arm1chart).hasSize(3);
        softly.assertThat(arm1chart).extracting("name", "color").containsExactlyInAnyOrder(
                tuple("CTC Grade 1", "#B4DA50"),
                tuple("CTC Grade 2", "#F7D533"),
                tuple("(Empty)", "#88CCEE")
        );

        softly.assertThat(arm1chart.get(2).getName()).isEqualTo("(Empty)");
        softly.assertThat(arm1chart.get(2).getCategories()).containsExactlyInAnyOrder("pt1", "pt2");
        softly.assertThat(arm1chart.get(2).getSeries()).extracting("value").containsExactly(1.0);
        softly.assertThat(arm1chart.get(2).getSeries()).extracting("totalSubjects").containsExactly(1);
        softly.assertThat(arm1chart.get(2).getSeries()).extracting("category").containsOnly("pt2");
        softly.assertThat(arm1chart.get(0).getName()).isEqualTo("CTC Grade 1");
        softly.assertThat(arm1chart.get(0).getCategories()).containsExactlyInAnyOrder("pt1", "pt2");
        softly.assertThat(arm1chart.get(0).getSeries()).extracting("value").containsExactly(1.0, 1.0);
        softly.assertThat(arm1chart.get(0).getSeries()).extracting("totalSubjects").containsExactly(1, 1);
        softly.assertThat(arm1chart.get(0).getSeries()).extracting("category").containsExactly("pt1", "pt2");
        softly.assertThat(arm1chart.get(0).getSeries()).extracting("rank").containsExactly(1, 2);
        softly.assertThat(arm1chart.get(1).getName()).isEqualTo("CTC Grade 2");
        softly.assertThat(arm1chart.get(1).getCategories()).containsExactlyInAnyOrder("pt1", "pt2");
        softly.assertThat(arm1chart.get(1).getSeries()).extracting("value").containsExactly(1.0);
        softly.assertThat(arm1chart.get(1).getSeries()).extracting("totalSubjects").containsExactly(1);
        softly.assertThat(arm1chart.get(1).getSeries()).extracting("category").containsOnly("pt1");

        List<? extends OutputBarChartData> arm2chart = result.stream().filter(c -> c.getTrellisedBy().get(0)
                .getTrellisOption().equals("arm2")).findFirst().get().getData();
        softly.assertThat(arm2chart).hasSize(2);
        softly.assertThat(arm2chart.get(0).getName()).isEqualTo("CTC Grade 1");
        softly.assertThat(arm2chart.get(0).getCategories()).containsExactlyInAnyOrder("pt1", "pt2");
        softly.assertThat(arm2chart.get(0).getSeries()).extracting("value").containsOnly(1.0);
        softly.assertThat(arm2chart.get(0).getSeries()).extracting("category").containsOnly("pt1");
        softly.assertThat(arm2chart.get(1).getName()).isEqualTo("CTC Grade 2");
        softly.assertThat(arm2chart.get(1).getCategories()).containsExactlyInAnyOrder("pt1", "pt2");
        softly.assertThat(arm2chart.get(1).getSeries()).extracting("value").containsOnly(1.0);
        softly.assertThat(arm2chart.get(1).getSeries()).extracting("category").containsOnly("pt2");
    }

    @Test
    public void shouldGetBarChartSelection() throws Exception {
        List<Ae> aes = getBarchartTestAes();

        when(aeIncidenceDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(aes);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(
                aes.stream().map(SubjectAwareWrapper::getSubject).collect(Collectors.toList()));


        final ChartGroupByOptions.ChartGroupBySettingsBuilder<Ae, AeGroupByOptions> settings = ChartGroupByOptions.builder();
        settings.withOption(COLOR_BY, AeGroupByOptions.MAX_SEVERITY_GRADE.getGroupByOptionAndParams());
        settings.withOption(X_AXIS, AeGroupByOptions.PT.getGroupByOptionAndParams());
        final ChartGroupByOptions.GroupByOptionAndParams<Ae, AeGroupByOptions> trellis = ARM.getGroupByOptionAndParams();
        settings.withTrellisOptions(new HashSet<>(Collections.singletonList(trellis)));

        Map<AeGroupByOptions, Object> selectedTrelises = new HashMap<>();
        selectedTrelises.put(ARM, "arm1");
        Map<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItems = new HashMap<>();
        selectedItems.put(X_AXIS, "pt1");
        selectedItems.put(COLOR_BY, "CTC Grade 2");

        //When
        final SelectionDetail selectionDetails = aeService.getSelectionDetails(DATASETS, AeFilters.empty(), PopulationFilters.empty(),
                ChartSelection.of(settings.build(), Collections.singleton(ChartSelectionItem.of(selectedTrelises, selectedItems))),
                CountType.COUNT_OF_SUBJECTS);

        //Then
        softly.assertThat(selectionDetails.getEventIds()).containsExactlyInAnyOrder("id1", "id2", "id7");
        softly.assertThat(selectionDetails.getSubjectIds()).containsExactly("01");
        //When
        final SelectionDetail selectionDetails2 = aeService.getSelectionDetails(DATASETS, AeFilters.empty(), PopulationFilters.empty(),
                ChartSelection.of(settings.build(), Collections.singleton(ChartSelectionItem.of(selectedTrelises, selectedItems))),
                CountType.COUNT_OF_EVENTS);

        //Then
        softly.assertThat(selectionDetails2.getEventIds()).containsExactlyInAnyOrder("id2");
        softly.assertThat(selectionDetails2.getSubjectIds()).containsExactly("01");
    }

    List<Ae> getBarchartTestAes() {
        Subject subject1 = Subject.builder().actualArm("arm1").subjectCode("E01").datasetId("Study1").subjectId("01").build();
        Subject subject2 = Subject.builder().actualArm("arm1").subjectCode("E02").datasetId("Study1").subjectId("02").build();
        Subject subject3 = Subject.builder().actualArm("arm2").subjectCode("E03").datasetId("Study1").subjectId("03").build();
        return Arrays.asList(
                new Ae(AeRaw.builder().id("id1").pt("pt1").aeSeverities(
                        newArrayList(
                                AeSeverityRaw.builder().severity(SEVERITY_1).startDate(toDate("11.08.2015")).endDate(toDate("12.08.2015")).build()
                        )
                ).build(), subject1),
                new Ae(AeRaw.builder().id("id2").pt("pt1").aeSeverities(
                        newArrayList(
                                AeSeverityRaw.builder().severity(SEVERITY_2).startDate(toDate("11.08.2015")).endDate(toDate("12.08.2015")).build(),
                                AeSeverityRaw.builder().severity(SEVERITY_1).startDate(toDate("12.08.2015")).endDate(toDate("15.08.2015")).build()
                        )
                ).build(), subject1),
                new Ae(AeRaw.builder().id("id3").pt("pt2").aeSeverities(
                        newArrayList(
                                AeSeverityRaw.builder().severity(SEVERITY_1).startDate(toDate("11.08.2015")).endDate(toDate("12.08.2015")).build()
                        )
                ).build(), subject2),
                new Ae(AeRaw.builder().id("id4").pt("pt1").aeSeverities(
                        newArrayList(
                                AeSeverityRaw.builder().severity(SEVERITY_1).startDate(toDate("11.08.2015")).endDate(toDate("12.08.2015")).build()
                        )
                ).build(), subject3),
                new Ae(AeRaw.builder().id("id5").pt("pt2").aeSeverities(
                        newArrayList(
                                AeSeverityRaw.builder().severity(SEVERITY_2).startDate(toDate("11.08.2015")).endDate(toDate("12.08.2015")).build()
                        )
                ).build(), subject3),
                new Ae(AeRaw.builder().id("id6").pt("pt1").aeSeverities(
                        newArrayList(
                                AeSeverityRaw.builder().severity(SEVERITY_1).startDate(toDate("11.08.2015")).endDate(toDate("12.08.2015")).build()
                        )
                ).build(), subject2),
                new Ae(AeRaw.builder().id("id7").pt("pt1").aeSeverities(
                        newArrayList(
                                AeSeverityRaw.builder().severity(SEVERITY_1).startDate(toDate("10.08.2015")).endDate(toDate("10.08.2015")).build()
                        )
                ).build(), subject1),
                new Ae(AeRaw.builder().id("id8").pt("pt2").build(), subject1)
        );
    }


    private List<Ae> getAeForAcuity() {
        Map<String, String> actionTaken1 = ImmutableMap.<String, String>builder().put("DRUG001", "Dose Reduced").put("additional_drug", "None").build();
        AeSeverity severity = AeSeverity.builder().severityNum(1).webappSeverity("CTC Grade 1").build();
        Subject subject = Subject.builder().subjectId("sid1").clinicalStudyCode("code").subjectCode("E01").datasetId("test").build();
        AeRaw rawevent = AeRaw.builder().id("idacuity").
                pt("ptr1").
                soc("socr1").
                hlt("hltr1").
                comment("comment").
                calcDurationIfNull(true).
                aeSeverities(
                        newArrayList(
                                AeSeverityRaw.builder().drugsActionTaken(actionTaken1).severity(severity).
                                        startDate(toDate("01.08.2015")).endDate(toDate("03.08.2015")).build()
                        )
                )
                .aeNumber(1)
                .subjectId("sid1").build();
        return newArrayList(new Ae(rawevent, subject));
    }

    @Test
    public void shouldGetDoDColumnsForAesForAcuity() throws Exception {

        final List<Ae> aes = getAeForAcuity();

        final Map<String, String> doDColumns = doDCommonService.getDoDColumns(Column.DatasetType.ACUITY, aes);

        softly.assertThat(doDColumns).hasSize(11);

        softly.assertThat(doDColumns.keySet()).containsOnly(
                "studyId",
                "subjectId",
                "preferredTerm",
                "highLevelTerm",
                "systemOrganClass",
                "maxSeverity",
                "startDate",
                "endDate",
                "duration",
                "actionTaken",
                "comment");
        softly.assertThat(doDColumns.values()).containsOnly(
                "Study id",
                "Subject id",
                "Preferred term",
                "High level term",
                "System organ class",
                "Max severity",
                "Start date",
                "End date",
                "Duration",
                "Action taken",
                "Comment");
    }

    @Test
    public void shouldGetDoDForAesForAcuity() throws Exception {

        List<Ae> aes = getAeForAcuity();
        when(aeIncidenceDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(aes);

        Set<String> aesIds = aes.stream().map(Ae::getId).collect(toSet());

        List<Map<String, String>> doDData = aeService.getDetailsOnDemandData(DUMMY_ACUITY_DATASETS, aesIds,
                Collections.emptyList(), 0, Integer.MAX_VALUE);

        softly.assertThat(doDData).hasSize(aes.size());

        Ae ae = aes.get(0);
        Map<String, String> dod = doDData.get(0);

        softly.assertThat(dod.size()).isEqualTo(31);
        softly.assertThat(ae.getSubject().getSubjectCode()).isEqualTo(dod.get("subjectId"));
        softly.assertThat(ae.getSubject().getClinicalStudyCode()).isEqualTo(dod.get("studyId"));
        softly.assertThat(ae.getEvent().getPt()).isEqualTo(dod.get("preferredTerm"));
        softly.assertThat(ae.getEvent().getHlt()).isEqualTo(dod.get("highLevelTerm"));
        softly.assertThat(ae.getEvent().getMaxAeSeverity()).isEqualTo(dod.get("maxSeverity"));
        softly.assertThat(ae.getActionTakenAsString()).isEqualTo(dod.get("actionTaken"));
        softly.assertThat(ae.getCausalityAsString()).isEqualTo(dod.get("causality"));
    }

    private List<Ae> getAeForDetect() {
        AeSeverity severity = AeSeverity.builder().severityNum(1).webappSeverity("CTC Grade 1").build();
        Subject subject = Subject.builder().subjectId("sid1").clinicalStudyCode("code").subjectCode("E01").datasetId("test").build();
        AeRaw rawevent = AeRaw.builder().id("iddetect").
                pt("pt1").
                soc("soc1").
                hlt("hlt1").
                actionTaken("ACTION_TAKEN").
                aeSeverities(
                        newArrayList(
                                AeSeverityRaw.builder().severity(severity).startDate(toDate("01.08.2015")).endDate(toDate("03.08.2015")).build()
                        )
                )
                .aeNumber(1)
                .calcDurationIfNull(true)
                .subjectId("sid1").build();
        return newArrayList(new Ae(rawevent, subject));
    }

    @Test
    public void shouldGetDoDForAesForDetect() throws Exception {

        List<Ae> aes = getAeForDetect();
        when(aeIncidenceDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(aes);

        Set<String> aesIds = aes.stream().map(Ae::getId).collect(toSet());

        List<Map<String, String>> doDData = aeService.getDetailsOnDemandData(DUMMY_DETECT_DATASETS, aesIds,
                Collections.emptyList(), 0, Integer.MAX_VALUE);

        softly.assertThat(doDData).hasSize(aes.size());

        Ae ae = aes.get(0);
        Map<String, String> dod = doDData.get(0);

        softly.assertThat(dod.size()).isEqualTo(24);
        softly.assertThat(ae.getSubject().getSubjectCode()).isEqualTo(dod.get("subjectId"));
        softly.assertThat(ae.getSubject().getClinicalStudyCode()).isEqualTo(dod.get("studyId"));
        softly.assertThat(ae.getEvent().getPt()).isEqualTo(dod.get("preferredTerm"));
        softly.assertThat(ae.getEvent().getHlt()).isEqualTo(dod.get("highLevelTerm"));
        softly.assertThat(ae.getEvent().getMaxAeSeverity()).isEqualTo(dod.get("maxSeverity"));
        softly.assertThat(ae.getEvent().getActionTaken()).isEqualTo(dod.get("actionTaken"));
        softly.assertThat(ae.getEvent().getCausality()).isEqualTo(dod.get("causality"));
    }

    @Test
    public void testGetSingleSubjectData() {
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singleton(SUBJECT1_ARM1));
        when(aeIncidenceDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(AE_EVENTS);

        List<Map<String, String>> singleSubjectData = aeService.getSingleSubjectData(DATASETS, "sid1");

        softly.assertThat(singleSubjectData).hasSize(5);
        softly.assertThat(singleSubjectData).flatExtracting(Map::keySet).containsOnly(
                "preferredTerm", "hlt", "soc", "severity", "maxSeverity",
                "startDate", "endDate", "duration", "serious", "causality", "description", "immuneMediated"
        );
        softly.assertThat(singleSubjectData)
                .extracting(e -> e.get("preferredTerm"), e -> e.get("hlt"), e -> e.get("soc"), e -> e.get("severity"),
                        e -> e.get("maxSeverity"), e -> e.get("startDate"), e -> e.get("endDate"), e -> e.get("duration"),
                        e -> e.get("serious"), e -> e.get("causality"), e -> e.get("description"), e -> e.get("immuneMediated"))
                .contains(
                        Tuple.tuple("pt1", "hlt1", "soc1", "CTC Grade 1", "CTC Grade 2", "2015-08-01T03:00:00",
                                "2015-08-03T03:00:00", "3", "", "drug1: Yes", "text1", ""),
                        Tuple.tuple("pt1", "hlt1", "soc1", "CTC Grade 2", "CTC Grade 2", "2015-08-04T03:00:00",
                                "2015-08-05T03:00:00", "2", "", "drug1: Yes", "text1", ""),
                        Tuple.tuple("pt2", "hlt2", "soc2", "CTC Grade 1", "CTC Grade 1", "2015-08-03T03:00:00",
                                "2015-08-04T03:00:00", "2", "Yes", "", "text2", "I2"),
                        Tuple.tuple("pt3", "hlt3", "soc3", "CTC Grade 1", "CTC Grade 2", "2015-08-04T03:00:00",
                                "2015-08-05T03:00:00", "2", "", "", "text3", ""),
                        Tuple.tuple("pt3", "hlt3", "soc3", "CTC Grade 2", "CTC Grade 2", "2015-08-06T03:00:00",
                                "2015-08-07T03:00:00", "2", "", "", "text3", "")
                );
    }

    @Test
    public void shouldGetSingleSubjectDataWithEmptyAeSeverityDate() {
        Ae ae1 = new Ae(AeRaw.builder().id("id1").pt("pt1").hlt("hlt1").soc("soc1").serious("1").usedInTfl(TRUE).
                aeSeverities(
                        newArrayList(
                                AeSeverityRaw.builder().severity(SEVERITY_1).endDate(toDateTime("05.08.2015 00:00:00")).build(),
                                AeSeverityRaw.builder().severity(SEVERITY_2).startDate(toDateTime("06.08.2015 00:00:00")).build()
                        )
                )
                .build(),
                SUBJECT1_ARM1
        );
        Ae ae2 = new Ae(AeRaw.builder().id("id2").pt("pt2").hlt("hlt2").soc("soc2").serious("1").usedInTfl(TRUE).
                aeSeverities(
                        newArrayList(
                                AeSeverityRaw.builder().severity(SEVERITY_3).build()
                        )
                )
                .build(),
                SUBJECT1_ARM1
        );


        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singleton(SUBJECT1_ARM1));
        when(aeIncidenceDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(newArrayList(ae1, ae2));

        List<Map<String, String>> singleSubjectData = aeService.getSingleSubjectData(DATASETS, "sid1");

        softly.assertThat(singleSubjectData).hasSize(3);
        softly.assertThat(singleSubjectData).flatExtracting(Map::keySet).containsOnly(
                "preferredTerm", "hlt", "soc", "severity", "maxSeverity",
                "startDate", "endDate", "duration", "serious", "causality", "description", "immuneMediated"
        );
        softly.assertThat(singleSubjectData)
                .extracting(e -> e.get("preferredTerm"), e -> e.get("hlt"), e -> e.get("soc"), e -> e.get("severity"),
                        e -> e.get("maxSeverity"), e -> e.get("startDate"), e -> e.get("endDate"), e -> e.get("duration"),
                        e -> e.get("serious"), e -> e.get("causality"), e -> e.get("description"), e -> e.get("immuneMediated"))
                .contains(
                        Tuple.tuple("pt1", "hlt1", "soc1", "CTC Grade 2", "CTC Grade 2", "2015-08-06T00:00:00", "", "", "1", "", "", ""),
                        Tuple.tuple("pt1", "hlt1", "soc1", "CTC Grade 1", "CTC Grade 2", "", "2015-08-05T00:00:00", "", "1", "", "", ""),
                        Tuple.tuple("pt2", "hlt2", "soc2", "CTC Grade 3", "CTC Grade 3", "", "", "", "1", "", "", "")
                );
    }

    @Test
    public void shouldGetSingleSubjectDataWithEmptyAeSeverity() {
        Ae ae1 = new Ae(AeRaw.builder().id("id1").pt("pt1").hlt("hlt1").soc("soc1").serious("1").usedInTfl(TRUE).drugsCausality(DRUG_CAUSALITY).text("ae1")
                .aeSeverities(
                        newArrayList(
                                AeSeverityRaw.builder().startDate(toDateTime("01.08.2015 00:00:00")).endDate(toDateTime("05.08.2015 00:00:00")).build()
                        )
                )
                .build(),
                SUBJECT1_ARM1
        );

        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singleton(SUBJECT1_ARM1));
        when(aeIncidenceDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(newArrayList(ae1));

        List<Map<String, String>> singleSubjectData = aeService.getSingleSubjectData(DATASETS, "sid1");

        softly.assertThat(singleSubjectData).hasSize(1);
        softly.assertThat(singleSubjectData).flatExtracting(Map::keySet).containsOnly(
                "preferredTerm", "hlt", "soc", "severity", "maxSeverity",
                "startDate", "endDate", "duration", "serious", "causality", "description", "immuneMediated"
        );
        softly.assertThat(singleSubjectData)
                .extracting(e -> e.get("preferredTerm"), e -> e.get("hlt"), e -> e.get("soc"), e -> e.get("severity"),
                        e -> e.get("maxSeverity"), e -> e.get("startDate"), e -> e.get("endDate"), e -> e.get("duration"),
                        e -> e.get("serious"), e -> e.get("causality"), e -> e.get("description"), e -> e.get("immuneMediated"))
                .contains(
                        Tuple.tuple("pt1", "hlt1", "soc1", "", "", "2015-08-01T00:00:00", "2015-08-05T00:00:00", "5", "1", "drug1: Yes", "ae1", "")
                );
    }
}
