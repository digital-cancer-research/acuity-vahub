package com.acuity.visualisations.rawdatamodel.service.filters;

import com.acuity.visualisations.config.annotation.TransactionalOracleITTest;
import com.acuity.visualisations.config.config.ApplicationModelConfigITCase;
import com.acuity.visualisations.rawdatamodel.filters.DateRangeFilter;
import com.acuity.visualisations.rawdatamodel.filters.MapFilter;
import com.acuity.visualisations.rawdatamodel.filters.MultiValueSetFilter;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.filters.RangeFilter;
import com.acuity.visualisations.rawdatamodel.filters.SetFilter;
import com.acuity.visualisations.rawdatamodel.util.DaysUtil;
import com.google.common.collect.Ordering;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.acuity.visualisations.config.util.TestConstants.ETHNIC_ASHANTI;
import static com.acuity.visualisations.config.util.TestConstants.ETHNIC_BONOMAN;
import static com.acuity.visualisations.rawdatamodel.util.DaysUtil.toDate;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {ApplicationModelConfigITCase.class})
@TransactionalOracleITTest
public class WhenRunningAcuityPopulationFilterServiceITCase extends PopulationITCase {

    @Test
    public void shouldGetAvailableFiltersWithEmptyFilters() throws Exception {

        PopulationFilters availableFilters = getAvailablePopulationFiltersFromEmptyFilters();

        softly.assertThat(availableFilters.getAge().getFrom()).isEqualTo(17);
        softly.assertThat(availableFilters.getAge().getTo()).isEqualTo(69);
        softly.assertThat(availableFilters.getSex().getValues()).containsExactlyInAnyOrder("Male", "Female");
        softly.assertThat(availableFilters.getDrugsDosed().getMap().keySet()).containsExactly("AZD1234");
        softly.assertThat(availableFilters.getDrugsDiscontinued().getMap().keySet()).containsExactly("AZD1234");
        softly.assertThat(availableFilters.getDrugsMaxDoses().getMap().keySet()).containsExactly("AZD1234");
        softly.assertThat(availableFilters.getDrugsMaxFrequencies().getMap().keySet()).containsExactly("AZD1234");
        softly.assertThat(availableFilters.getDrugsDiscontinuationDate().getMap().keySet()).containsExactly("AZD1234");
        softly.assertThat(availableFilters.getDrugsDiscontinuationReason().getMap().keySet()).containsExactly("AZD1234");
        assertThat(availableFilters.getFirstTreatmentDate().getFrom()).isInSameDayAs(DaysUtil.toDate("2014-07-22"));
        assertThat(availableFilters.getFirstTreatmentDate().getTo()).isInSameDayAs(DaysUtil.toDate("2015-04-20 23:59"));
        assertThat(availableFilters.getRandomisationDate().getFrom()).isInSameDayAs(DaysUtil.toDate("2013-01-13"));
        assertThat(availableFilters.getRandomisationDate().getTo()).isInSameDayAs(DaysUtil.toDate("2014-12-21 23:59"));
    }

    @Test
    @Ignore
    public void shouldGetAvailableFiltersWithTotalStudyDurationFilter() throws Exception {

        PopulationFilters popFilter = new PopulationFilters();
        popFilter.setTotalStudyDuration(new RangeFilter<>(300, 1000));

        PopulationFilters availableFilters = getAvailablePopulationFilters(popFilter);

        softly.assertThat(availableFilters.getTotalStudyDuration().getFrom()).isGreaterThanOrEqualTo(300);
        softly.assertThat(availableFilters.getTotalStudyDuration().getTo()).isLessThanOrEqualTo(1000);
    }

    @Test
    public void shouldGetAvailableFiltersWithAttendedVisitsFilter() throws Exception {

        PopulationFilters popFilter = new PopulationFilters();
        popFilter.setAttendedVisits(new MultiValueSetFilter<>(newHashSet("201.01")));

        PopulationFilters availableFilters = getAvailablePopulationFilters(popFilter);

        softly.assertThat(availableFilters.getAttendedVisits().getValues()).contains("201.01");
    }

    @Test
    public void shouldGetAvailableFiltersWithDrugDosedFilter() throws Exception {

        MapFilter<String, SetFilter<String>> drugsDosed = new MapFilter<>();
        Map<String, SetFilter<String>> map = new HashMap<>();
        map.put("AZD1234", new SetFilter<>(newArrayList("Yes"), false));
        drugsDosed.setMap(map);

        PopulationFilters popFilter = new PopulationFilters();
        popFilter.setDrugsDosed(drugsDosed);

        PopulationFilters availableFilters = getAvailablePopulationFilters(popFilter);

        softly.assertThat(availableFilters.getMatchedItemsCount()).isEqualTo(124);

    }

