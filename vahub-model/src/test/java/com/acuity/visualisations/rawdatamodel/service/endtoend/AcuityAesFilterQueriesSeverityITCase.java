package com.acuity.visualisations.rawdatamodel.service.endtoend;

import com.acuity.visualisations.config.annotation.TransactionalOracleITTest;
import com.acuity.visualisations.config.config.ApplicationModelConfigITCase;
import com.acuity.visualisations.rawdatamodel.dataproviders.AeSeverityChangeDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.PopulationDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.filters.AeFilters;
import com.acuity.visualisations.rawdatamodel.filters.DateRangeFilter;
import com.acuity.visualisations.rawdatamodel.filters.MapFilter;
import com.acuity.visualisations.rawdatamodel.filters.MultiValueSetFilter;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.filters.RangeFilter;
import com.acuity.visualisations.rawdatamodel.filters.SetFilter;
import com.acuity.visualisations.rawdatamodel.service.filters.AeFilterService;
import com.acuity.visualisations.rawdatamodel.util.DaysUtil;
import com.acuity.visualisations.rawdatamodel.vo.AeDetailLevel;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Ae;
import com.acuity.va.security.acl.domain.Datasets;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_DATASETS;
import static com.acuity.visualisations.rawdatamodel.util.DateUtils.toDate;
import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {ApplicationModelConfigITCase.class})
@TransactionalOracleITTest
public class AcuityAesFilterQueriesSeverityITCase {

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();
    @Autowired
    private AeSeverityChangeDatasetsDataProvider aeSeverityChangeDatasetsDataProvider;
    @Autowired
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;
    @Autowired
    private AeFilterService aeFilterService;

    ///////////////////////////////////////////////////////////////////////////////////////
    //  Queries - Pt
    ///////////////////////////////////////////////////////////////////////////////////////
    @Test
    public void shouldListAllPtFromValidAdverseEventFilter() {
        AeFilters result = getFiltersWithSetup(filters -> {
        }, DUMMY_ACUITY_DATASETS);

        Set<String> pts = result.getPt().getValues();

        softly.assertThat(pts).contains("ADDISON'S DISEASE",
                "MUSCULOSKELETAL PAIN",
                "AGEUSIA",
                "ECZEMA",
                "FATIGUE",
                "HYPOKALAEMIA");
        softly.assertThat(pts).hasSize(355);
        //softly.assertThat(keyAndInsertedRows.getRight()).isEqualTo(0);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(2538);
    }

    @Test
    public void shouldListPtFromValidAdverseEventFilterIncludeEmpty() {

        AeFilters result = getFiltersWithSetup(filters -> filters.setPt(new SetFilter<>(newArrayList("LEUKOPENIA"), true)), DUMMY_ACUITY_DATASETS);

        Set<String> pts = result.getPt().getValues();

        assertThat(pts).containsOnly("LEUKOPENIA", null);
        assertThat(pts).hasSize(2);
        assertThat(result.getMatchedItemsCount()).isEqualTo(19);

        result = getFiltersWithSetup(filters -> filters.setPt(new SetFilter<>(newArrayList("LEUKOPENIA"), false)), DUMMY_ACUITY_DATASETS);

        pts = result.getPt().getValues();

        assertThat(pts).doesNotContainNull();
    }

    //    @Test
//    public void shouldListAllStudyPeriods() {
//        // Given
//        Pair<FilterKey, Integer> keyAndInsertedRows = aesFilterService.insertIfMissing(DUMMY_ACUITY_DATASETS, AesFilters.empty(), PopulationFilters.empty());
//
//        //When
//        Set<String> studyPeriods = aesFilterRepositoryCached.getDistinctStudyPeriods(keyAndInsertedRows.getLeft());
//
//        //Then
//        softly.assertThat(studyPeriods).containsNull();
    //  }
    ///////////////////////////////////////////////////////////////////////////////////////
    //  Queries - Htl
    ///////////////////////////////////////////////////////////////////////////////////////
    @Test
    public void shouldListAllHltFromValidAdverseEventFilter() {

        AeFilters result = getFiltersWithSetup(filters -> {
        }, DUMMY_ACUITY_DATASETS);

        Set<String> hlts = result.getHlt().getValues();

        assertThat(hlts).contains("PARTIAL VISION LOSS", null, "LACRIMAL DISORDERS");
        softly.assertThat(hlts).hasSize(207);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(2538);
    }

