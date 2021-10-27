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
import com.acuity.visualisations.rawdatamodel.dataproviders.ConmedDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.PopulationDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.filters.ConmedFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.service.dod.DoDCommonService;
import com.acuity.visualisations.rawdatamodel.test.annotation.SpringITTest;
import com.acuity.visualisations.rawdatamodel.trellis.TrellisOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItem;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ConmedGroupByOptions;
import com.acuity.visualisations.rawdatamodel.util.Column;
import com.acuity.visualisations.rawdatamodel.util.DateUtils;
import com.acuity.visualisations.rawdatamodel.vo.ConmedRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Conmed;
import com.acuity.va.security.acl.domain.Datasets;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_DATASETS;
import static com.acuity.visualisations.rawdatamodel.Constants.DATASETS;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.COLOR_BY;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.X_AXIS;
import static com.acuity.visualisations.rawdatamodel.util.Attributes.DEFAULT_EMPTY_VALUE;
import static com.acuity.visualisations.rawdatamodel.util.DaysUtil.toDate;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.toSet;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringITTest
public class ConmedsServiceTest {
    @Autowired
    private ConmedsService conmedService;
    @MockBean
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;
    @MockBean
    private ConmedDatasetsDataProvider conmedDatasetsDataProvider;

    private DoDCommonService doDCommonService = new DoDCommonService();

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    private final Map<String, Date> drugFirstDoseDate1 = ImmutableMap.<String, Date>builder().
            put("drug1", DateUtils.toDate("01.08.2015")).
            put("drug2", DateUtils.toDate("01.10.2015")).build();

    private static Subject subject1 = Subject.builder()
            .subjectId("sid1")
            .clinicalStudyCode("STUDYID001")
            .studyPart("A")
            .subjectCode("E01")
            .actualArm("Placebo")
            .drugFirstDoseDate("Placebo", DateUtils.toDate("01.08.2015"))
            .firstTreatmentDate(toDate("2014-12-02"))
            .dateOfRandomisation(toDate("2015-01-02"))
            .build();

    private static Subject subject2 = Subject.builder()
            .subjectId("sid2")
            .actualArm("MedX")
            .drugFirstDoseDate("MedX", DateUtils.toDate("01.08.2015"))
            .firstTreatmentDate(toDate("2014-12-05"))
            .dateOfRandomisation(toDate("2015-01-05"))
            .build();

    private static Subject subject3 = Subject.builder()
            .subjectId("sid3")
            .clinicalStudyCode("STUDYID001")
            .subjectCode("E03")
            .actualArm("Placebo")
            .drugFirstDoseDate("Placebo", DateUtils.toDate("01.08.2015"))
            .firstTreatmentDate(toDate("2014-12-02"))
            .dateOfRandomisation(toDate("2015-01-02"))
            .build();

    private static Conmed conmed1 = new Conmed(ConmedRaw.builder()
            .id("c1")
            .medicationName("drug1")
            .dose(4d)
            .startDate(toDate("2014-12-03 00:00"))
            .endDate(toDate("2015-03-01 23:59"))
            .treatmentReason("reason1")
            .doseUnits("mg")
            .doseFrequency("1")
            .atcCode("J05AB")
            .build(), subject1);

    private static Conmed conmed2 = new Conmed(ConmedRaw.builder()
            .id("c2")
            .medicationName("drug2")
            .dose(5d)
            .startDate(toDate("2015-02-03 00:08"))
            .treatmentReason("reason1")
            .doseUnits("mg")
            .doseFrequency("1")
            .atcCode("F05BC")
            .build(), subject1);

    private static Conmed conmed3 = new Conmed(ConmedRaw.builder()
            .id("c3")
            .medicationName("drug2")
            .dose(5d)
            .startDate(toDate("2015-02-03 00:08"))
            .treatmentReason("reason1")
            .doseUnits("mg")
            .doseFrequency("1")
            .atcCode("K05BC")
            .build(), subject2);

    private static Conmed conmed4 = new Conmed(ConmedRaw.builder()
            .id("c3")
            .build(), subject1);

    private static Conmed conmed5 = new Conmed(ConmedRaw.builder()
            .id("c1")
            .medicationName("drug1")
            .dose(4d)
            .startDate(toDate("2014-12-03 00:00"))
            .endDate(toDate("2015-03-01 13:59"))
            .treatmentReason("reason1")
            .doseUnits("mg")
            .doseFrequency("1")
            .atcCode("J05AB")
            .build(), subject1);

    private static Conmed conmed6 = new Conmed(ConmedRaw.builder()
            .id("c1")
            .medicationName("drug1")
            .dose(4d)
            .startDate(toDate("2014-12-03 00:00"))
            .endDate(toDate("2015-03-01 13:59"))
            .treatmentReason("reason1")
            .doseTotal(100d)
            .doseUnits("mg")
            .doseFrequency("1")
            .atcCode("J05AB")
            .build(), subject3);

