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

package com.acuity.visualisations.rawdatamodel.service.filters;

import com.acuity.visualisations.rawdatamodel.dataproviders.AeIncidenceDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.PopulationDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.filters.AeFilters;
import com.acuity.visualisations.rawdatamodel.filters.DateRangeFilter;
import com.acuity.visualisations.rawdatamodel.filters.MapFilter;
import com.acuity.visualisations.rawdatamodel.filters.MultiValueSetFilter;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.filters.RangeFilter;
import com.acuity.visualisations.rawdatamodel.filters.SetFilter;
import com.acuity.visualisations.rawdatamodel.test.TestConfig;
import com.acuity.visualisations.rawdatamodel.util.DateUtils;
import com.acuity.visualisations.rawdatamodel.util.DaysUtil;
import com.acuity.visualisations.rawdatamodel.vo.AeRaw;
import com.acuity.visualisations.rawdatamodel.vo.AeSeverity;
import com.acuity.visualisations.rawdatamodel.vo.AeSeverityRaw;
import com.acuity.visualisations.rawdatamodel.vo.FilterQuery;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Ae;
import com.google.common.collect.ImmutableMap;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.acuity.visualisations.rawdatamodel.util.DateUtils.toDate;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfig.class)
public class AeFilterServiceTest {

    @Autowired
    private AeFilterService aeFilterService;
    @MockBean
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;
    //@MockBean(name = "aeIncidenceDatasetsDataProvider")
    //private AeIncidenceDatasetsDataProvider aeIncidenceDatasetsDataProvider;
    @MockBean(name = "eventDataProvider")
    private AeIncidenceDatasetsDataProvider aeIncidenceDatasetsDataProvider;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    private final AeSeverity SEVERITY_1 = AeSeverity.builder().severityNum(1).webappSeverity("CTC Grade 1").build();
    private final AeSeverity SEVERITY_2 = AeSeverity.builder().severityNum(2).webappSeverity("CTC Grade 2").build();
    private final AeSeverity SEVERITY_3 = AeSeverity.builder().severityNum(3).webappSeverity("CTC Grade 3").build();

    private Map<String, String> ACTION_TAKEN_1 = ImmutableMap.<String, String>builder().put("DRUG001", "Dose Reduced").put("additional_drug", "None").build();
    private Map<String, String> ACTION_TAKEN_2 = ImmutableMap.<String, String>builder().put("DRUG001", "None").put("additional_drug", "Dose Reduced").build();
    private Map<String, String> CAUSALITY_1 = ImmutableMap.<String, String>builder().put("DRUG001", "Yes").put("additional_drug", "No").build();
    private Map<String, String> CAUSALITY_2 = ImmutableMap.<String, String>builder().put("DRUG001", "No").put("additional_drug", "Yes").build();

    private final Map<String, Date> drugFirstDoseDate1 = ImmutableMap.<String, Date>builder().
            put("drug1", toDate("01.08.2015")).
            put("drug2", toDate("01.10.2015")).build();

    private Subject SUBJECT1 = Subject.builder().subjectId("sid1").subjectCode("E01").datasetId("test")
            .firstTreatmentDate(DateUtils.toDate("01.08.2015"))
            .dateOfRandomisation(DateUtils.toDate("02.08.2015"))
            .lastTreatmentDate(DateUtils.toDate("09.08.2016"))
            .drugFirstDoseDate(drugFirstDoseDate1).build();
    private Subject SUBJECT2 = Subject.builder().subjectId("sid2").subjectCode("E02").datasetId("test")
            .firstTreatmentDate(DateUtils.toDate("02.08.2015"))
            .dateOfRandomisation(DateUtils.toDate("21.07.2015"))
            .lastTreatmentDate(DateUtils.toDate("09.08.2015"))
            .drugFirstDoseDate(drugFirstDoseDate1).build();