    @Test
    public void shouldListHltFromValidAdverseEventFilterIncludeEmpty() {

        AeFilters result = getFiltersWithSetup(filters -> filters.setHlt(new SetFilter<>(newArrayList("PARTIAL VISION LOSS"), true)), DUMMY_ACUITY_DATASETS);

        Set<String> hlts = result.getHlt().getValues();

        assertThat(hlts).containsOnly(null, "PARTIAL VISION LOSS");
        assertThat(hlts).hasSize(2);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(6);

        result = getFiltersWithSetup(filters -> filters.setPt(new SetFilter<>(newArrayList("PARTIAL VISION LOSS"), false)), DUMMY_ACUITY_DATASETS);

        hlts = result.getHlt().getValues();

        assertThat(hlts).doesNotContainNull();
    }

    ///////////////////////////////////////////////////////////////////////////////////////
    //  Queries - Description
    ///////////////////////////////////////////////////////////////////////////////////////
    @Test
    public void shouldListAllDescriptionFromValidAdverseEventFilter() {

        AeFilters result = getFiltersWithSetup(filters -> {
        }, DUMMY_ACUITY_DATASETS);

        Set<String> descriptions = result.getDescription().getValues();

        assertThat(descriptions).containsNull();
        softly.assertThat(descriptions).hasSize(355);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(2538);
    }
//
//    @Test
//    @Ignore("nulls are replaced with '(Empty)'")
//    public void shouldListDescriptionFromValidAdverseEventFilterIncludeEmpty() {
//        // Given
//        AesFilters aesFilters = new AesFilters();
//        aesFilters.setDescription(new SetFilter<>(Collections.emptyList(), true));
//
//        // add in empty for reverse check
//        aesFilterService.insertIfMissing(DUMMY_ACUITY_DATASETS, AesFilters.empty(), PopulationFilters.empty());
//        Pair<FilterKey, Integer> keyAndInsertedRows = aesFilterService.insertIfMissing(DUMMY_ACUITY_DATASETS, aesFilters, PopulationFilters.empty());
//
//        // When
//        Set<String> descriptions = aesFilterRepositoryCached.getDistinctDescription(keyAndInsertedRows.getLeft());
//
//        // Then
//        assertThat(descriptions).containsNull();
//        assertThat(descriptions).hasSize(1);
//        //assertThat(keyAndInsertedRows.getRight()).isEqualTo(0);
//    }
//
    ///////////////////////////////////////////////////////////////////////////////////////
    //  Queries - Comment
    ///////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void shouldListAllCommentFromValidAdverseEventFilter() {
        AeFilters result = getFiltersWithSetup(filters -> {
        }, DUMMY_ACUITY_DATASETS);

        Set<String> comments = result.getComment().getValues();