    @Test
    public void shouldGetAvailableFiltersWithDrugDosedFilter2() throws Exception {

        MapFilter<String, SetFilter<String>> drugsDosed = new MapFilter<>();
        Map<String, SetFilter<String>> map = new HashMap<>();
        map.put("AZD1234", new SetFilter<>(newArrayList("No"), false));
        drugsDosed.setMap(map);

        PopulationFilters popFilter = new PopulationFilters();
        popFilter.setDrugsDosed(drugsDosed);

        PopulationFilters availableFilters = getAvailablePopulationFilters(popFilter);

        softly.assertThat(availableFilters.getMatchedItemsCount()).isEqualTo(0);
    }

    @Test
    public void shouldGetAvailableFiltersWithDrugMaxFreqFilter() throws Exception {

        MapFilter<String, SetFilter<String>> freqs = new MapFilter<>();
        Map<String, SetFilter<String>> map = new HashMap<>();
        map.put("AZD1234", new SetFilter<>(newArrayList("BID"), false));
        freqs.setMap(map);

        PopulationFilters popFilter = new PopulationFilters();
        popFilter.setDrugsMaxFrequencies(freqs);

        PopulationFilters availableFilters = getAvailablePopulationFilters(popFilter);

        softly.assertThat(availableFilters.getMatchedItemsCount()).isEqualTo(124);
    }

    @Test
    public void shouldGetAvailableFiltersWithDrugDscDateFilter() throws Exception {

        MapFilter<Date, DateRangeFilter> dsc = new MapFilter<>();
        Map<String, DateRangeFilter> map = new HashMap<>();
        map.put("AZD1234", new DateRangeFilter(toDate("2015-11-09"), toDate("2015-11-24"), false));
        dsc.setMap(map);

        PopulationFilters popFilter = new PopulationFilters();
        popFilter.setDrugsDiscontinuationDate(dsc);

        PopulationFilters availableFilters = getAvailablePopulationFilters(popFilter);

        softly.assertThat(availableFilters.getMatchedItemsCount()).isEqualTo(5);
    }

    @Test
    public void shouldGetAvailableFiltersWithDrugDscDateFilter2() throws Exception {

        MapFilter<Date, DateRangeFilter> dsc = new MapFilter<>();
        Map<String, DateRangeFilter> map = new HashMap<>();
        map.put("AZD1234", new DateRangeFilter(toDate("2015-11-02"), toDate("2015-11-08"), false));
        dsc.setMap(map);

        PopulationFilters popFilter = new PopulationFilters();
        popFilter.setDrugsDiscontinuationDate(dsc);

        PopulationFilters availableFilters = getAvailablePopulationFilters(popFilter);

        softly.assertThat(availableFilters.getMatchedItemsCount()).isEqualTo(0);
    }

    @Test
    public void shouldGetAvailableFiltersWithDrugDscDateFilter3() throws Exception {

        MapFilter<Date, DateRangeFilter> dsc = new MapFilter<>();
        Map<String, DateRangeFilter> map = new HashMap<>();
        map.put("AZD1234", new DateRangeFilter(toDate("2014-10-10"), toDate("2015-11-11"), true));
        dsc.setMap(map);

        PopulationFilters popFilter = new PopulationFilters();
        popFilter.setDrugsDiscontinuationDate(dsc);

        PopulationFilters availableFilters = getAvailablePopulationFilters(popFilter);

        softly.assertThat(availableFilters.getMatchedItemsCount()).isEqualTo(88);
    }

    @Test
    public void shouldGetAvailableFiltersWithDrugDosedAndDscFilter() throws Exception {

        MapFilter<String, SetFilter<String>> drugsDosed = new MapFilter<>();
        Map<String, SetFilter<String>> map = new HashMap<>();
        map.put("AZD1234", new SetFilter<>(newArrayList("Yes"), false));
        drugsDosed.setMap(map);

        MapFilter<String, SetFilter<String>> drugsDsc = new MapFilter<>();
        HashMap<String, SetFilter<String>> dsc = new HashMap<>();
        dsc.put("AZD1234", new SetFilter<>(newArrayList("Yes"), false));
        drugsDsc.setMap(dsc);

        PopulationFilters popFilter = new PopulationFilters();
        popFilter.setDrugsDosed(drugsDosed);
        popFilter.setDrugsDiscontinued(drugsDsc);

        PopulationFilters availableFilters = getAvailablePopulationFilters(popFilter);

        softly.assertThat(availableFilters.getMatchedItemsCount()).isEqualTo(111);
    }