    private Ae ae1 = new Ae(AeRaw.builder().id("1").aeNumber(1).serious("Yes").text("desc1").outcome("outcome1").requiredTreatment("Yes").
            causedSubjectWithdrawal("Yes").doseLimitingToxicity("Yes").timepoint("2").immuneMediated("Yes").infusionReaction("No").
            drugsCausality(CAUSALITY_1).actionTaken("Detect-AT1").causality("Detect-CA1").requiresHospitalisation("Yes").treatmentEmergent("No").
            usedInTfl(true).studyPeriod("SP1").
            specialInterestGroups(newArrayList("SIG1", "SIG2")).
            daysFromPrevDoseToStart(10).comment("comment1").suspectedEndpoint("Yes").suspectedEndpointCategory("SECat1").
            aeOfSpecialInterest("Yes").
            calcDurationIfNull(true).
            aeSeverities(
                    newArrayList(
                            AeSeverityRaw.builder().drugsActionTaken(ACTION_TAKEN_1).severity(SEVERITY_1).
                                    startDate(toDate("01.08.2015")).endDate(toDate("03.08.2015")).build(),
                            AeSeverityRaw.builder().drugsActionTaken(ACTION_TAKEN_1).severity(SEVERITY_2).
                                    startDate(toDate("04.08.2015")).endDate(toDate("05.08.2015")).build()
                    )
            ).
            subjectId("sid1").pt("pt1").hlt("hlt1").soc("soc1").build(), SUBJECT1);
    private Ae ae2 = new Ae(AeRaw.builder().id("2").aeNumber(2).serious("No").text("desc2").outcome("outcome2").requiredTreatment("No").
            causedSubjectWithdrawal("No").doseLimitingToxicity("No").timepoint("1").immuneMediated("No").infusionReaction("Yes").
            drugsCausality(CAUSALITY_2).actionTaken("Detect-AT2").causality("Detect-CA2").requiresHospitalisation("No").treatmentEmergent("Yes").
            usedInTfl(false).studyPeriod("SP2").
            specialInterestGroups(newArrayList("SIG1")).
            daysFromPrevDoseToStart(2).comment("comment2").suspectedEndpoint("No").suspectedEndpointCategory("SECat2").
            aeOfSpecialInterest("No").
            calcDurationIfNull(true).
            aeSeverities(
                    newArrayList(
                            AeSeverityRaw.builder().drugsActionTaken(ACTION_TAKEN_2).severity(SEVERITY_3).
                                    startDate(toDate("01.08.2015")).endDate(toDate("03.08.2015")).build()
                    )
            ).
            subjectId("sid2").pt("pt2").hlt("hlt2").soc("soc2").build(), SUBJECT2);
    private Ae ae3 = new Ae(AeRaw.builder().id("3").specialInterestGroups(newArrayList("SIG3")).
            aeNumber(3).subjectId("sid1").pt("pt3").hlt("hlt3").soc("soc3").build(), SUBJECT1);
    private Ae ae4 = new Ae(AeRaw.builder().id("4").
            aeNumber(4).subjectId("sid2").pt(null).hlt(null).soc(null).build(), SUBJECT2);
    private List<Ae> events = newArrayList(ae1, ae2, ae3, ae4);
    private List<Subject> subjects = events.stream().map(e -> e.getSubject()).collect(Collectors.toList());