        assertThat(comments).containsNull();
        softly.assertThat(comments).hasSize(1);
    }

    ///////////////////////////////////////////////////////////////////////////////////////
    //  Queries - Severity
    ///////////////////////////////////////////////////////////////////////////////////////
    @Test
    public void shouldListAllSeveritiesFromValidAdverseEventFilter() {

        AeFilters result = getFiltersWithSetup(filters -> {
        }, DUMMY_ACUITY_DATASETS);

        Set<String> severity = result.getSeverity().getValues();

        softly.assertThat(severity).containsExactlyInAnyOrder("CTC Grade 1", "CTC Grade 2", "CTC Grade 3", "CTC Grade 4", "CTC Grade 5", null);

    }

    @Test
    public void shouldListSeveritiesFromValidAdverseEventFilter() {

        AeFilters result = getFiltersWithSetup(filters
                -> filters.setSeverity(new SetFilter<>(newArrayList("CTC Grade 1", "CTC Grade 2"), false)), DUMMY_ACUITY_DATASETS);

        Set<String> severity = result.getSeverity().getValues();

        softly.assertThat(severity).containsExactlyInAnyOrder("CTC Grade 1", "CTC Grade 2");
    }

    ///////////////////////////////////////////////////////////////////////////////////////
    //  Queries - Soc
    ///////////////////////////////////////////////////////////////////////////////////////
    @Test
    public void shouldListAllSocFromValidAdverseEventFilter() {

        AeFilters result = getFiltersWithSetup(filters -> {
        }, DUMMY_ACUITY_DATASETS);

        Set<String> socs = result.getSoc().getValues();

        softly.assertThat(socs).containsExactlyInAnyOrder(null,
                "METAB",
                "RENAL AND URINARY DISORDERS",
                "RENAL",
                "GENRL",
                "INJ&P",
                "IMMUN",
                "CARD",
                "GASTR",
                "EYE",
                "INV",
                "HEPAT",
                "VASC",
                "ENDO",
                "PSYCH",
                "INFEC",
                "RESP",
                "EAR",
                "SKIN",
                "MUSC",
                "REPRO",
                "NERV",
                "NEOPL",
                "BLOOD");
        softly.assertThat(socs).hasSize(24);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(2538);
    }

    @Test
    public void shouldListSocFromValidAdverseEventFilterIncludeEmpty() {
        AeFilters result = getFiltersWithSetup(filters -> filters.setSoc(new SetFilter<>(newArrayList("NERV"), true)), DUMMY_ACUITY_DATASETS);

        Set<String> socs = result.getSoc().getValues();

        softly.assertThat(socs).containsOnly(null, "NERV");
        softly.assertThat(socs).hasSize(2);
        // softly.assertThat(keyAndInsertedRows.getRight()).isEqualTo(178);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(263);

        result = getFiltersWithSetup(filters -> filters.setSoc(new SetFilter<>(newArrayList("NERV"), false)), DUMMY_ACUITY_DATASETS);

        socs = result.getSoc().getValues();

        softly.assertThat(socs).doesNotContainNull();
    }

    ///////////////////////////////////////////////////////////////////////////////////////
    //  Queries - Serious
    ///////////////////////////////////////////////////////////////////////////////////////
    @Test
    public void shouldListAllSeriousFromValidAdverseEventFilter() {
        AeFilters result = getFiltersWithSetup(filters -> {
        }, DUMMY_ACUITY_DATASETS);

        Set<String> serious = result.getSerious().getValues();

        softly.assertThat(serious).containsExactlyInAnyOrder(null, "No", "Yes");
        softly.assertThat(serious).hasSize(3);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(2538);
    }

    @Test
    public void shouldListSeriousFromValidAdverseEventFilterIncludeEmpty() {
        AeFilters result = getFiltersWithSetup(filters -> filters.setSerious(new SetFilter<>(newArrayList("Yes"), true)), DUMMY_ACUITY_DATASETS);

        Set<String> serious = result.getSerious().getValues();

        softly.assertThat(serious).containsExactlyInAnyOrder(null, "Yes");
        softly.assertThat(serious).hasSize(2);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(57);

        result = getFiltersWithSetup(filters -> filters.setSerious(new SetFilter<>(newArrayList("Yes"), false)), DUMMY_ACUITY_DATASETS);

        serious = result.getSerious().getValues();

        // Then
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(53);
        softly.assertThat(serious).doesNotContainNull();
    }

    //    ///////////////////////////////////////////////////////////////////////////////////////