    @Test
    public void shouldGetAvailableFiltersWithDrugDosedAndMaxDoseFilter() throws Exception {

        MapFilter<String, SetFilter<String>> drugsDosed = new MapFilter<>();
        Map<String, SetFilter<String>> map = new HashMap<>();
        map.put("AZD1234", new SetFilter<>(newArrayList("Yes"), false));
        drugsDosed.setMap(map);

        MapFilter<String, SetFilter<String>> drugsMaxDoses = new MapFilter<>();
        HashMap<String, SetFilter<String>> mapFreq = new HashMap<>();
        mapFreq.put("AZD1234", new SetFilter<>(newArrayList("100 mg"), false));
        drugsMaxDoses.setMap(mapFreq);

        PopulationFilters popFilter = new PopulationFilters();
        popFilter.setDrugsDosed(drugsDosed);
        popFilter.setDrugsMaxDoses(drugsMaxDoses);

        PopulationFilters availableFilters = getAvailablePopulationFilters(popFilter);

        softly.assertThat(availableFilters.getMatchedItemsCount()).isEqualTo(2);

    }

    @Test
    public void shouldGetAvailableFiltersWithDrugDosedAndMaxDoseFilter2() throws Exception {

        MapFilter<String, SetFilter<String>> drugsDosed = new MapFilter<>();
        Map<String, SetFilter<String>> map = new HashMap<>();
        map.put("AZD1234", new SetFilter<>(newArrayList("Yes"), false));
        drugsDosed.setMap(map);

        MapFilter<String, SetFilter<String>> drugsMaxDoses = new MapFilter<>();
        HashMap<String, SetFilter<String>> mapFreq = new HashMap<>();
        mapFreq.put("AZD1234", new SetFilter<>(newArrayList("100 mg", "60 mg"), false));
        drugsMaxDoses.setMap(mapFreq);

        PopulationFilters popFilter = new PopulationFilters();
        popFilter.setDrugsDosed(drugsDosed);
        popFilter.setDrugsMaxDoses(drugsMaxDoses);

        PopulationFilters availableFilters = getAvailablePopulationFilters(popFilter);

        softly.assertThat(availableFilters.getMatchedItemsCount()).isEqualTo(23);
    }

    @Test
    public void shouldGetAvailableFiltersWithDrugDosedFilter3() throws Exception {

        MapFilter<String, SetFilter<String>> drugsDosed = new MapFilter<>();
        Map<String, SetFilter<String>> map = new HashMap<>();
        map.put("AZD1234", new SetFilter<>(newArrayList("No"), false));
        drugsDosed.setMap(map);

        PopulationFilters popFilter = new PopulationFilters();
        popFilter.setDrugsDosed(drugsDosed);

        PopulationFilters availableFilters = getAvailablePopulationFilters(popFilter);

        softly.assertThat(availableFilters.getMatchedItemsCount()).isEqualTo(0);
    }

    @Test
    public void shouldGetAvailableFiltersWithStdDurInclFilter() throws Exception {

        MapFilter<Integer, RangeFilter<Integer>> dur = new MapFilter<>();
        Map<String, RangeFilter<Integer>> map = new HashMap<>();
        map.put("AZD1234", new RangeFilter<>(new Integer(0), new Integer(100), false));
        dur.setMap(map);

        PopulationFilters popFilter = new PopulationFilters();
        popFilter.setDrugsTotalDurationInclBreaks(dur);

        PopulationFilters availableFilters = getAvailablePopulationFilters(popFilter);

        softly.assertThat(availableFilters.getMatchedItemsCount()).isEqualTo(73);
    }

    @Test
    public void shouldGetAvailableFiltersWithCentreFilter() throws Exception {

        PopulationFilters popFilter = new PopulationFilters();
        popFilter.setCentreNumbers(new SetFilter<>(newArrayList("1")));

        PopulationFilters availableFilters = getAvailablePopulationFilters(popFilter);

        softly.assertThat(availableFilters.getMatchedItemsCount()).isEqualTo(124);
    }

    @Test
    public void shouldGetAvailableFiltersWithStdDurExclFilter() throws Exception {

        MapFilter<Integer, RangeFilter<Integer>> dur = new MapFilter<>();
        Map<String, RangeFilter<Integer>> map = new HashMap<>();
        map.put("AZD1234", new RangeFilter<>(new Integer(0), new Integer(100), false));
        dur.setMap(map);

        PopulationFilters popFilter = new PopulationFilters();
        popFilter.setDrugsTotalDurationExclBreaks(dur);

        PopulationFilters availableFilters = getAvailablePopulationFilters(popFilter);

        softly.assertThat(availableFilters.getMatchedItemsCount()).isEqualTo(111);
    }

