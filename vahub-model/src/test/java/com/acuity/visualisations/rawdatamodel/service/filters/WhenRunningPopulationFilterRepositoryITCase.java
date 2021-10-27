package com.acuity.visualisations.rawdatamodel.service.filters;

import com.acuity.visualisations.config.annotation.TransactionalOracleITTest;
import com.acuity.visualisations.config.config.ApplicationModelConfigITCase;
import com.acuity.visualisations.rawdatamodel.filters.MultiValueSetFilter;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.filters.RangeFilter;
import com.acuity.visualisations.rawdatamodel.filters.SetFilter;
import com.acuity.visualisations.rawdatamodel.util.DaysUtil;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_DATASETS;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {ApplicationModelConfigITCase.class})
@TransactionalOracleITTest
public class WhenRunningPopulationFilterRepositoryITCase extends PopulationITCase {

    private final String AMERICAN = "American";
    private final String RUSSIAN = "Russian";
    private final String JAPANESE = "Japanese";
    private final String SPANISH = "Spanish";
    private final String ITALIAN = "Italian";
    private final String ITALY = "Italy";
    private final String SPAIN = "Spain";
    private final String PORTUGAL = "Portugal";
    private final String ICELAND = "Iceland";
    private final String DEFAULT_GROUP = "Default group";
    private final String FIRST_GROUP = "First group";
    private final String SUBJECT_NAME_1 = "E000010040";
    private final String SUBJECT_NAME_2 = "E0000100253";
    private final String DEATH = "Death";

    private final int DELTA_IN_MILLISECONDS = 18000000;

    @Test
    public void shouldGetStudyPartFromValidFilter() {

        PopulationFilters populationFilters = new PopulationFilters();
        populationFilters.setStudyPart(new SetFilter<>(newArrayList("B")));

        PopulationFilters filters = getAvailablePopulationFilters(populationFilters);

        assertThat(filters.getStudyPart().getValues()).containsOnly("B");
    }

    @Test
    public void shouldGetDistinctMaxDoses() {

        PopulationFilters filters = getAvailablePopulationFiltersFromEmptyFilters();

        assertThat(filters.getDrugsMaxDoses().getMap().keySet()).containsExactlyInAnyOrder("AZD1234");
        assertThat(filters.getDrugsMaxDoses().getMap().get("AZD1234").getValues()).containsExactlyInAnyOrder("10 mg", "30 mg", "66 mg", "20 mg",
                "70 mg", "40 mg", "50 mg", "60 mg", "100 mg", "120 mg");
    }

    ///////////////////////////////////////////////////////////////////////////////////////
    //  Queries - Duration on Study
    ///////////////////////////////////////////////////////////////////////////////////////
    @Test
    public void shouldFindAllMinMaxDurationOnStudy() {

        PopulationFilters filters = getAvailablePopulationFiltersFromEmptyFilters();

        assertThat(filters.getDurationOnStudy().getFrom()).isEqualTo(11);
        assertThat(filters.getDurationOnStudy().getTo()).isEqualTo(1839);
    }

    @Test
    public void shouldFindAllMinMaxDurationOnStudyFromValidPopulationFilter() {

        PopulationFilters populationFilters = new PopulationFilters();
        populationFilters.setDurationOnStudy(new RangeFilter<>(0, 50));

        PopulationFilters filters = getAvailablePopulationFilters(populationFilters);

        assertThat(filters.getDurationOnStudy().getFrom()).isEqualTo(11);
        assertThat(filters.getDurationOnStudy().getTo()).isEqualTo(45);
//        assertThat(filters.getMatchedItemsCount()).isEqualTo(6); // is 192 - need check what's correct
    }

    @Test
    @Ignore("no suitable data in the database")
    public void shouldListAllEthnicGroupFromValidPopulationFilterFromVisualisationId() {

        PopulationFilters filters = getAvailablePopulationFiltersFromEmptyFilters();
        assertThat(filters.getEthnicGroup().getValues()).containsExactlyInAnyOrder(null, AMERICAN, RUSSIAN, SPANISH, JAPANESE, ITALIAN);
    }

    @Test
    @Ignore("no suitable data in the database")
    public void shouldListOnlyJapanesesFromValidPopulationFilterFromVisualisationId() {

        PopulationFilters populationFilters = new PopulationFilters();
        populationFilters.setEthnicGroup(new SetFilter<>(newArrayList(JAPANESE)));

        PopulationFilters filters = getAvailablePopulationFilters(populationFilters);

        assertThat(filters.getEthnicGroup().getValues()).containsExactly(JAPANESE);
    }