//    //  Queries - Causality
//    ///////////////////////////////////////////////////////////////////////////////////////
//    @Test
//    public void shouldListAllCausalityFromValidAdverseEventFilter() {
//         AeFilters result = getFiltersWithSetup(filters -> filters.setSerious(new SetFilter<>(newArrayList("Yes"), true)), DUMMY_ACUITY_DATASETS);
//
//        Set<String> serious = result.getDrugsCausality().getValues();
//        
//        // Given
//        Pair<FilterKey, Integer> keyAndInsertedRows = aesFilterService.insertIfMissing(DUMMY_ACUITY_DATASETS, AesFilters.empty(), PopulationFilters.empty());
//
//        // When
//        Set<String> causalities = aesFilterRepositoryCached.getDistinctCausalities(keyAndInsertedRows.getLeft());
//
//        // Then
//        softly.assertThat(causalities).containsNull();
//    }
//
//    @Test
//    public void shouldListAllDrugsCausalityFromValidAdverseEventFilter() {
//        // Given
//        Pair<FilterKey, Integer> keyAndInsertedRows = aesFilterService.insertIfMissing(DUMMY_ACUITY_DATASETS, AesFilters.empty(), PopulationFilters.empty());
//
//        // When
//        List<CategoryValues> causalities = aesFilterRepositoryCached.getDistinctDrugsCausality(keyAndInsertedRows.getLeft());
//
//        // Then
//        softly.assertThat(causalities).hasSize(4);
//        softly.assertThat(causalities)
//                .filteredOn("category", "AZD1234")
//                .extracting("value")
//                .containsOnly("Yes", "No");
//
//        softly.assertThat(causalities)
//                .filteredOn("category", "additional_drug")
//                .extracting("value")
//                .containsOnly("Yes", "No");
//     }
    @Test
    public void shouldListCausalityFromValidAdverseEventFilterIncludeNoEmpty() {

        AeFilters result = getFiltersWithSetup(filters -> {
            Map<String, SetFilter<String>> map = new HashMap<>();
            map.put("AZD1234", new SetFilter<>(newArrayList("No"), false));
            map.put("Additional_drug", new SetFilter<>(newArrayList("Yes"), false));
            MapFilter<String, SetFilter<String>> drugsCausality = new MapFilter<>();

            drugsCausality.setMap(map);

            filters.setDrugsCausality(drugsCausality);
        }, DUMMY_ACUITY_DATASETS);

        MapFilter<String, SetFilter<String>> drugsCausality = result.getDrugsCausality();

        // Then
        softly.assertThat(drugsCausality.getMap()).hasSize(2);
        softly.assertThat(drugsCausality.getMap().get("AZD1234").getValues()).containsOnly("No");
        softly.assertThat(drugsCausality.getMap().get("Additional_drug").getValues()).containsOnly("Yes");

        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(727);
    }

    @Test
    public void shouldListCausalityFromValidAdverseEventFilterOnlyEmpty() {
        /**
         * Expect to get more when including empty. Empty is defined both as null but also when that drug is not mentioned in the action
         */
        AeFilters result = getFiltersWithSetup(filters -> {
            Map<String, SetFilter<String>> map = new HashMap<>();
            map.put("AZD1234", new SetFilter<String>(newArrayList(), true));
            map.put("Additional_drug", new SetFilter<String>(newArrayList(), true));
            MapFilter<String, SetFilter<String>> drugsCausality = new MapFilter<>();
            drugsCausality.setMap(map);

            filters.setDrugsCausality(drugsCausality);
        }, DUMMY_ACUITY_DATASETS);

        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(4);
    }

    @Test
    public void shouldListCausalityFromValidAdverseEventFilterIncludeEmpty() {
        /**
         * Expect to get more when including empty. Empty is defined both as null but also when that drug is not mentioned in the action
         */
        AeFilters result = getFiltersWithSetup(filters -> {
            Map<String, SetFilter<String>> map = new HashMap<>();
            map.put("AZD1234", new SetFilter<>(newArrayList("No"), true));
            map.put("Additional_drug", new SetFilter<>(newArrayList("Yes"), true));
            MapFilter<String, SetFilter<String>> drugsCausality = new MapFilter<>();

            drugsCausality.setMap(map);

            filters.setDrugsCausality(drugsCausality);
        }, DUMMY_ACUITY_DATASETS);

        MapFilter<String, SetFilter<String>> drugsCausality = result.getDrugsCausality();

        // Then
        softly.assertThat(drugsCausality.getMap()).hasSize(2);
        softly.assertThat(drugsCausality.getMap().get("AZD1234").getValues()).containsOnly("No");
        softly.assertThat(drugsCausality.getMap().get("Additional_drug").getValues()).containsOnly("Yes");
    }

    ///////////////////////////////////////////////////////////////////////////////////////
    //  Queries - Action Taken
