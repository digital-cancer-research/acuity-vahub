package com.acuity.visualisations.rawdatamodel.service.endtoend;

import com.acuity.visualisations.config.annotation.TransactionalOracleITTest;
import com.acuity.visualisations.config.config.ApplicationModelConfigITCase;
import com.acuity.visualisations.rawdatamodel.dataproviders.PopulationDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.VitalDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.filters.DateRangeFilter;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.filters.RangeFilter;
import com.acuity.visualisations.rawdatamodel.filters.SetFilter;
import com.acuity.visualisations.rawdatamodel.filters.VitalFilters;
import com.acuity.visualisations.rawdatamodel.service.filters.VitalFilterService;
import com.acuity.visualisations.rawdatamodel.util.DaysUtil;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Vital;
import com.acuity.va.security.acl.domain.Datasets;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_DATASETS;
import static com.acuity.visualisations.rawdatamodel.util.DateUtils.toDate;
import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {ApplicationModelConfigITCase.class})
@TransactionalOracleITTest
public class AcuityVitalsFilterRepositoryQueriesITCase {

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();
    @Autowired
    private VitalDatasetsDataProvider vitalsDatasetsDataProvider;
    @Autowired
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;
    @Autowired
    private VitalFilterService vitalFilterService;

    ///////////////////////////////////////////////////////////////////////////////////////
    //  Queries - Vital Measurements
    ///////////////////////////////////////////////////////////////////////////////////////
    @Test
    public void shouldListAllVitalsMeasurementsFromValidVitalsFilter() {
        // Given
        VitalFilters result = getFiltersWithSetup(filters -> {
        }, DUMMY_ACUITY_DATASETS);

        // When
        Set<String> measurements = result.getVitalsMeasurements().getValues();

        // Then
        softly.assertThat(measurements).containsExactlyInAnyOrder(
                "Systolic Blood Pressure",
                "Diastolic Blood Pressure",
                "Height",
                "Weight",
                "Pulse Rate");
        softly.assertThat(measurements).hasSize(5);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(9604);
    }

    @Test
    public void shouldListAllStudyPeriods() {
        // Given
        VitalFilters result = getFiltersWithSetup(filters -> {
        }, DUMMY_ACUITY_DATASETS);

        //When
        Set<String> studyPeriods = result.getStudyPeriods().getValues();

        //Then
        assertThat(studyPeriods).containsNull();
    }

    @Test
    public void shouldListVitalsMeasurementsFromValidVitalsFilter() {
        // Given
        VitalFilters result = getFiltersWithSetup(filters
                        -> filters.setVitalsMeasurements(new SetFilter<>(newArrayList("Systolic Blood Pressure", "Diastolic Blood Pressure"), false)),
                DUMMY_ACUITY_DATASETS
        );

        // When
        Set<String> measurements = result.getVitalsMeasurements().getValues();

        // Then
        softly.assertThat(measurements).containsOnly("Systolic Blood Pressure", "Diastolic Blood Pressure");
        softly.assertThat(measurements).hasSize(2);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(6240);
    }

    ///////////////////////////////////////////////////////////////////////////////////////
    //  Queries - Measurement date
    ///////////////////////////////////////////////////////////////////////////////////////
    @Test
    public void shouldFindAllMinMaxMeasurementDateFromValidVitalsFilter() {
        // Given
        VitalFilters result = getFiltersWithSetup(filters -> {
        }, DUMMY_ACUITY_DATASETS);

        // When
        DateRangeFilter measurementDate = result.getMeasurementDate();

        // Then
        assertThat(measurementDate.getFrom()).isInSameDayAs(DaysUtil.toDate("2014-06-25"));
        assertThat(measurementDate.getTo()).isInSameDayAs(DaysUtil.toDate("2016-12-06 23:59"));
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(9604);
    }