    // was for detect
    @Test
    public void shouldListAllRacesFromValidPopulationFilterFromVisualisationId() {

        PopulationFilters filters = getAvailablePopulationFiltersFromEmptyFilters();

        assertThat(filters.getRace().getValues()).containsExactlyInAnyOrder("White", "Asian", "Other");
    }

    @Test
    public void shouldListOnlyWhiteFromValidPopulationFilterFromVisualisationId() {

        PopulationFilters populationFilters = new PopulationFilters();
        populationFilters.setRace(new SetFilter<>(newArrayList("White")));

        PopulationFilters filters = getAvailablePopulationFilters(populationFilters);

        assertThat(filters.getRace().getValues()).containsExactly("White");
    }

    @Test
    public void shouldListAllSexesFromValidPopulationFilterFromVisualisationId() {

        PopulationFilters filters = getAvailablePopulationFiltersFromEmptyFilters();

        assertThat(filters.getSex().getValues()).containsExactlyInAnyOrder("Male", "Female");
    }

    @Test
    public void shouldListOnlyMaleFromValidPopulationFilterFromVisualisationId() {

        PopulationFilters populationFilters = new PopulationFilters();
        populationFilters.setSex(new SetFilter<>(newArrayList("Male")));

        PopulationFilters filters = getAvailablePopulationFilters(populationFilters);

        assertThat(filters.getSex().getValues()).containsExactly("Male");
    }

    @Test
    public void shouldGetDistinctAttendedVisitsWhen() {

        PopulationFilters populationFilters = new PopulationFilters();
        populationFilters.setAttendedVisits(new MultiValueSetFilter<>(newHashSet("6.01", "16.01")));

        PopulationFilters filters = getAvailablePopulationFilters(populationFilters);

        assertThat(filters.getAttendedVisits().getValues()).contains("6.01", "16.01");
    }

    @Test
    public void shouldGetDistinctCentreNumbersForSpecifiedValue() {

        PopulationFilters populationFilters = new PopulationFilters();
        populationFilters.setCentreNumbers(new SetFilter<>(newArrayList("1")));

        PopulationFilters filters = getAvailablePopulationFilters(populationFilters);

        assertThat(filters.getCentreNumbers().getValues()).containsExactly("1");
    }

    @Test
    public void shouldGetDistinctCentreNumbersForAllValues() {

        PopulationFilters filters = getAvailablePopulationFiltersFromEmptyFilters();

        assertThat(filters.getCentreNumbers().getValues()).containsExactly("1");
    }

    @Test
    @Ignore("no suitable data in the database")
    public void shouldGetDistinctCountriesForAllValues() {

        PopulationFilters filters = getAvailablePopulationFiltersFromEmptyFilters();

        assertThat(filters.getCountry().getValues()).containsExactlyInAnyOrder(null, SPAIN, ITALY, ICELAND, PORTUGAL);
    }

    @Test
    @Ignore("no suitable data in the database")
    public void shouldGetDistinctCountriesForSpecifiedValue() {

        PopulationFilters populationFilters = new PopulationFilters();
        populationFilters.setCountry(new SetFilter<>(newArrayList(ICELAND)));

        PopulationFilters filters = getAvailablePopulationFilters(populationFilters);

        assertThat(filters.getCountry().getValues()).containsExactly(ICELAND);
    }

    @Test
    public void shouldGetDistinctDoseCohortForSpecifiedGroup() {

        PopulationFilters populationFilters = new PopulationFilters();
        populationFilters.setDoseCohort(new SetFilter<>(newArrayList(DEFAULT_GROUP)));

        PopulationFilters filters = getAvailablePopulationFilters(populationFilters);

        assertThat(filters.getDoseCohort().getValues()).containsExactly(DEFAULT_GROUP);
    }

    @Test
    public void shouldGetDistinctDoseCohortForAllGroups() {

        PopulationFilters filters = getAvailablePopulationFiltersFromEmptyFilters();

        assertThat(filters.getDoseCohort().getValues()).containsExactly(DEFAULT_GROUP);
    }

    @Test
    public void shouldGetDistinctDrugsDiscontinuationReason() {

        PopulationFilters filters = getAvailablePopulationFiltersFromEmptyFilters();

        assertThat(filters.getDrugsDiscontinuationReason().getMap().keySet()).containsExactlyInAnyOrder("AZD1234");
        assertThat(filters.getDrugsDiscontinuationReason().getMap().get("AZD1234").getValues())
                .containsExactlyInAnyOrder(null, "Condition Under Investigation Worsened", "Voluntary Discontinuation by Subject",
                        "Adverse Event", "Other");
        assertThat(filters.getDrugsDiscontinuationReason().getMap().get("AZD1234").getIncludeEmptyValues()).isTrue();
    }