    private static Conmed conmed7 = new Conmed(ConmedRaw.builder()
            .id("c3")
            .build(), subject3);

    private static Conmed conmed8 = new Conmed(ConmedRaw.builder()
            .id("c1")
            .medicationName("drug1")
            .dose(4d)
            .doseUnits("mg")
            .doseTotal(40d)
            .doseUnitsOther("g")
            .startDate(toDate("2014-12-03 00:00"))
            .endDate(toDate("2015-03-01 13:59"))
            .treatmentReason("reason1")
            .route("month")
            .reasonForTreatmentStop("death")
            .reasonForTreatmentStopOther("other death")
            .therapyReasonOther("other reason")
            .therapyReason("therapy reason")
            .otherProphylaxisSpec("prophylaxis")
            .activeIngredient1("ingredient")
            .aeNum(3)
            .aePt("pt")
            .doseUnits("mg")
            .doseFrequency("1")
            .frequencyOther("3")
            .atcCode("J05AB")
            .atcText("atc_text")
            .infectionBodySystem("infection")
            .infectionBodySystemOther("other infection")
            .build(), subject1);

    static final List<Conmed> CONMEDS = ImmutableList.of(conmed1, conmed2);

    @Test
    public void testGetSingleSubjectData() {
        when(populationDatasetsDataProvider.loadData(any(Datasets.class)))
                .thenReturn(Collections.singleton(subject1));
        when(conmedDatasetsDataProvider.loadData(any(Datasets.class)))
                .thenReturn(Arrays.asList(conmed1, conmed2));

        List<Map<String, String>> singleSubjectData = conmedService.getSingleSubjectData(DATASETS, "sid1", ConmedFilters.empty());

        Map<String, String> outputRow1 = new HashMap<>();
        outputRow1.put("medicationName", "drug1");
        outputRow1.put("startDate", "2014-12-03T00:00:00");
        outputRow1.put("endDate", "2015-03-01T23:59:00");
        outputRow1.put("treatmentReason", "reason1");
        outputRow1.put("getDoseWithUnits", "4.0 mg");
        outputRow1.put("duration", "89");
        outputRow1.put("doseFrequency", "1");
        outputRow1.put("atcCode", "J05AB");
        outputRow1.put("studyDayAtConmedStart", "1");
        outputRow1.put("getStudyDayAtConmedEnd", "89");
        outputRow1.put("getConmedStartPriorToRandomisation", "Yes");
        outputRow1.put("getConmedEndPriorToRandomisation", "No");
        outputRow1.put("conmedTreatmentOngoing", "No");

        Map<String, String> outputRow2 = new HashMap<>();
        outputRow2.put("medicationName", "drug2");
        outputRow2.put("startDate", "2015-02-03T00:08:00");
        outputRow2.put("treatmentReason", "reason1");
        outputRow2.put("getDoseWithUnits", "5.0 mg");
        outputRow2.put("doseFrequency", "1");
        outputRow2.put("atcCode", "F05BC");
        outputRow2.put("studyDayAtConmedStart", "63");
        outputRow2.put("getConmedStartPriorToRandomisation", "No");
        outputRow2.put("conmedTreatmentOngoing", "Yes");

        softly.assertThat(singleSubjectData).hasSize(2);
        softly.assertThat(singleSubjectData.get(0).entrySet()).containsAll(outputRow1.entrySet());
        softly.assertThat(singleSubjectData.get(1).entrySet()).containsAll(outputRow2.entrySet());
    }

    @Test
    public void getAvailableBarChartXAxis() {
        when(populationDatasetsDataProvider.loadData(any(Datasets.class)))
                .thenReturn(Arrays.asList(subject1, subject2));
        when(conmedDatasetsDataProvider.loadData(any(Datasets.class)))
                .thenReturn(Arrays.asList(conmed1, conmed2, conmed3));

        AxisOptions<ConmedGroupByOptions> result = conmedService.getAvailableBarChartXAxis(
                DATASETS, ConmedFilters.empty(), PopulationFilters.empty()
        );

        softly.assertThat(result.getOptions()).hasSize(2);
        softly.assertThat(result.getOptions().get(0).getGroupByOption().name()).isEqualTo("MEDICATION_NAME");
        softly.assertThat(result.getOptions().get(1).getGroupByOption().name()).isEqualTo("ATC_CODE");

        softly.assertThat(result.getDrugs()).containsOnly("Placebo", "MedX");
    }