    @Test
    public void shouldFindAllMinMaxMeasurementDateFromValidVitalsFilterIncludeEmpty() {
        // Given
        VitalFilters result = getFiltersWithSetup(filters -> {
            filters.setMeasurementDate(new DateRangeFilter(toDate("27.01.2015"), toDate("06.12.2016"), false));
        }, DUMMY_ACUITY_DATASETS);

        // When
        DateRangeFilter measurementDate = result.getMeasurementDate();

        // Then
        assertThat(measurementDate.getFrom()).isInSameDayAs(DaysUtil.toDate("2015-01-27"));
        assertThat(measurementDate.getTo()).isInSameDayAs(DaysUtil.toDate("2016-12-06 23:59"));
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(7676);
    }

    @Test
    public void shouldFindAllMinMaxMeasurementDateFromValidNullVitalsFilter() {
        // Given
        VitalFilters result = getFiltersWithSetup(filters -> {
            filters.setMeasurementDate(new DateRangeFilter(toDate("29.01.2015"), null));
        }, DUMMY_ACUITY_DATASETS);

        // When
        DateRangeFilter measurementDate = result.getMeasurementDate();

        // Then
        softly.assertThat(measurementDate.getFrom()).isInSameDayAs(DaysUtil.toDate("2015-01-31"));
        softly.assertThat(measurementDate.getTo()).isInSameDayAs(DaysUtil.toDate("2016-12-06 23:59"));
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(7628);
    }

    ///////////////////////////////////////////////////////////////////////////////////////
    //  Queries - Days since first dose
    ///////////////////////////////////////////////////////////////////////////////////////
    @Test
    public void shouldFindAllMinMaxDaysSinceFirstDoseFromValidVitalsFilter() {
        // Given
        VitalFilters result = getFiltersWithSetup(filters -> {
        }, DUMMY_ACUITY_DATASETS);

        // When
        RangeFilter daysSinceFirstDose = result.getDaysSinceFirstDose();

        // Then
        softly.assertThat(daysSinceFirstDose.getFrom()).isEqualTo(-45);
        softly.assertThat(daysSinceFirstDose.getTo()).isEqualTo(782);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(9604);
    }

    @Test
    public void shouldFindAllMinMaxDaysSinceFirstDoseFromValidVitalsFilterIncludeEmpty() {
        // Given
        VitalFilters result = getFiltersWithSetup(filters -> filters.setDaysSinceFirstDose(new RangeFilter<Integer>(-45, 782, true)),
                DUMMY_ACUITY_DATASETS);
        // When
        RangeFilter daysSinceFirstDose = result.getDaysSinceFirstDose();

        // Then
        softly.assertThat(daysSinceFirstDose.getFrom()).isEqualTo(-45);
        softly.assertThat(daysSinceFirstDose.getTo()).isEqualTo(782);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(9604);
    }

    ///////////////////////////////////////////////////////////////////////////////////////
    //  Queries - Visit Number
    ///////////////////////////////////////////////////////////////////////////////////////
    @Test
    public void shouldFindAllMinMaxVisitNumberFromValidVitalsFilter() {
        // Given
        VitalFilters result = getFiltersWithSetup(filters -> {
        }, DUMMY_ACUITY_DATASETS);

        // When
        RangeFilter visitNumber = result.getVisitNumber();

        // Then
        softly.assertThat(visitNumber.getFrom()).isEqualTo(1.);
        softly.assertThat(visitNumber.getTo()).isEqualTo(310.);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(9604);
    }