    @Test
    public void shouldGetDistinctDrugsDiscontinued() {

        PopulationFilters filters = getAvailablePopulationFiltersFromEmptyFilters();

        assertThat(filters.getDrugsDiscontinued().getMap().get("AZD1234").getValues()).containsExactlyInAnyOrder("Yes", "No");
    }

    @Test
    public void shouldGetDistinctDrugsDosed() {

        PopulationFilters filters = getAvailablePopulationFiltersFromEmptyFilters();

        assertThat(filters.getDrugsDosed().getMap().get("AZD1234").getValues()).containsExactly("Yes");
    }

    @Test
    public void shouldGetDistinctDrugsMaxFrequencies() {

        PopulationFilters filters = getAvailablePopulationFiltersFromEmptyFilters();

        assertThat(filters.getDrugsMaxFrequencies().getMap().get("AZD1234").getValues()).containsExactly("BID");
    }

    @Test
    public void shouldGetDistinctOtherCohortForAllValues() {

        PopulationFilters filters = getAvailablePopulationFiltersFromEmptyFilters();

        assertThat(filters.getOtherCohort().getValues()).containsExactlyInAnyOrder(new String[] {null});
    }

    @Test
    @Ignore("no suitable data in the database")
    public void shouldGetDistinctOtherCohortForSpecifiedValue() {

        PopulationFilters populationFilters = new PopulationFilters();
        populationFilters.setOtherCohort(new SetFilter<>(newArrayList(FIRST_GROUP)));

        PopulationFilters filters = getAvailablePopulationFilters(populationFilters);

        assertThat(filters.getOtherCohort().getValues()).containsExactly(FIRST_GROUP);
    }

    @Test
    public void shouldGetDistinctRandomisedForAllValues() {

        PopulationFilters filters = getAvailablePopulationFiltersFromEmptyFilters();

        assertThat(filters.getRandomised().getValues()).containsExactly("No", "Yes");
    }

    @Test
    public void shouldGetDistinctRandomisedForSpecifiedValue() {

        PopulationFilters populationFilters = new PopulationFilters();
        populationFilters.setRandomised(new SetFilter<>(newArrayList("Yes")));

        PopulationFilters filters = getAvailablePopulationFilters(populationFilters);

        assertThat(filters.getRandomised().getValues()).containsExactly("Yes");
    }

    @Test
    public void shouldGetDistinctStudyIdentifiers() {

        PopulationFilters filters = getAvailablePopulationFiltersFromEmptyFilters();

        assertThat(filters.getStudyIdentifier().getValues()).containsExactly("D1234C00001");
    }

    @Test
    public void shouldGetDistinctStudyIdentifiersWithFilter() {

        PopulationFilters populationFilters = new PopulationFilters();
        populationFilters.setStudyIdentifier(new SetFilter<>(newArrayList("D1234C00001")));

        PopulationFilters filters = getAvailablePopulationFilters(populationFilters);

        assertThat(filters.getStudyIdentifier().getValues()).containsExactly("D1234C00001");
    }

    @Test
    public void shouldGetDistinctSubjectDeaths() {

        PopulationFilters filters = getAvailablePopulationFiltersFromEmptyFilters();

        assertThat(filters.getDeath().getValues()).containsExactlyInAnyOrder("Yes", "No");
    }

    @Test
    public void shouldGetDistinctSubjectIdsForAllValues() {

        PopulationFilters filters = getAvailablePopulationFiltersFromEmptyFilters();

        assertThat(filters.getSubjectId().getValues()).hasSize(124);
    }

    @Test
    public void shouldGetDistinctSubjectIdsForSpecifiedValues() {

        PopulationFilters populationFilters = new PopulationFilters();
        populationFilters.setSubjectId(new SetFilter<>(newArrayList("E000010068")));

        PopulationFilters filters = getAvailablePopulationFilters(populationFilters);

        assertThat(filters.getSubjectId().getValues()).containsExactly("E000010068");
    }

    @Test
    public void shouldGetDistinctSubjectsForSpecifiedValues() {

        PopulationFilters populationFilters = new PopulationFilters();
        populationFilters.setSubjectId(new SetFilter<>(newArrayList(SUBJECT_NAME_1, SUBJECT_NAME_2)));

        PopulationFilters filters = getAvailablePopulationFilters(populationFilters);

        assertThat(filters.getSubjectId().getValues()).containsExactly(SUBJECT_NAME_1, SUBJECT_NAME_2);
    }