//    ///////////////////////////////////////////////////////////////////////////////////////
//    @Test
//    public void shouldListAllActionTakenFromValidAdverseEventFilter() {
//        AeFilters result = getFiltersWithSetup(filters -> {}, DUMMY_ACUITY_DATASETS);
//        
//        // Given
//        Pair<FilterKey, Integer> keyAndInsertedRows = aesFilterService.insertIfMissing(DUMMY_ACUITY_DATASETS, AesFilters.empty(), PopulationFilters.empty());
//
//        // When
//        Set<String> actionTakens = result.getDrugsActionTaken()..getDistinctActionTakens(keyAndInsertedRows.getLeft());
//
//        // Then
//        assertThat(actionTakens).containsNull();
//    }
//
//    @Test
//    public void shouldListDrugsActionTakenFromValidAdverseEventFilter() {
//        // Given
//        Pair<FilterKey, Integer> keyAndInsertedRows = aesFilterService.insertIfMissing(DUMMY_ACUITY_DATASETS, AesFilters.empty(), PopulationFilters.empty());
//
//        // When
//        List<CategoryValues> actionTakens = aesFilterRepositoryCached.getDistinctDrugsActionTaken(keyAndInsertedRows.getLeft());
//
//        // Then
//        softly.assertThat(actionTakens).hasSize(8);
//        softly.assertThat(actionTakens)
//                .filteredOn("category", "AZD1234")
//                .extracting("value")
//                .containsOnly("Temporarily Stopped", "Permanently Stopped", "Dose Changed", "None");
//
//        softly.assertThat(actionTakens)
//                .filteredOn("category", "additional_drug")
//                .extracting("value")
//                .containsOnly("Temporarily Stopped", "Permanently Stopped", "Dose Changed", "None");
//    }
//
    @Test
    public void shouldListActionTakenFromValidAdverseEventFilterNotIncludeEmpty() {

        AeFilters result = getFiltersWithSetup(filters -> {
            Map<String, MultiValueSetFilter<String>> map = new HashMap<>();
            map.put("AZD1234", new MultiValueSetFilter<>(newArrayList("None"), false));
            map.put("Additional_drug", new MultiValueSetFilter<>(newArrayList("Dose Changed"), false));
            MapFilter<String, MultiValueSetFilter<String>> drugsAction = new MapFilter<>();

            drugsAction.setMap(map);

            filters.setDrugsActionTaken(drugsAction);
        }, DUMMY_ACUITY_DATASETS);

        // Then
        softly.assertThat(result.getDrugsActionTaken().getMap()).hasSize(2);
        softly.assertThat(result.getDrugsActionTaken().getMap().get("AZD1234").getValues()).containsOnly("None");
        softly.assertThat(result.getDrugsActionTaken().getMap().get("Additional_drug").getValues()).containsOnly("Dose Changed");
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(62);
    }

    @Test
    public void shouldListActionTakenFromValidAdverseEventFilterIncludeOnlyEmpty() {

        AeFilters result = getFiltersWithSetup(filters -> {
            Map<String, MultiValueSetFilter<String>> map = new HashMap<>();
            map.put("AZD1234", new MultiValueSetFilter<String>(newArrayList(), true));
            map.put("additional_drug", new MultiValueSetFilter<String>(newArrayList(), true));
            MapFilter<String, MultiValueSetFilter<String>> drugsAction = new MapFilter<>();
            drugsAction.setMap(map);
            filters.setDrugsActionTaken(drugsAction);
        }, DUMMY_ACUITY_DATASETS);

        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(4);
    }

    @Test
    public void shouldListActionTakenFromValidAdverseEventFilterIncludeEmpty() {
        AeFilters result = getFiltersWithSetup(filters -> {
            Map<String, MultiValueSetFilter<String>> map = new HashMap<>();
            map.put("AZD1234", new MultiValueSetFilter<>(newArrayList("None"), true));
            map.put("Additional_drug", new MultiValueSetFilter<>(newArrayList("Dose Changed"), true));
            MapFilter<String, MultiValueSetFilter<String>> drugsAction = new MapFilter<>();
            drugsAction.setMap(map);
            filters.setDrugsActionTaken(drugsAction);
        }, DUMMY_ACUITY_DATASETS);

        softly.assertThat(result.getDrugsActionTaken().getMap()).hasSize(2);
        softly.assertThat(result.getDrugsActionTaken().getMap().get("AZD1234").getValues()).containsOnly("None");
        softly.assertThat(result.getDrugsActionTaken().getMap().get("Additional_drug").getValues()).containsOnly("Dose Changed");
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(66); // correct, sql wrong
    }

    ///////////////////////////////////////////////////////////////////////////////////////
    //  Queries - Special Interest Groups
    ///////////////////////////////////////////////////////////////////////////////////////
    @Test
    public void shouldListAllSpecialInterestGroupsFromValidAdverseEventFilter() {
        AeFilters result = getFiltersWithSetup(filters -> {
        }, DUMMY_ACUITY_DATASETS);

        Set<String> specialInterestGroups = result.getSpecialInterestGroup().getValues();

        softly.assertThat(specialInterestGroups).hasSize(1);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(2538);
    }

    ///////////////////////////////////////////////////////////////////////////////////////
    //  Queries - Suspected Endpoint
    ///////////////////////////////////////////////////////////////////////////////////////
    @Test
    public void shouldListAllSuspectedEndpointValidAdverseEventFilter() {
        AeFilters result = getFiltersWithSetup(filters -> {
        }, DUMMY_ACUITY_DATASETS);

        Set<String> suspectedEndpoints = result.getSuspectedEndpoint().getValues();

        softly.assertThat(suspectedEndpoints).hasSize(1);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(2538);
    }

    ///////////////////////////////////////////////////////////////////////////////////////
    //  Queries - Suspected Endpoint Cat
    ///////////////////////////////////////////////////////////////////////////////////////
    @Test
    public void shouldListAllSuspectedEndpointCategoryValidAdverseEventFilter() {
        AeFilters result = getFiltersWithSetup(filters -> {
        }, DUMMY_ACUITY_DATASETS);

        Set<String> suspectedEndpointCategories = result.getSuspectedEndpointCategory().getValues();

        softly.assertThat(suspectedEndpointCategories).hasSize(1);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(2538);
    }

    ///////////////////////////////////////////////////////////////////////////////////////
    //  Queries - Suspected Endpoint Cat
    ///////////////////////////////////////////////////////////////////////////////////////
    @Test
    public void shouldListAllAeOfSpecialInterestValidAdverseEventFilter() {
        AeFilters result = getFiltersWithSetup(filters -> {
        }, DUMMY_ACUITY_DATASETS);

        Set<String> aeOfSpecialInterest = result.getAeOfSpecialInterest().getValues();

        softly.assertThat(aeOfSpecialInterest).hasSize(1);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(2538);
    }

    @Test
    public void shouldGetAllRangeStartDate() {
        AeFilters result = getFiltersWithSetup(filters -> {
        }, DUMMY_ACUITY_DATASETS);

        DateRangeFilter actual = result.getStartDate();

        assertThat(actual.getFrom()).isInSameDayAs(DaysUtil.toDate("2014-07-26"));
        assertThat(actual.getTo()).isInSameDayAs(DaysUtil.toDate("2016-10-27 23:59"));
    }

    @Test
    public void shouldGetSpecifiedRangeStartDateWhenFiltered() throws Exception {
        AeFilters result = getFiltersWithSetup(filters
                -> filters.setStartDate(new DateRangeFilter(toDate("12.09.2014"), toDate("18.06.2015"))), DUMMY_ACUITY_DATASETS);

        DateRangeFilter actual = result.getStartDate();

        assertThat(DaysUtil.truncLocalTime(actual.getFrom())).isInSameDayAs(DaysUtil.toDate("2014-09-12"));
        assertThat(DaysUtil.truncLocalTime(actual.getTo())).isInSameDayAs(DaysUtil.toDate("2015-06-18"));
    }

    @Test
    public void shouldGetAllRangeEndDate() {
        AeFilters result = getFiltersWithSetup(filters -> {
        }, DUMMY_ACUITY_DATASETS);

        DateRangeFilter actual = result.getEndDate();

        assertThat(DaysUtil.truncLocalTime(actual.getFrom())).isInSameDayAs("2014-08-18");
        assertThat(DaysUtil.truncLocalTime(actual.getTo())).isInSameDayAs("2019-06-07");
    }

    @Test
    public void shouldGetSpecifiedRangeEndDateWhenFiltered() throws Exception {
        AeFilters result = getFiltersWithSetup(filters
                -> filters.setEndDate(new DateRangeFilter(toDate("01.01.2016"), toDate("19.11.2016"))), DUMMY_ACUITY_DATASETS);

        DateRangeFilter actual = result.getEndDate();

        assertThat(DaysUtil.truncLocalTime(actual.getFrom())).isInSameDayAs("2016-01-01");
        assertThat(DaysUtil.truncLocalTime(actual.getTo())).isInSameDayAs("2016-11-19");
    }

    @Test
    public void shouldGetAllRangeDuration() {
        AeFilters result = getFiltersWithSetup(filters -> {
        }, DUMMY_ACUITY_DATASETS);

        RangeFilter actual = result.getDuration();

        softly.assertThat(actual.getFrom()).isEqualTo(1);
        softly.assertThat(actual.getTo()).isEqualTo(1778);
    }

    @Test
    public void shouldGetAllRangeDurationWithFilter() {
        AeFilters result = getFiltersWithSetup(filters -> filters.setDuration(new RangeFilter<>(0, 10)), DUMMY_ACUITY_DATASETS);

        RangeFilter actual = result.getDuration();

        softly.assertThat(actual.getFrom()).isEqualTo(1);
        softly.assertThat(actual.getTo()).isEqualTo(10);
    }

    @Test
    public void shouldListAllOutcomesFromValidAdverseEventFilter() {
        AeFilters result = getFiltersWithSetup(filters -> {
        }, DUMMY_ACUITY_DATASETS);

        Set<String> outcomes = result.getOutcome().getValues();

        softly.assertThat(outcomes).containsNull();
        // Result expected from AZ1234 on DEV
        //softly.assertThat(outcomes).containsOnly("1", "0", "2", null);        
    }

    @Test
    public void shouldListAllRequiredTreatmentFromValidAdverseEventFilter() {
        AeFilters result = getFiltersWithSetup(filters -> {
        }, DUMMY_ACUITY_DATASETS);

        Set<String> requireTreatment = result.getRequiredTreatment().getValues();

        softly.assertThat(requireTreatment).containsNull();
        // Result expected from AZ1234 on DEV
        //softly.assertThat(requireTreatment).contains("Yes", "No");       
    }

    @Test
    public void shouldListAllCausedSubjectWithdrawalFromValidAdverseEventFilter() {
        AeFilters result = getFiltersWithSetup(filters -> {
        }, DUMMY_ACUITY_DATASETS);

        Set<String> causedSubjectWithdrawal = result.getCausedSubjectWithdrawal().getValues();

        softly.assertThat(causedSubjectWithdrawal).containsNull();
        // Result expected from AZ1234 on DEV
        //softly.assertThat(causedSubjectWithdrawal).contains("Yes", "No");  
    }

    @Test
    public void shouldListAllDoseLimitingToxicityFromValidAdverseEventFilter() {

        AeFilters result = getFiltersWithSetup(filters -> {
        }, DUMMY_ACUITY_DATASETS);

        Set<String> doseLimitingToxicity = result.getDoseLimitingToxicity().getValues();

        // Result expected from AZ1234 on DEV
        // softly.assertThat(doseLimitingToxicity).contains("Yes", "No");
        softly.assertThat(doseLimitingToxicity).containsNull();
    }

    @Test
    public void shouldListAllTimePointDoseLimitingToxicityFromValidAdverseEventFilter() {

        AeFilters result = getFiltersWithSetup(filters -> {
        }, DUMMY_ACUITY_DATASETS);

        Set<String> timepointDoseLimitingToxicity = result.getTimePointDoseLimitingToxicity().getValues();

        // Result expected from AZ1234 on DEV
        // softly.assertThat(result).contains("1","2","3","4");
        softly.assertThat(timepointDoseLimitingToxicity).containsNull();
    }

    @Test
    public void shouldListAllImmuneMediatedFromValidAdverseEventFilter() {
        AeFilters result = getFiltersWithSetup(filters -> {
        }, DUMMY_ACUITY_DATASETS);

        Set<String> immuneMediated = result.getImmuneMediated().getValues();
        // Result expected from AZ1234 on DEV
        //softly.assertThat(immuneMediated).contains("Yes", "No");
        softly.assertThat(immuneMediated).containsNull();
    }

    @Test
    public void shouldListAllInfusionReactionFromValidAdverseEventFilter() {
        AeFilters result = getFiltersWithSetup(filters -> {
        }, DUMMY_ACUITY_DATASETS);

        Set<String> infusionReaction = result.getInfusionReaction().getValues();
        // Result expected from AZ1234 on DEV
        //softly.assertThat(immuneMediated).contains("Yes", "No");
        softly.assertThat(infusionReaction).containsNull();
    }

    //    @Test