    @Test
    public void shouldFindAllMinMaxVisitNumberValidVitalsFilterIncludeEmpty() {
        // Given
        VitalFilters result = getFiltersWithSetup(filters -> filters.setVisitNumber(new RangeFilter<>(2.0, 5.0)), DUMMY_ACUITY_DATASETS);

        // When
        RangeFilter visitNumber = result.getVisitNumber();

        // Then
        softly.assertThat(visitNumber.getFrom()).isEqualTo(2.0);
        softly.assertThat(visitNumber.getTo()).isEqualTo(5.0);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1440);
    }

    @Test
    public void shouldFindAllMinMaxVisitNumberValidNullVitalsFilter() {
        // Given
        VitalFilters result = getFiltersWithSetup(filters -> filters.setVisitNumber(new RangeFilter<>(2.0, null)), DUMMY_ACUITY_DATASETS);

        // When
        RangeFilter visitNumber = result.getVisitNumber();

        // Then
        softly.assertThat(visitNumber.getFrom()).isEqualTo(2.);
        softly.assertThat(visitNumber.getTo()).isEqualTo(310.);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(8989);
    }

    ///////////////////////////////////////////////////////////////////////////////////////
    //  Queries - Units
    ///////////////////////////////////////////////////////////////////////////////////////
    @Test
    public void shouldListAllUnitsFromValidVitalsFilter() {
        // Given
        VitalFilters result = getFiltersWithSetup(filters -> {
        }, DUMMY_ACUITY_DATASETS);

        // When
        Set<String> units = result.getUnits().getValues();

        // Then
        softly.assertThat(units).hasSize(4);
        softly.assertThat(units).containsExactlyInAnyOrder("Beats/Min", "cm", "kg", "mmHg");
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(9604);
    }

    @Test
    public void shouldListUnitsFromValidVitalsFilter() {
        // Given
        VitalFilters result = getFiltersWithSetup(filters -> filters.setUnits(new SetFilter<>(newArrayList("mmHg"), false)), DUMMY_ACUITY_DATASETS);

        // When
        Set<String> units = result.getUnits().getValues();

        // Then
        softly.assertThat(units).containsOnly("mmHg");
        softly.assertThat(units).hasSize(1);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(6240);
    }

    ///////////////////////////////////////////////////////////////////////////////////////
    //  Queries - % change from Baseline
    ///////////////////////////////////////////////////////////////////////////////////////
    //  Different from Model tests, but data in incorrect and out of date in the pre calc tables,  tested with test and was correct
    @Test
    public void shouldListAllPercentageChangeFromBaselineFromValidVitalsFilter() {
        // Given
        VitalFilters result = getFiltersWithSetup(filters -> {
        }, DUMMY_ACUITY_DATASETS);

        // When
        RangeFilter<Double> pcfbl = result.getPercentageChangeFromBaseline();

        // Then
        softly.assertThat(pcfbl.getFrom()).isCloseTo(-48.08, within(0.1));
        softly.assertThat(pcfbl.getTo()).isCloseTo(78.57, within(0.1));
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(9604);
    }

    //  Different from Model tests, but data in incorrect and out of date in the pre calc tables,  tested with test and was correct
    @Test
    public void shouldListPercentageChangeFromBaselineFromValidVitalsFilter() {
        // Given
        VitalFilters result = getFiltersWithSetup(filters -> filters.setPercentageChangeFromBaseline(new RangeFilter<>(0.0, 14d)), DUMMY_ACUITY_DATASETS);

        // When
        RangeFilter<Double> pcfbl = result.getPercentageChangeFromBaseline();

        // Then
        softly.assertThat(pcfbl.getFrom()).isEqualTo(0.);
        softly.assertThat(pcfbl.getTo()).isCloseTo(13.9, within(0.1));
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(3914);
    }

    ///////////////////////////////////////////////////////////////////////////////////////
    //  Queries - Change from Baseline
    ///////////////////////////////////////////////////////////////////////////////////////
    //  Different from Model tests, but data in incorrect and out of date in the pre calc tables,  tested with test and was correct
    // Ie the pre calc insert SQL was updated, but no re ran on IT to update the data.
    @Test
    public void shouldListAllChangeFromBaselineFromValidVitalsFilter() {
        // Given
        VitalFilters result = getFiltersWithSetup(filters -> {
        }, DUMMY_ACUITY_DATASETS);

        // When
        RangeFilter<Double> cfbl = result.getChangeFromBaseline();

        // Then
        softly.assertThat(cfbl.getFrom()).isCloseTo(-80., within(0.1));
        softly.assertThat(cfbl.getTo()).isCloseTo(80., within(0.1));
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(9604);
    }

    @Test
    public void shouldListChangeFromBaselineFromValidVitalsFilter() {
        // Given
        VitalFilters result = getFiltersWithSetup(filters -> filters.setChangeFromBaseline(new RangeFilter<>(20., 30.)), DUMMY_ACUITY_DATASETS);

        // When
        RangeFilter<Double> cfbl = result.getChangeFromBaseline();

        // Then
        softly.assertThat(cfbl.getFrom()).isEqualTo(20.0);
        softly.assertThat(cfbl.getTo()).isCloseTo(30., within(0.1));
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(472);
    }

    ///////////////////////////////////////////////////////////////////////////////////////
    //  Queries - baseline
    ///////////////////////////////////////////////////////////////////////////////////////
    @Test
    public void shouldListAllBaselineFromValidVitalsFilter() {
        // Given
        VitalFilters result = getFiltersWithSetup(filters -> {
        }, DUMMY_ACUITY_DATASETS);

        // When
        RangeFilter<Double> baseline = result.getBaseline();

        // Then
        softly.assertThat(baseline.getFrom()).isCloseTo(42., within(0.1));
        softly.assertThat(baseline.getTo()).isCloseTo(180., within(0.1));
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(9604);
    }

    @Test
    public void shouldListBaselineFromValidVitalsFilter() {
        // Given
        VitalFilters result = getFiltersWithSetup(filters -> filters.setBaseline(new RangeFilter<>(0d, 300d)), DUMMY_ACUITY_DATASETS);

        // When
        RangeFilter<Double> baseline = result.getBaseline();

        // Then
        softly.assertThat(baseline.getFrom()).isCloseTo(42., within(0.1));
        softly.assertThat(baseline.getTo()).isCloseTo(180., within(0.1));
    }

    ///////////////////////////////////////////////////////////////////////////////////////
    //  Queries - Baseline Flag
    ///////////////////////////////////////////////////////////////////////////////////////
    @Test
    public void shouldListAllBaselineFlagsFromValidVitalsFilter() {
        // Given
        VitalFilters result = getFiltersWithSetup(filters -> {
        }, DUMMY_ACUITY_DATASETS);

        // When
        Set<String> baselineFlags = result.getBaselineFlags().getValues();

        // Then
        softly.assertThat(baselineFlags).contains("Y", "N");
        softly.assertThat(baselineFlags).hasSize(2);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(9604);
    }

    @Test
    public void shouldListBaselineFlagsFromValidVitalsFilter() {
        // Given
        VitalFilters result = getFiltersWithSetup(filters -> filters.setBaselineFlags(new SetFilter<>(newArrayList("Y"), false)),
                DUMMY_ACUITY_DATASETS);

        // When
        Set<String> baselineFlags = result.getBaselineFlags().getValues();

        // Then
        softly.assertThat(baselineFlags).containsOnly("Y");
        softly.assertThat(baselineFlags).hasSize(1);
    }

    @Test
    public void testResultFilter() throws Exception {
        // Given
        VitalFilters result = getFiltersWithSetup(filters -> filters.setResultValue(new RangeFilter<>(70d, 104d)),
                DUMMY_ACUITY_DATASETS);

        // When
        RangeFilter<Double> resultValue = result.getResultValue();

        // Then
        softly.assertThat(resultValue.getFrom()).isEqualTo(70d);
        softly.assertThat(resultValue.getTo()).isEqualTo(104d);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(5763);
    }

    @Test
    public void shouldGetEmptyFilters() throws Exception {

        VitalFilters result = getFiltersWithSetup(filters -> {
        }, DUMMY_ACUITY_DATASETS);

        List<String> emptyFilters = result.getEmptyFilterNames();

        // Then  
        softly.assertThat(emptyFilters).containsOnly(
                "plannedTimePoints", "analysisVisit", "studyPeriods", "scheduleTimepoints",
                "lastDoseDate", "lastDoseAmounts", "anatomicalLocations", "sidesOfInterest",
                "physicalPositions", "clinicallySignificant"
        );
    }

    private VitalFilters getFiltersWithSetup(final Consumer<VitalFilters> filterSetter, Datasets datasets) {

        Collection<Vital> vitals = vitalsDatasetsDataProvider.loadData(datasets);
        final Collection<Subject> subjects = populationDatasetsDataProvider.loadData(datasets);

        VitalFilters vitalFilters = new VitalFilters();
        filterSetter.accept(vitalFilters);
        return (VitalFilters) vitalFilterService.getAvailableFilters(newArrayList(vitals), vitalFilters, subjects, PopulationFilters.empty());
    }

}