    @Test
    public void shouldGetDistinctWithdrawalCompletionForAllValues() {

        PopulationFilters filters = getAvailablePopulationFiltersFromEmptyFilters();

        assertThat(filters.getWithdrawalCompletion().getValues()).containsExactlyInAnyOrder("Yes", "No");
    }

    @Test
    public void shouldGetDistinctWithdrawalCompletionForSpecifiedValue() {

        PopulationFilters populationFilters = new PopulationFilters();
        populationFilters.setWithdrawalCompletion(new SetFilter<>(newArrayList("Yes")));

        PopulationFilters filters = getAvailablePopulationFilters(populationFilters);

        assertThat(filters.getWithdrawalCompletion().getValues()).containsExactly("Yes");
    }

    @Test
    public void shouldGetDistinctWithdrawalCompletionReason() {

        PopulationFilters populationFilters = new PopulationFilters();
        populationFilters.setWithdrawalCompletionReason(new SetFilter<>(newArrayList(DEATH)));

        PopulationFilters filters = getAvailablePopulationFilters(populationFilters);

        assertThat(filters.getWithdrawalCompletionReason().getValues()).containsExactly(DEATH);
    }

    @Test
    public void shouldGetMinMaxAge() {

        PopulationFilters filters = getAvailablePopulationFiltersFromEmptyFilters();

        assertThat(filters.getAge().getFrom()).isEqualTo(17);
        assertThat(filters.getAge().getTo()).isEqualTo(69);
    }

    @Test
    public void shouldGetMinMaxDrugsDiscontinuationDate() {

        PopulationFilters filters = getAvailablePopulationFiltersFromEmptyFilters();

        assertThat(filters.getDrugsDiscontinuationDate().getMap().get("AZD1234").getFrom()).isInSameMinuteAs(DaysUtil.toDate("2014-08-20"));
        assertThat(filters.getDrugsDiscontinuationDate().getMap().get("AZD1234").getTo()).isInSameMinuteAs(DaysUtil.toDate("2016-10-31 23:59"));
    }

    @Test
    public void shouldGetMinMaxDrugsTotalDurationExclBreaks() {

        PopulationFilters filters = getAvailablePopulationFiltersFromEmptyFilters();

        assertThat(filters.getDrugsTotalDurationExclBreaks().getMap().get("AZD1234").getFrom()).isEqualTo(1);
        assertThat(filters.getDrugsTotalDurationExclBreaks().getMap().get("AZD1234").getTo()).isEqualTo(1720);
    }

    @Test
    public void shouldGetMinMaxDrugsTotalDurationInclBreaks() {

        PopulationFilters filters = getAvailablePopulationFiltersFromEmptyFilters();

        assertThat(filters.getDrugsTotalDurationInclBreaks().getMap().get("AZD1234").getFrom()).isEqualTo(1);
        assertThat(filters.getDrugsTotalDurationInclBreaks().getMap().get("AZD1234").getTo()).isEqualTo(1757);
    }

    @Test
    public void shouldGetMinMaxFirstTreatmentDate() {

        PopulationFilters filters = getAvailablePopulationFiltersFromEmptyFilters();

        assertThat(filters.getFirstTreatmentDate().getFrom()).isInSameDayAs(DaysUtil.toDate("2014-07-22"));
        assertThat(filters.getFirstTreatmentDate().getTo()).isInSameDayAs(DaysUtil.toDate("2015-04-20 23:59"));
    }

    @Test
    public void shouldGetMinMaxRandomisationDate() {

        PopulationFilters filters = getAvailablePopulationFiltersFromEmptyFilters();

        assertThat(filters.getRandomisationDate().getFrom()).isInSameDayAs(DaysUtil.toDate("2013-01-13"));
        assertThat(filters.getRandomisationDate().getTo()).isInSameDayAs(DaysUtil.toDate("2014-12-21 23:59"));
    }

    @Test
    public void shouldGetEmptyFilters() throws Exception {

        PopulationFilters result = getAvailablePopulationFiltersFromEmptyFilters();

        List<String> emptyFilters = result.getEmptyFilterNames(DUMMY_ACUITY_DATASETS);

        // Then  
        softly.assertThat(emptyFilters).containsOnly(
                "plannedTreatmentArm",
                "actualTreatmentArm",
                "siteIDs",
                "regions",
                "lastTreatmentDate",
                "phase",
                "medicalHistory",
                "studySpecificFilters",
                "safetyPopulation",
                "totalStudyDuration",
                "biomarkerGroups",
                "exposureInDays",
                "actualExposureInDays",
                "country",
                "specifiedEthnicGroup",
                "ethnicGroup",
                "doseCohort",
                "otherCohort");
    }
}