//    public void shouldGetSubjectIds() {
//        Pair<FilterKey, Integer> keyAndInsertedRows = aesFilterService.insertIfMissing(DUMMY_ACUITY_DATASETS,
//                AesFilters.empty(), PopulationFilters.empty());
//
//        Set<String> subjectIds = aesFilterRepositoryCached.getDistinctSubjectIds(keyAndInsertedRows.getLeft());
//
//        assertThat(subjectIds).hasSize(122);
//    }
//
//    /////////////////////////////////////////////////////////////////////////////////////
//    // days on study at start/end
//    /////////////////////////////////////////////////////////////////////////////////////
    @Test
    public void shouldGetAllRangeDaysOnStudyAtStart() {
        AeFilters result = getFiltersWithSetup(filters -> {
        }, DUMMY_ACUITY_DATASETS);

        RangeFilter<Integer> dayOnStudy = result.getDaysOnStudyAtStart();

        softly.assertThat(dayOnStudy.getFrom()).isEqualTo(-27);
        softly.assertThat(dayOnStudy.getTo()).isEqualTo(674);
    }

    @Test
    public void shouldGetSpecifiedRangeDaysOnStudyAtStartWhenFiltered() throws Exception {
        AeFilters result = getFiltersWithSetup(filters -> filters.setDaysOnStudyAtStart(new RangeFilter(0, 100)), DUMMY_ACUITY_DATASETS);

        RangeFilter<Integer> dayOnStudy = result.getDaysOnStudyAtStart();

        softly.assertThat(dayOnStudy.getFrom()).isEqualTo(0);
        softly.assertThat(dayOnStudy.getTo()).isEqualTo(100);
    }

    @Test
    public void shouldGetAllRangeDaysOnStudyAtEnd() {
        AeFilters result = getFiltersWithSetup(filters -> {
        }, DUMMY_ACUITY_DATASETS);

        RangeFilter<Integer> dayOnStudy = result.getDaysOnStudyAtEnd();

        softly.assertThat(dayOnStudy.getFrom()).isEqualTo(-14);
        softly.assertThat(dayOnStudy.getTo()).isEqualTo(1781);
    }

    @Test
    public void shouldGetSpecifiedRangeDaysOnStudyAtEndWhenFiltered() throws Exception {
        AeFilters result = getFiltersWithSetup(filters -> filters.setDaysOnStudyAtEnd(new RangeFilter<Integer>(100, 200)), DUMMY_ACUITY_DATASETS);

        RangeFilter<Integer> dayOnStudy = result.getDaysOnStudyAtEnd();

        softly.assertThat(dayOnStudy.getFrom()).isEqualTo(100);
        softly.assertThat(dayOnStudy.getTo()).isEqualTo(200);
    }

    @Test
    public void shouldGetAeNumber() throws Exception {
        AeFilters result = getFiltersWithSetup(filters -> {
        }, DUMMY_ACUITY_DATASETS);

        SetFilter<String> aeNumbers = result.getAeNumber();

        // Then
        assertThat(aeNumbers.getValues()).contains(null, "E0000100271-27", "E0000100209-4").hasSize(2073);
    }

    @Test
    public void shouldGetAeNumberWhenFiltered() throws Exception {
        AeFilters result = getFiltersWithSetup(filters
                -> filters.setAeNumber(new SetFilter<String>(newArrayList("E0000100278-4", "E0000100119-8"), true)), DUMMY_ACUITY_DATASETS);

        SetFilter<String> aeNumbers = result.getAeNumber();

        assertThat(aeNumbers.getValues()).containsOnly(null, "E0000100278-4", "E0000100119-8");
    }