    @Test
    public void shouldGetAvailableFiltersWithDrugDscDateAndReasonFilter() throws Exception {

        MapFilter<Date, DateRangeFilter> dsc = new MapFilter<>();
        Map<String, DateRangeFilter> map = new HashMap<>();
        map.put("AZD1234", new DateRangeFilter(toDate("2014-11-01"), toDate("2014-11-10"), false));
        dsc.setMap(map);

        MapFilter<String, SetFilter<String>> drugsDscRsn = new MapFilter<>();
        Map<String, SetFilter<String>> map2 = new HashMap<>();
        map2.put("AZD1234", new SetFilter<>(newArrayList("Adverse Event"), false));
        drugsDscRsn.setMap(map2);

        PopulationFilters popFilter = new PopulationFilters();
        popFilter.setDrugsDiscontinuationDate(dsc);
        popFilter.setDrugsDiscontinuationReason(drugsDscRsn);

        PopulationFilters availableFilters = getAvailablePopulationFilters(popFilter);

        softly.assertThat(availableFilters.getMatchedItemsCount()).isEqualTo(1);
        softly.assertThat(availableFilters.getDrugsDiscontinuationReason().getMap().get("AZD1234").getSortedValues()).containsExactly("Adverse Event");
    }

    @Test
    public void shouldGetAvailableFiltersWithStdDurExclAndDateFilter() throws Exception {

        MapFilter<Date, DateRangeFilter> dsc = new MapFilter<>();
        Map<String, DateRangeFilter> map = new HashMap<>();
        map.put("AZD1234", new DateRangeFilter(toDate("2015-11-21"), toDate("2015-12-26"), false));
        dsc.setMap(map);

        MapFilter<Integer, RangeFilter<Integer>> dur = new MapFilter<>();
        Map<String, RangeFilter<Integer>> map2 = new HashMap<>();
        map2.put("AZD1234", new RangeFilter<>(new Integer(0), new Integer(100), false));
        dur.setMap(map2);

        PopulationFilters popFilter = new PopulationFilters();
        popFilter.setDrugsDiscontinuationDate(dsc);
        popFilter.setDrugsTotalDurationExclBreaks(dur);

        PopulationFilters availableFilters = getAvailablePopulationFilters(popFilter);

        softly.assertThat(availableFilters.getMatchedItemsCount()).isEqualTo(7);
    }

    @Test
    public void shouldGetDistinctDrugsMaxDosesSortedInNumericalOrder() throws Exception {
        // Given
        PopulationFilters availableFilters = getAvailablePopulationFiltersFromEmptyFilters();
        // When
        availableFilters.getDrugsMaxDoses().getMap().values().forEach(f -> {
            List<Integer> values = f.getSortedValues().stream().map(v -> Integer.valueOf(v.replaceAll("\\D*$", ""))).collect(Collectors.toList());
            softly.assertThat(Ordering.natural().nullsLast().isOrdered(values)).isTrue();
        });
    }

    @Test
    public void shouldGetDistinctDrugsDiscontinuationReasonSortedInAlphaOrder() throws Exception {
        // Given
        PopulationFilters availableFilters = getAvailablePopulationFiltersFromEmptyFilters();
        // When
        availableFilters.getDrugsDiscontinuationReason().getMap().values().forEach(f -> softly.assertThat(Ordering.natural().nullsLast().isOrdered(f
                .getSortedValues())).isTrue());
    }

    // differs from old model: nulls are not collected
    @Test
    @Ignore("no suitable data in the database")
    public void shouldGetAvailableFiltersWithSpecifiedEthnicGroupFilterofSizeTwo() throws Exception {

        PopulationFilters availableFilters = getAvailablePopulationFiltersFromEmptyFilters();

        softly.assertThat(availableFilters.getSpecifiedEthnicGroup().getValues().size()).isEqualTo(3);
        softly.assertThat(availableFilters.getSpecifiedEthnicGroup().getSortedValues()).containsExactly(ETHNIC_ASHANTI, ETHNIC_BONOMAN, null);
    }

    @Test
    @Ignore("no suitable data in the database")
    public void shouldGetAvailableFiltersWithPresetSpecifiedEthnicGroupFilter() throws Exception {
        PopulationFilters filters = new PopulationFilters();
        filters.setSpecifiedEthnicGroup(new SetFilter<>(newArrayList(ETHNIC_ASHANTI)));

        PopulationFilters availableFilters = getAvailablePopulationFilters(filters);

        softly.assertThat(availableFilters.getMatchedItemsCount()).isEqualTo(1);
    }
}