    @Test
    public void getBarChartColoring() {
        when(populationDatasetsDataProvider.loadData(any(Datasets.class)))
                .thenReturn(Arrays.asList(subject1, subject2));
        when(conmedDatasetsDataProvider.loadData(any(Datasets.class)))
                .thenReturn(Arrays.asList(conmed1, conmed2, conmed3));

        List<TrellisOptions<ConmedGroupByOptions>> result = conmedService.getBarChartColorByOptions(
                DATASETS, ConmedFilters.empty(), PopulationFilters.empty()
        );

        softly.assertThat(result).hasSize(6);

        softly.assertThat(result.get(0).getTrellisedBy().name()).isEqualTo("ANATOMICAL_GROUP");
        softly.assertThat((List) result.get(0).getTrellisOptions()).containsOnly("J", "F", "K");

        softly.assertThat(result.get(1).getTrellisedBy().name()).isEqualTo("DOSE");
        softly.assertThat((List) result.get(1).getTrellisOptions()).containsOnly("4-4", "5-5");

        softly.assertThat(result.get(2).getTrellisedBy().name()).isEqualTo("DOSE_UNITS");
        softly.assertThat((List) result.get(2).getTrellisOptions()).containsOnly("mg");

        softly.assertThat(result.get(3).getTrellisedBy().name()).isEqualTo("ONGOING");
        softly.assertThat((List) result.get(3).getTrellisOptions()).containsOnly("No", "Yes");

        softly.assertThat(result.get(4).getTrellisedBy().name()).isEqualTo("CONMED_STARTED_PRIOR_TO_STUDY");
        softly.assertThat((List) result.get(4).getTrellisOptions()).containsOnly("No");

        softly.assertThat(result.get(5).getTrellisedBy().name()).isEqualTo("CONMED_ENDED_PRIOR_TO_STUDY");
        softly.assertThat((List) result.get(5).getTrellisOptions()).containsOnly("No", DEFAULT_EMPTY_VALUE);
    }

    @Test
    public void getBarChartTrellising() {
        when(populationDatasetsDataProvider.loadData(any(Datasets.class)))
                .thenReturn(Arrays.asList(subject1, subject2));
        when(conmedDatasetsDataProvider.loadData(any(Datasets.class)))
                .thenReturn(Arrays.asList(conmed1, conmed2, conmed3));

        List<TrellisOptions<ConmedGroupByOptions>> result = conmedService.getTrellisOptions(
                DATASETS, ConmedFilters.empty(), PopulationFilters.empty()
        );

        softly.assertThat(result).hasSize(1);
        softly.assertThat(result.get(0).getTrellisedBy().name()).isEqualTo("ARM");
        softly.assertThat((List) result.get(0).getTrellisOptions()).containsOnly("Placebo", "MedX");
    }

    @Test
    public void shouldGetAllAcuityDetailsOnDemandColumnsInCorrectOrder() {
        // Given
        List<Conmed> conmeds = Arrays.asList(conmed1, conmed2, conmed8);

        // When
        Map<String, String> columns = doDCommonService.getDoDColumns(Column.DatasetType.ACUITY, conmeds);

        // Then
        softly.assertThat(columns.keySet())
                .containsExactlyInAnyOrder("studyId", "studyPart", "subjectId", "aePt", "aeNum",
                        "medicationName",
                        "atcCode",
                        "dose",
                        "doseUnits",
                        "doseFrequency",
                        "startDate",
                        "endDate",
                        "duration",
                        "conmedTreatmentOngoing",
                        "studyDayAtConmedStart",
                        "studyDayAtConmedEnd",
                        "startPriorToRandomisation",
                        "endPriorToRandomisation",
                        "treatmentReason");
        softly.assertThat(columns.values())
                .containsExactlyInAnyOrder("Study id", "Study part", "Subject id",
                        "Medication Name",
                        "Atc Code",
                        "Dose",
                        "Dose Units",
                        "Dose frequency",
                        "Start Date",
                        "End Date",
                        "Duration",
                        "Conmed Treatment Ongoing",
                        "Study Day At Conmed Start",
                        "Study Day At Conmed End",
                        "Conmed Start Prior Randomisation",
                        "Conmed End Prior Randomisation",
                        "AE PT",
                        "AE Number",
                        "Treatment reason");
    }

    @Test
    public void shouldGetAcuityDetailsOnDemandColumnsInCorrectOrder() {
        // Given
        List<Conmed> conmeds = Arrays.asList(conmed4);

        // When
        Map<String, String> columns = doDCommonService.getDoDColumns(Column.DatasetType.ACUITY, conmeds);

        // Then
        softly.assertThat(columns.keySet()).containsExactly("studyId", "studyPart", "subjectId", "conmedTreatmentOngoing");
        softly.assertThat(columns.values()).containsExactly("Study id", "Study part", "Subject id", "Conmed Treatment Ongoing");
    }