//
//    @Test
//    public void shouldListSubjectECodesFromValidFilter() {
//        // Given
//        AesFilters aesFilters = new AesFilters();
//        aesFilters.setPt(new SetFilter<>(newArrayList("ABDOMINAL DISTENSION")));
//        Pair<FilterKey, Integer> keyAndInsertedRows = aesFilterService.insertIfMissing(DUMMY_ACUITY_DATASETS, aesFilters, PopulationFilters.empty());
//
//        // When
//        Set<String> result = aesFilterRepositoryCached.getDistinctSubjectECodes(keyAndInsertedRows.getLeft());
//
//        // Then
//        assertThat(result).containsOnly("E0000100151");
//
//    }
//

    @Test
    public void shouldGetCorrectListOfAeStartPriorToRand() {

        AeFilters result = getFiltersWithSetup(filters
                -> filters.setAeStartPriorToRandomisation(new SetFilter<>(Arrays.asList("No"))), DUMMY_ACUITY_DATASETS);

        Set<String> aeStartPriorToRandomisation = result.getAeStartPriorToRandomisation().getValues();

        assertThat(aeStartPriorToRandomisation).containsOnly("No");
    }

    @Test
    public void shouldGetCorrectListOfAeEndPriorToRand() {

        AeFilters result = getFiltersWithSetup(filters
                -> filters.setAeStartPriorToRandomisation(new SetFilter<>(Arrays.asList("No"))), DUMMY_ACUITY_DATASETS);

        Set<String> aeStartPriorToRandomisation = result.getAeStartPriorToRandomisation().getValues();

        assertThat(aeStartPriorToRandomisation).containsOnly("No");
    }

    @Test
    public void shouldGetCorrectListOfAeStartPriorToRandWithEmptyFilters() {

        AeFilters result = getFiltersWithSetup(filters -> {
        }, DUMMY_ACUITY_DATASETS);

        Set<String> aeStartPriorToRandomisation = result.getAeStartPriorToRandomisation().getValues();

        assertThat(aeStartPriorToRandomisation).containsOnly("No", null);
    }

    @Test
    public void shouldGetCorrectListOfAeEndPriorToRandWithEmptyFilters() {

        AeFilters result = getFiltersWithSetup(filters -> {
        }, DUMMY_ACUITY_DATASETS);

        Set<String> aeEndPriorToRandomisation = result.getAeEndPriorToRandomisation().getValues();

        assertThat(aeEndPriorToRandomisation).containsOnly("No", null);
    }

    @Test
    public void shouldGetEmptyFilters() throws Exception {

        AeFilters result = getFiltersWithSetup(filters -> {
        }, DUMMY_ACUITY_DATASETS);

        List<String> emptyFilters = result.getEmptyFilterNames();

        // Then  
        softly.assertThat(emptyFilters).containsOnly(
                "daysFromPrevDoseToStart",
                "studyPeriods",
                "comment",
                "actionTaken",
                "causality",
                "treatmentEmergent",
                "requiresHospitalisation",
                "suspectedEndpoint",
                "suspectedEndpointCategory",
                "aeOfSpecialInterest",
                "specialInterestGroup");
    }

    private AeFilters getFiltersWithSetup(final Consumer<AeFilters> filterSetter, Datasets datasets) {

        Collection<Ae> aes = aeSeverityChangeDatasetsDataProvider.loadData(datasets);
        final Collection<Subject> subjects = populationDatasetsDataProvider.loadData(datasets);

        AeFilters aeFilters = new AeFilters();
        aeFilters.setAeDetailLevel(AeDetailLevel.PER_SEVERITY_CHANGE);
        filterSetter.accept(aeFilters);
        return (AeFilters) aeFilterService.getAvailableFilters(newArrayList(aes), aeFilters, subjects, PopulationFilters.empty());
    }
}