    @Test
    public void shouldGetPts() {

        AeFilters result = givenFilterSetup(filters -> filters.setPt(new SetFilter(newArrayList("pt1"))));

        softly.assertThat(result.getPt().getValues()).containsOnly("pt1");
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldGetPtsIncludingEmpty() {
        AeFilters result = givenFilterSetup(filters -> filters.setPt(new SetFilter(newArrayList("pt1"), true)));

        softly.assertThat(result.getPt().getValues()).containsOnly("pt1", null);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(2);
    }

    @Test
    public void shouldGetHlt() {
        AeFilters result = givenFilterSetup(filters -> filters.setHlt(new SetFilter(newArrayList("hlt1"))));

        softly.assertThat(result.getHlt().getValues()).containsOnly("hlt1");
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldGetHltIncludingEmpty() {
        AeFilters result = givenFilterSetup(filters -> filters.setHlt(new SetFilter(newArrayList("hlt1"), true)));

        softly.assertThat(result.getHlt().getValues()).containsOnly("hlt1", null);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(2);
    }

    @Test
    public void shouldGetSoc() {
        AeFilters result = givenFilterSetup(filters -> filters.setSoc(new SetFilter(newArrayList("soc1"))));

        softly.assertThat(result.getSoc().getValues()).containsOnly("soc1");
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldGetSocIncludingEmpty() {
        AeFilters result = givenFilterSetup(filters -> filters.setSoc(new SetFilter(newArrayList("soc1"), true)));

        softly.assertThat(result.getSoc().getValues()).containsOnly("soc1", null);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(2);
    }

    @Test
    public void shouldGetDrugsActionTaken() {
        AeFilters result = givenFilterSetup(filters -> {

            MapFilter<String, MultiValueSetFilter<String>> actionTakenFilter = new MapFilter<>();
            Map<String, MultiValueSetFilter<String>> actionTakenMap = new HashMap<>();
            actionTakenMap.put("DRUG001", new MultiValueSetFilter<>(newArrayList("Dose Reduced"), false));
            actionTakenFilter.setMap(actionTakenMap);

            filters.setDrugsActionTaken(actionTakenFilter);
        });

        softly.assertThat(result.getDrugsActionTaken().getMap().get("DRUG001").getValues()).containsExactly("Dose Reduced");
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldGetDrugsActionTaken2() {
        AeFilters result = givenFilterSetup(filters -> {

            MapFilter<String, MultiValueSetFilter<String>> actionTakenFilter = new MapFilter<>();
            Map<String, MultiValueSetFilter<String>> actionTakenMap = new HashMap<>();
            actionTakenMap.put("DRUG001", new MultiValueSetFilter<>(newArrayList("Dose Reduced", "None"), false));
            actionTakenFilter.setMap(actionTakenMap);

            filters.setDrugsActionTaken(actionTakenFilter);
        });

        softly.assertThat(result.getDrugsActionTaken().getMap().get("DRUG001").getValues()).containsExactly("Dose Reduced", "None");
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(2);
    }

    @Test
    public void shouldGetAllDrugsActionTaken() {
        AeFilters result = givenFilterSetup(filters -> {
        });

        softly.assertThat(result.getDrugsActionTaken().getMap().get("DRUG001").getValues()).containsExactly("Dose Reduced", "None");
        softly.assertThat(result.getDrugsActionTaken().getMap().get("additional_drug").getValues()).containsExactly("Dose Reduced", "None");
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(4);
    }

    @Test
    public void shouldGetDrugsCausality() {
        AeFilters result = givenFilterSetup(filters -> {

            MapFilter<String, SetFilter<String>> causalityFilter = new MapFilter<>();
            Map<String, SetFilter<String>> causalityMap = new HashMap<>();
            causalityMap.put("DRUG001", new SetFilter<>(newArrayList("No"), false));
            causalityFilter.setMap(causalityMap);

            filters.setDrugsCausality(causalityFilter);
        });

        softly.assertThat(result.getDrugsCausality().getMap().get("DRUG001").getValues()).containsExactly("No");
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldGetAllDrugsCausality() {
        AeFilters result = givenFilterSetup(filters -> {
        });

        softly.assertThat(result.getDrugsCausality().getMap().get("DRUG001").getValues()).containsExactly("No", "Yes");
        softly.assertThat(result.getDrugsCausality().getMap().get("additional_drug").getValues()).containsExactly("No", "Yes");
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(4);
    }

    @Test
    public void shouldGetAllSeverity() {
        AeFilters result = givenFilterSetup(filters -> {
        });

        softly.assertThat(result.getSeverity().getValues()).containsOnly(SEVERITY_2.getWebappSeverity(), SEVERITY_3.getWebappSeverity(), null);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(4);
    }

    @Test
    public void shouldGetSeverity() {
        AeFilters result = givenFilterSetup(filters -> filters.setSeverity(new SetFilter(newArrayList(SEVERITY_2.getWebappSeverity()), true)));

        softly.assertThat(result.getSeverity().getValues()).containsOnly(null, SEVERITY_2.getWebappSeverity());
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(3);
    }

    @Test
    public void shouldGetAllSerious() {
        AeFilters result = givenFilterSetup(filters -> {
        });

        softly.assertThat(result.getSerious().getValues()).containsOnly("Yes", "No", null);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(4);
    }

    @Test
    public void shouldGetSerious() {
        AeFilters result = givenFilterSetup(filters -> filters.setSerious(new SetFilter(newArrayList("No"), true)));

        softly.assertThat(result.getSerious().getValues()).containsOnly(null, "No");
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(3);
    }

    @Test
    public void shouldGetAllStartDates() {
        AeFilters result = givenFilterSetup(filters -> {
        });

        assertThat(result.getStartDate().getFrom()).isInSameDayAs(DaysUtil.toDate("2015-08-01"));
        assertThat(result.getStartDate().getTo()).isInSameDayAs(DaysUtil.toDate("2015-08-01 23:59"));
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(4);
    }

    @Test
    public void shouldGetAllEndDates() {
        AeFilters result = givenFilterSetup(filters -> {
        });

        assertThat(result.getEndDate().getFrom()).isInSameDayAs(DaysUtil.toDate("2015-08-03"));
        assertThat(result.getEndDate().getTo()).isInSameDayAs(DaysUtil.toDate("2015-08-05 23:59"));
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(4);
    }

    @Test
    public void shouldGetEndDates() {
        AeFilters result = givenFilterSetup(filters -> filters.setEndDate(new DateRangeFilter(toDate("04.08.2015"), toDate("12.08.2015"))));

        assertThat(result.getEndDate().getFrom()).isInSameDayAs(DaysUtil.toDate("2015-08-05"));
        assertThat(result.getEndDate().getTo()).isInSameDayAs(DaysUtil.toDate("2015-08-05 23:59"));
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldGetAllDaysOnStudyAtStart() {
        AeFilters result = givenFilterSetup(filters -> {
        });

        assertThat(result.getDaysOnStudyAtStart().getFrom()).isEqualTo(-1);
        assertThat(result.getDaysOnStudyAtStart().getTo()).isEqualTo(0);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(4);
    }

    @Test
    public void shouldGetDaysOnStudyAtStart() {
        AeFilters result = givenFilterSetup(filters -> filters.setDaysOnStudyAtStart(new RangeFilter(0, 5)));

        assertThat(result.getDaysOnStudyAtStart().getFrom()).isEqualTo(0);
        assertThat(result.getDaysOnStudyAtStart().getTo()).isEqualTo(0);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldGetAllDaysOnStudyAtEnd() {
        AeFilters result = givenFilterSetup(filters -> {
        });

        assertThat(result.getDaysOnStudyAtEnd().getFrom()).isEqualTo(1);
        assertThat(result.getDaysOnStudyAtEnd().getTo()).isEqualTo(4);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(4);
    }

    @Test
    public void shouldGetAllDuration() {
        AeFilters result = givenFilterSetup(filters -> {
        });

        assertThat(result.getDuration().getFrom()).isEqualTo(3);
        assertThat(result.getDuration().getTo()).isEqualTo(5);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(4);
    }

    @Test
    public void shouldGetDuration() {
        AeFilters result = givenFilterSetup(filters -> filters.setDuration(new RangeFilter(4, 6)));

        assertThat(result.getDuration().getFrom()).isEqualTo(5);
        assertThat(result.getDuration().getTo()).isEqualTo(5);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldGetAllDescriptions() {
        AeFilters result = givenFilterSetup(filters -> {
        });

        assertThat(result.getDescription().getValues()).containsOnly("desc1", "desc2", null);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(4);
    }

    @Test
    public void shouldGetDescriptionsIncludeEmpty() {

        AeFilters result = givenFilterSetup(filters -> filters.setDescription(new SetFilter(newArrayList("desc1"), true)));

        assertThat(result.getDescription().getValues()).containsOnly("desc1", null);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(3);
    }

    @Test
    public void shouldGetAllOutcomes() {
        AeFilters result = givenFilterSetup(filters -> {
        });

        assertThat(result.getOutcome().getValues()).containsOnly("outcome1", "outcome2", null);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(4);
    }

    @Test
    public void shouldGetOutcomesIncludeEmpty() {

        AeFilters result = givenFilterSetup(filters -> filters.setOutcome(new SetFilter(newArrayList("outcome1"), true)));

        assertThat(result.getOutcome().getValues()).containsOnly("outcome1", null);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(3);
    }

    @Test
    public void shouldGetAllRequiredTreatment() {
        AeFilters result = givenFilterSetup(filters -> {
        });

        assertThat(result.getRequiredTreatment().getValues()).containsOnly("Yes", "No", null);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(4);
    }

    @Test
    public void shouldGetRequiredTreatmentIncludeEmpty() {

        AeFilters result = givenFilterSetup(filters -> filters.setRequiredTreatment(new SetFilter(newArrayList("Yes"), true)));

        assertThat(result.getRequiredTreatment().getValues()).containsOnly("Yes", null);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(3);
    }

    @Test
    public void shouldGetAllCausedSubjectWithdrawal() {
        AeFilters result = givenFilterSetup(filters -> {
        });

        assertThat(result.getCausedSubjectWithdrawal().getValues()).containsOnly("Yes", "No", null);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(4);
    }

    @Test
    public void shouldGetCausedSubjectWithdrawalIncludeEmpty() {

        AeFilters result = givenFilterSetup(filters -> filters.setCausedSubjectWithdrawal(new SetFilter(newArrayList("Yes"), true)));

        assertThat(result.getCausedSubjectWithdrawal().getValues()).containsOnly("Yes", null);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(3);
    }

    @Test
    public void shouldGetAllDoseLimitingToxicity() {
        AeFilters result = givenFilterSetup(filters -> {
        });

        assertThat(result.getDoseLimitingToxicity().getValues()).containsOnly("Yes", "No", null);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(4);
    }

    @Test
    public void shouldGetDoseLimitingToxicityIncludeEmpty() {

        AeFilters result = givenFilterSetup(filters -> filters.setDoseLimitingToxicity(new SetFilter(newArrayList("Yes"), true)));

        assertThat(result.getDoseLimitingToxicity().getValues()).containsOnly("Yes", null);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(3);
    }

    @Test
    public void shouldGetAllTimepointDoseLimitingToxicity() {
        AeFilters result = givenFilterSetup(filters -> {
        });

        assertThat(result.getTimePointDoseLimitingToxicity().getValues()).containsOnly("1", "2", null);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(4);
    }

    @Test
    public void shouldGetTimepointDoseLimitingToxicityIncludeEmpty() {

        AeFilters result = givenFilterSetup(filters -> filters.setTimePointDoseLimitingToxicity(new SetFilter(newArrayList("1"), true)));

        assertThat(result.getTimePointDoseLimitingToxicity().getValues()).containsOnly("1", null);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(3);
    }

    @Test
    public void shouldGetAllInfusionReaction() {
        AeFilters result = givenFilterSetup(filters -> {
        });

        assertThat(result.getInfusionReaction().getValues()).containsOnly("Yes", "No", null);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(4);
    }

    @Test
    public void shouldGetInfusionReactionIncludeEmpty() {

        AeFilters result = givenFilterSetup(filters -> filters.setInfusionReaction(new SetFilter(newArrayList("Yes"), true)));

        assertThat(result.getInfusionReaction().getValues()).containsOnly("Yes", null);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(3);
    }

    @Test
    public void shouldGetAllImmuneMediated() {
        AeFilters result = givenFilterSetup(filters -> {
        });

        assertThat(result.getImmuneMediated().getValues()).containsOnly("Yes", "No", null);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(4);
    }

    @Test
    public void shouldGetImmuneMediatedIncludeEmpty() {

        AeFilters result = givenFilterSetup(filters -> filters.setImmuneMediated(new SetFilter(newArrayList("Yes"), true)));

        assertThat(result.getImmuneMediated().getValues()).containsOnly("Yes", null);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(3);
    }

    @Test
    public void shouldGetAllActonTaken() {
        AeFilters result = givenFilterSetup(filters -> {
        });

        assertThat(result.getActionTaken().getValues()).containsOnly("Detect-AT1", "Detect-AT2", null);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(4);
    }

    @Test
    public void shouldGetActonTakenIncludeEmpty() {

        AeFilters result = givenFilterSetup(filters -> filters.setActionTaken(new SetFilter(newArrayList("Detect-AT1"), true)));

        assertThat(result.getActionTaken().getValues()).containsOnly("Detect-AT1", null);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(3);
    }

    @Test
    public void shouldGetAllCausality() {
        AeFilters result = givenFilterSetup(filters -> {
        });

        assertThat(result.getCausality().getValues()).containsOnly("Detect-CA1", "Detect-CA2", null);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(4);
    }

    @Test
    public void shouldGetCausalityIncludeEmpty() {

        AeFilters result = givenFilterSetup(filters -> filters.setCausality(new SetFilter(newArrayList("Detect-CA1"), true)));

        assertThat(result.getCausality().getValues()).containsOnly("Detect-CA1", null);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(3);
    }

    @Test
    public void shouldGetAllRequiresHospitalisation() {
        AeFilters result = givenFilterSetup(filters -> {
        });

        assertThat(result.getRequiresHospitalisation().getValues()).containsOnly("Yes", "No", null);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(4);
    }

    @Test
    public void shouldGetRequiresHospitalisationIncludeEmpty() {

        AeFilters result = givenFilterSetup(filters -> filters.setRequiresHospitalisation(new SetFilter(newArrayList("Yes"), true)));

        assertThat(result.getRequiresHospitalisation().getValues()).containsOnly("Yes", null);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(3);
    }

    @Test
    public void shouldGetAllTreatmentEmergent() {
        AeFilters result = givenFilterSetup(filters -> {
        });

        assertThat(result.getTreatmentEmergent().getValues()).containsOnly("Yes", "No", null);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(4);
    }

    @Test
    public void shouldGetTreatmentEmergentIncludeEmpty() {

        AeFilters result = givenFilterSetup(filters -> filters.setTreatmentEmergent(new SetFilter(newArrayList("Yes"), true)));

        assertThat(result.getTreatmentEmergent().getValues()).containsOnly("Yes", null);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(3);
    }

    @Test
    public void shouldGetAllUsedInTfl() {
        AeFilters result = givenFilterSetup(filters -> {
        });

        assertThat(result.getUsedInTfl().getValues()).containsOnly(true, false, null);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(4);
    }

    @Test
    public void shouldGetUsedInTflIncludeEmpty() {

        AeFilters result = givenFilterSetup(filters -> filters.setUsedInTfl(new SetFilter(newArrayList(true), true)));

        assertThat(result.getUsedInTfl().getValues()).containsOnly(true, null);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(3);
    }

    @Test
    public void shouldSetUsedInTfl() {

        AeFilters result = givenFilterSetup(filters -> filters.addUsedInTflFilter());

        assertThat(result.getUsedInTfl().getValues()).containsOnly(true);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldRemoveUsedInTfl() {

        AeFilters result = givenFilterSetup(filters -> {
            filters.addUsedInTflFilter();
            filters.removeUsedInTflFilter();
        });

        softly.assertThat(result.getUsedInTfl().getValues()).containsOnly(true, false, null);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(4);
    }

    @Test
    public void shouldGetAllStudyPeriod() {
        AeFilters result = givenFilterSetup(filters -> {
        });

        assertThat(result.getStudyPeriods().getValues()).containsOnly("SP1", "SP2", null);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(4);
    }

    @Test
    public void shouldGetStudyPeriodIncludeEmpty() {

        AeFilters result = givenFilterSetup(filters -> filters.setStudyPeriods(new SetFilter(newArrayList("SP1"), true)));

        assertThat(result.getStudyPeriods().getValues()).containsOnly("SP1", null);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(3);
    }

    @Test
    public void shouldGetAllSpecialInterestGroups() {
        AeFilters result = givenFilterSetup(filters -> {
        });

        assertThat(result.getSpecialInterestGroup().getValues()).containsOnly("SIG1", "SIG2", "SIG3", null);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(4);
    }

    @Test
    public void shouldGetSpecialInterestGroup2() {

        AeFilters result = givenFilterSetup(filters -> filters.setSpecialInterestGroup(new MultiValueSetFilter<>(newHashSet("SIG1"))));

        assertThat(result.getSpecialInterestGroup().getValues()).containsOnly("SIG1", "SIG2");
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(2);
    }

    @Test
    public void shouldGetSpecialInterestGroup3() {

        AeFilters result = givenFilterSetup(filters -> filters.setSpecialInterestGroup(new MultiValueSetFilter<>(newHashSet("SIG2"))));

        assertThat(result.getSpecialInterestGroup().getValues()).containsOnly("SIG1", "SIG2");
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldGetSpecialInterestGroup4() {

        AeFilters result = givenFilterSetup(filters -> filters.setSpecialInterestGroup(new MultiValueSetFilter<>(newHashSet("SIG3"))));

        assertThat(result.getSpecialInterestGroup().getValues()).containsOnly("SIG3");
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldGetDaysFromPrevDoseToStart() {

        AeFilters result = givenFilterSetup(filters -> {
        });

        assertThat(result.getDaysFromPrevDoseToStart().getFrom()).isEqualTo(2);
        assertThat(result.getDaysFromPrevDoseToStart().getTo()).isEqualTo(10);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(4);
    }

    @Test
    public void shouldGetDaysFromPrevDoseToStartRange() {

        AeFilters result = givenFilterSetup(filters -> filters.setDaysFromPrevDoseToStart(new RangeFilter(3, 11)));

        assertThat(result.getDaysFromPrevDoseToStart().getTo()).isEqualTo(10);
        assertThat(result.getDaysFromPrevDoseToStart().getTo()).isEqualTo(10);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldGetAeNumber() {

        AeFilters result = givenFilterSetup(filters -> {
        });

        assertThat(result.getAeNumber().getValues()).containsOnly("E01-1", "E01-3", "E02-2", "E02-4");
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(4);
    }

    @Test
    public void shouldGetAeNumberIncludeEmpty() {

        AeFilters result = givenFilterSetup(filters -> filters.setAeNumber(new SetFilter(newArrayList("E01-1"), true)));

        assertThat(result.getAeNumber().getValues()).containsOnly("E01-1");
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldGetAllComments() {

        AeFilters result = givenFilterSetup(filters -> {
        });

        assertThat(result.getComment().getValues()).containsOnly("comment1", "comment2", null);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(4);
    }

    @Test
    public void shouldGetCommentsIncludeEmpty() {

        AeFilters result = givenFilterSetup(filters -> filters.setComment(new SetFilter(newArrayList("comment1"), false)));

        assertThat(result.getComment().getValues()).containsOnly("comment1");
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldGetAllgetSuspectedEndpoint() {

        AeFilters result = givenFilterSetup(filters -> {
        });

        assertThat(result.getSuspectedEndpoint().getValues()).containsOnly("Yes", "No", null);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(4);
    }

    @Test
    public void shouldGetgetSuspectedEndpointIncludeEmpty() {

        AeFilters result = givenFilterSetup(filters -> filters.setSuspectedEndpoint(new SetFilter(newArrayList("Yes"), false)));

        assertThat(result.getSuspectedEndpoint().getValues()).containsOnly("Yes");
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldGetAllgetSuspectedEndpointCat() {

        AeFilters result = givenFilterSetup(filters -> {
        });

        assertThat(result.getSuspectedEndpointCategory().getValues()).containsOnly("SECat1", "SECat2", null);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(4);
    }

    @Test
    public void shouldGetgetSuspectedEndpointCatIncludeEmpty() {

        AeFilters result = givenFilterSetup(filters -> filters.setSuspectedEndpointCategory(new SetFilter(newArrayList("SECat1"), false)));

        assertThat(result.getSuspectedEndpointCategory().getValues()).containsOnly("SECat1");
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldGetAllAeOfSpecialInterest() {

        AeFilters result = givenFilterSetup(filters -> {
        });

        assertThat(result.getAeOfSpecialInterest().getValues()).containsOnly("Yes", "No", null);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(4);
    }

    @Test
    public void shouldGetAeOfSpecialInterestIncludeEmpty() {

        AeFilters result = givenFilterSetup(filters -> filters.setAeOfSpecialInterest(new SetFilter(newArrayList("Yes"), false)));

        assertThat(result.getAeOfSpecialInterest().getValues()).containsOnly("Yes");
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldGetAllAeStartPriorToRandomisation() {

        AeFilters result = givenFilterSetup(filters -> {
        });

        assertThat(result.getAeStartPriorToRandomisation().getValues()).containsOnly("Yes", "No", null);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(4);
    }

    @Test
    public void shouldGetAeStartPriorToRandomisationIncludeEmpty() {

        AeFilters result = givenFilterSetup(filters -> filters.setAeStartPriorToRandomisation(new SetFilter(newArrayList("Yes"), false)));

        assertThat(result.getAeStartPriorToRandomisation().getValues()).containsOnly("Yes");
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldQuery() {

        AeFilters f = new AeFilters();
        f.setPt(new SetFilter(newArrayList("pt1")));

        final List<Subject> subjects = events.stream().map(e -> e.getSubject()).collect(Collectors.toList());
        FilterQuery<Ae> filterQuery = new FilterQuery<>(events, f, subjects, PopulationFilters.empty());

        //When
        FilterResult<Ae> result = aeFilterService.query(filterQuery);

        //Then
        softly.assertThat(result.getFilteredResult()).hasSize(1);
        softly.assertThat(result.getFilteredResult()).extracting("id").containsOnly("1");
    }

    @Test
    public void shouldQueryWithNulls() {

        Ae ae5 = new Ae(AeRaw.builder().id("5").aeNumber(5).subjectId("subjectId5").pt("pt5").hlt("hlt5").soc(null).build(),
                Subject.builder().subjectId("subjectId5").build());
        List<Ae> moreEvents = newArrayList(ae1, ae2, ae3, ae4, ae5);

        final List<Subject> subjects = moreEvents.stream().map(e -> e.getSubject()).collect(Collectors.toList());
        AeFilters f = new AeFilters();
        f.setPt(new SetFilter(newArrayList("pt5")));
        f.setHlt(new SetFilter(newArrayList("hlt5")));
        f.setSoc(new SetFilter(newArrayList("soctrtrt"), true));

        FilterQuery<Ae> filterQuery = new FilterQuery<>(moreEvents, f, subjects, PopulationFilters.empty());

        //When
        FilterResult<Ae> result = aeFilterService.query(filterQuery);

        //Then
        softly.assertThat(result.getFilteredResult()).hasSize(1);
        softly.assertThat(result.getFilteredResult()).extracting("id").containsOnly("5");
    }
    
    @Test
    public void shouldQueryRangeFiltersWithNullsToAndFrom() {

        Ae ae1 = new Ae(AeRaw.builder().id("5").daysFromPrevDoseToStart(15).subjectId("subjectId5").build(),
                Subject.builder().subjectId("subjectId5").build());
        Ae ae2 = new Ae(AeRaw.builder().id("6").daysFromPrevDoseToStart(20).subjectId("subjectId6").build(),
                Subject.builder().subjectId("subjectId6").build());
        Ae ae3 = new Ae(AeRaw.builder().id("7").daysFromPrevDoseToStart(null).subjectId("subjectId7").build(),
                Subject.builder().subjectId("subjectId7").build());
        List<Ae> allEvents = newArrayList(ae1, ae2, ae3);

        final List<Subject> subjects = allEvents.stream().map(e -> e.getSubject()).collect(Collectors.toList());
        AeFilters f = new AeFilters();
        f.setDaysFromPrevDoseToStart(new RangeFilter(null, null, true));

        FilterQuery<Ae> filterQuery = new FilterQuery<>(allEvents, f, subjects, PopulationFilters.empty());

        //When
        FilterResult<Ae> result = aeFilterService.query(filterQuery);

        //Then
        softly.assertThat(result.getFilteredResult()).hasSize(3);
        softly.assertThat(result.getFilteredResult()).extracting("id").containsOnly("5", "6", "7");
    }
    
    @Test
    public void shouldQueryRangeFiltersWithNullsToAndFrom2() {

        Ae ae1 = new Ae(AeRaw.builder().id("5").daysFromPrevDoseToStart(15).subjectId("subjectId5").build(),
                Subject.builder().subjectId("subjectId5").build());
        Ae ae2 = new Ae(AeRaw.builder().id("6").daysFromPrevDoseToStart(20).subjectId("subjectId6").build(),
                Subject.builder().subjectId("subjectId6").build());
        Ae ae3 = new Ae(AeRaw.builder().id("7").daysFromPrevDoseToStart(null).subjectId("subjectId7").build(),
                Subject.builder().subjectId("subjectId7").build());
        List<Ae> allEvents = newArrayList(ae1, ae2, ae3);

        final List<Subject> subjects = allEvents.stream().map(e -> e.getSubject()).collect(Collectors.toList());
        AeFilters f = new AeFilters();
        f.setDaysFromPrevDoseToStart(new RangeFilter(null, null, false));

        FilterQuery<Ae> filterQuery = new FilterQuery<Ae>(allEvents, f, subjects, PopulationFilters.empty());

        //When
        FilterResult<Ae> result = aeFilterService.query(filterQuery);

        //Then
        softly.assertThat(result.getFilteredResult()).hasSize(2);
        softly.assertThat(result.getFilteredResult()).extracting("id").containsOnly("5", "6");
    }

    private AeFilters givenFilterSetup(final Consumer<AeFilters> filterSetter) {
        AeFilters aeFilters = new AeFilters();
        filterSetter.accept(aeFilters);
        return (AeFilters) aeFilterService.getAvailableFilters(events, aeFilters, subjects, PopulationFilters.empty());
    }

}