    @Test
    public void shouldGetAcuityDetailsOnDemand() {
        // Given
        List<Conmed> conmeds = Arrays.asList(conmed8);
        Set<String> conmedIds = conmeds.stream().map(Conmed::getId).collect(toSet());
        when(conmedDatasetsDataProvider.loadData(any())).thenReturn(conmeds);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Arrays.asList(subject1));

        // When
        List<Map<String, String>> doDData =
                conmedService.getDetailsOnDemandData(DUMMY_ACUITY_DATASETS, conmedIds, Collections.emptyList(), 0, Integer.MAX_VALUE);

        // Then
        Conmed conmed = conmeds.get(0);
        Map<String, String> dod = doDData.get(0);

        softly.assertThat(doDData).hasSize(conmeds.size());
        softly.assertThat(dod.size()).isEqualTo(20);
        softly.assertThat(conmed.getSubjectCode()).isEqualTo(dod.get("subjectId"));
        softly.assertThat(conmed.getStudyPart()).isEqualTo(dod.get("studyPart"));
        softly.assertThat(conmed.getStudyId()).isEqualTo(dod.get("studyId"));
        softly.assertThat(conmed.getEvent().getStartDate()).isInSameDayAs(dod.get("startDate"));
        softly.assertThat(conmed.getEvent().getEndDate()).isInSameDayAs(dod.get("endDate"));
        softly.assertThat(conmed.getEvent().getMedicationName()).isEqualTo(dod.get("medicationName"));
        softly.assertThat(conmed.getEvent().getTreatmentReason()).isEqualTo(dod.get("treatmentReason"));
        softly.assertThat(conmed.getDuration()).isEqualTo(Integer.valueOf(dod.get("duration")));
        softly.assertThat(conmed.getConmedStartPriorToRandomisation()).isEqualTo(dod.get("startPriorToRandomisation"));
        softly.assertThat(conmed.getConmedEndPriorToRandomisation()).isEqualTo(dod.get("endPriorToRandomisation"));
        softly.assertThat(conmed.getStudyDayAtConmedStart()).isEqualTo(Integer.valueOf(dod.get("studyDayAtConmedStart")));
        softly.assertThat(conmed.getStudyDayAtConmedEnd()).isEqualTo(Integer.valueOf(dod.get("studyDayAtConmedEnd")));
        softly.assertThat(conmed.getEvent().getDoseFrequency()).isEqualTo(dod.get("doseFrequency"));
        softly.assertThat(conmed.getEvent().getDose().longValue()).isEqualTo(Long.valueOf(dod.get("dose")));
        softly.assertThat(conmed.getEvent().getDoseUnits()).isEqualTo(dod.get("doseUnits"));
        softly.assertThat(conmed.getConmedTreatmentOngoing()).isEqualTo(dod.get("conmedTreatmentOngoing"));
        softly.assertThat(conmed.getEvent().getId()).isEqualTo(dod.get("eventId"));
        softly.assertThat(conmed.getEvent().getAtcCode()).isEqualTo(dod.get("atcCode"));
        softly.assertThat(conmed.getEvent().getAePt()).isEqualTo(dod.get("aePt"));
        softly.assertThat(conmed.getEvent().getAeNum()).isEqualTo(Integer.valueOf(dod.get(("aeNum"))));
    }

    @Test
    public void shouldGetSelection() {
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(newArrayList(subject1));
        when(conmedDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(newArrayList(conmed1, conmed2));

        ChartGroupByOptions<Conmed, ConmedGroupByOptions> settings =
                ChartGroupByOptions.<Conmed, ConmedGroupByOptions>builder()
                        .withOption(X_AXIS,
                                ConmedGroupByOptions.MEDICATION_NAME.getGroupByOptionAndParams())
                        .withOption(COLOR_BY, ConmedGroupByOptions.DOSE.getGroupByOptionAndParams())
                        .build();

        Map<ConmedGroupByOptions, Object> selectedTrellises = new HashMap<>();
        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItems = new HashMap<>();
        selectedItems.put(X_AXIS, "drug2");
        selectedItems.put(COLOR_BY, "5-5");

        List<ChartSelectionItem<Conmed, ConmedGroupByOptions>> selectionItems = Collections.singletonList(
                ChartSelectionItem.of(selectedTrellises, selectedItems)
        );

        SelectionDetail selectionResult = conmedService.getSelectionDetails(
                DUMMY_ACUITY_DATASETS,
                ConmedFilters.empty(),
                PopulationFilters.empty(),
                ChartSelection.of(settings, selectionItems)
        );

        softly.assertThat(selectionResult.getEventIds()).hasSize(1);
        softly.assertThat(selectionResult.getSubjectIds()).hasSize(1);
        softly.assertThat(selectionResult.getTotalEvents()).isEqualTo(2);
        softly.assertThat(selectionResult.getTotalSubjects()).isEqualTo(1);
    }
}
