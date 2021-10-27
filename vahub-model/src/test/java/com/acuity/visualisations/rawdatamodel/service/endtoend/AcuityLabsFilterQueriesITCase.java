package com.acuity.visualisations.rawdatamodel.service.endtoend;

import com.acuity.visualisations.config.annotation.TransactionalOracleITTest;
import com.acuity.visualisations.config.config.ApplicationModelConfigITCase;
import com.acuity.visualisations.rawdatamodel.dataproviders.LabDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.PopulationDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.filters.LabFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.filters.RangeFilter;
import com.acuity.visualisations.rawdatamodel.filters.SetFilter;
import com.acuity.visualisations.rawdatamodel.service.filters.LabFilterService;
import com.acuity.visualisations.rawdatamodel.suites.interfaces.LabTests;
import com.acuity.visualisations.rawdatamodel.util.DaysUtil;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Lab;
import com.acuity.va.security.acl.domain.Datasets;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_DATASETS;
import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {ApplicationModelConfigITCase.class})
@TransactionalOracleITTest
@Category(LabTests.class)
public class AcuityLabsFilterQueriesITCase {

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();
    @Autowired
    private LabDatasetsDataProvider labDatasetsDataProvider;
    @Autowired
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;
    @Autowired
    private LabFilterService labFilterService;

    @Test
    public void shouldListEmptyFilters() {
        List<String> result = getFiltersWithSetup(filters -> { }, DUMMY_ACUITY_DATASETS).getEmptyFilterNames();

        assertThat(result).containsOnly("analysisVisit", "studyPeriods", "protocolScheduleTimepoint", "valueDipstick", "sourceType");
    }

    @Test
    public void shouldReturnRangeOfMeasurementTimePointFromLabsFilter() throws Exception {
        LabFilters result = getFiltersWithSetup(filters -> { }, DUMMY_ACUITY_DATASETS);

        assertThat(result.getMeasurementTimePoint().getFrom()).isInSameDayAs(DaysUtil.toDate("2014-06-25"));
        assertThat(result.getMeasurementTimePoint().getTo()).isInSameDayAs(DaysUtil.toDate("2016-12-06 23:59"));
        assertThat(result.getMatchedItemsCount()).isEqualTo(38574);
    }

    @Test
    public void shouldReturnRangeOfDaysOnStudyFromLabsFilter() throws Exception {
        LabFilters availableFilters = getFiltersWithSetup(filters -> { }, DUMMY_ACUITY_DATASETS);

        softly.assertThat(availableFilters.getDaysOnStudy().getFrom()).isEqualTo(-28);
        softly.assertThat(availableFilters.getDaysOnStudy().getTo()).isEqualTo(747);
    }

    @Test
    public void shouldReturnRangeOfVisitNumberFromLabsFilter() throws Exception {
        LabFilters availableFilters = getFiltersWithSetup(filters -> { }, DUMMY_ACUITY_DATASETS);

        assertThat(availableFilters.getVisitNumber().getFrom()).isEqualTo(1.0);
        assertThat(availableFilters.getVisitNumber().getTo()).isEqualTo(310.0);
    }

    @Test
    public void shouldReturnRangeOfLabValueFromLabsFilter() throws Exception {
        LabFilters availableFilters = getFiltersWithSetup(filters -> { }, DUMMY_ACUITY_DATASETS);

        assertThat(availableFilters.getLabValue().getFrom()).isEqualTo(0.0);
        assertThat(availableFilters.getLabValue().getTo()).isEqualTo(7154.0);
    }

    @Test
    public void shouldReturnLabUnitsFromLabsFilter() throws Exception {
        LabFilters availableFilters = getFiltersWithSetup(filters -> { }, DUMMY_ACUITY_DATASETS);

        assertThat(availableFilters.getLabUnit().getValues()).containsOnly(
            "%", "1/mm3", "10**3/mm3", "10**3/uL", "10**6/mm3", "10**6/uL", "10**9/L", "10**12/L", "IU/L", "L/L", "U/L", "[ratio]", "fL", "g/L",
            "g/dL", "mEq/L", "mg/L", "mg/dL", "mmol/L", "ukat/L", "um3", "umol/L", "ml/min"
        );
    }

    @Test
    public void shouldReturnRangeOfBaselineValueFromLabsFilter() throws Exception {
        LabFilters availableFilters = getFiltersWithSetup(filters -> { }, DUMMY_ACUITY_DATASETS);

        assertThat(availableFilters.getBaselineValue().getFrom()).isEqualTo(0.0);
        assertThat(availableFilters.getBaselineValue().getTo()).isEqualTo(5520.0);
    }

    @Test
    public void shouldReturnRangeOfChangeFromBaselineValueFromLabsFilter() throws Exception {
        LabFilters availableFilters = getFiltersWithSetup(filters -> { }, DUMMY_ACUITY_DATASETS);

        assertThat(availableFilters.getChangeFromBaselineValue().getFrom()).isEqualTo(-5516.35);
        assertThat(availableFilters.getChangeFromBaselineValue().getTo()).isEqualTo(2172.0);
    }

    @Test
    public void shouldSortBaselineFlagFromLabsFilter() throws Exception {
        LabFilters availableFilters = getFiltersWithSetup(filters -> { }, DUMMY_ACUITY_DATASETS);

        assertThat(availableFilters.getBaselineFlag().getValues()).contains("N", "Y");
    }

    @Test
    public void shouldReturnRangeOfPercentageChangeFromBaselineValueFromLabsFilter() throws Exception {
        LabFilters availableFilters = getFiltersWithSetup(filters -> { }, DUMMY_ACUITY_DATASETS);

        softly.assertThat(availableFilters.getPercentageChangeFromBaselineValue().getFrom()).isEqualTo(-100.0);
        softly.assertThat(availableFilters.getPercentageChangeFromBaselineValue().getTo()).isEqualTo(8986.02);
    }

    @Test
    public void shouldSortOutOfRefRangeFromLabsFilter() throws Exception {
        LabFilters availableFilters = getFiltersWithSetup(filters -> { }, DUMMY_ACUITY_DATASETS);

        assertThat(availableFilters.getOutOfRefRange().getValues()).containsOnly("false", "true");
    }

    @Test
    public void shouldReturnRangeOfRefRangeNormValueFromLabsFilter() throws Exception {
        LabFilters availableFilters = getFiltersWithSetup(filters -> { }, DUMMY_ACUITY_DATASETS);

        assertThat(availableFilters.getRefRangeNormValue().getFrom()).isCloseTo(-5.17d, offset(1.0));
        assertThat(availableFilters.getRefRangeNormValue().getTo()).isCloseTo(55.83, offset(1.0));
    }

    @Test
    public void shouldReturnRangeOfRefRangeNormValueFromNullFromFilter() throws Exception {
        // Given
        LabFilters availableFilters = getFiltersWithSetup(filters -> filters.setRefRangeNormValue(new RangeFilter<>(null, 55.83)), DUMMY_ACUITY_DATASETS);

        assertThat(availableFilters.getRefRangeNormValue().getFrom()).isCloseTo(-5.2, offset(0.1));
        assertThat(availableFilters.getRefRangeNormValue().getTo()).isCloseTo(55.83, offset(0.1));
    }

    @Test
    public void shouldReturnRangeOfLabValueOverUpperRefValueFromLabsFilter() throws Exception {
        LabFilters availableFilters = getFiltersWithSetup(filters -> { }, DUMMY_ACUITY_DATASETS);

        assertThat(availableFilters.getLabValueOverUpperRefValue().getFrom()).isCloseTo(0.0, offset(1.0));
        assertThat(availableFilters.getLabValueOverUpperRefValue().getTo()).isCloseTo(34.3, offset(1.0));
    }

    @Test
    public void shouldReturnRangeOfLabValueOverLowerRefValue() throws Exception {
        LabFilters availableFilters = getFiltersWithSetup(filters -> { }, DUMMY_ACUITY_DATASETS);

        assertThat(availableFilters.getLabValueOverLowerRefValue().getFrom()).isCloseTo(0.0, offset(1.0));
        assertThat(availableFilters.getLabValueOverLowerRefValue().getTo()).isCloseTo(137.25, offset(1.0));
    }

    @Test
    public void shouldReturnRangeOfLowerRefValueFromLabsFilter() throws Exception {
        LabFilters availableFilters = getFiltersWithSetup(filters -> { }, DUMMY_ACUITY_DATASETS);

        assertThat(availableFilters.getLowerRefValue().getFrom()).isCloseTo(0.0, offset(1.0));
        assertThat(availableFilters.getLowerRefValue().getTo()).isCloseTo(2950.0, offset(1.0));
    }

    @Test
    public void shouldReturnRangeOfUpperRefValueFromLabsFilter() throws Exception {
        LabFilters availableFilters = getFiltersWithSetup(filters -> { }, DUMMY_ACUITY_DATASETS);

        assertThat(availableFilters.getUpperRefValue().getFrom()).isCloseTo(0.0, offset(1.0));
        assertThat(availableFilters.getUpperRefValue().getTo()).isCloseTo(7100.0, offset(1.0));
    }

    @Test
    public void shouldSortLabcodesFromLabsFilter() throws Exception {
        // When
        LabFilters availableFilters = getFiltersWithSetup(filters -> { }, DUMMY_ACUITY_DATASETS);

        // Then
        Set<String> labCodes = availableFilters.getLabcode().getValues();
        assertThat(labCodes).containsOnly(
                "Alanine Aminotransferase",
                "Albumin",
                "Alkaline Phosphatase",
                "Aspartate Aminotransferase",
                "B-Haemoglobin",
                "Basophils",
                "Basophils, Particle Concentration",
                "Calcium, Total",
                "Creatinine",
                "Creatinine Clearance",
                "Eosinophils",
                "Eosinophils, Particle Conc.",
                "Erythrocyte, Volume Fraction",
                "Erythrocytes, Mean Cell Volume",
                "Erythrocytes, Particle Concentration",
                "Gamma-Glutamyltransferase",
                "Glucose, Fasting",
                "Glucose,Random",
                "Lactate Dehydrogenase",
                "Leucocytes, Particle Concentration",
                "Lymphocytes",
                "Lymphocytes, Particle Concentration",
                "Monocytes",
                "Monocytes, Particle Concentration",
                "Neutrophils",
                "Neutrophils, Particle Concentration",
                "Platelets, Particle Concentration",
                "Potassium",
                "Sodium",
                "Total Bilirubin",
                "Urea",
                "Urea Nitrogen",
                "X"
        );

        assertThat(availableFilters.getMatchedItemsCount()).isEqualTo(38574);
    }

    @Test
    public void shouldFilterByLabcodes() throws Exception {
        // Given
        LabFilters availableFilters = getFiltersWithSetup(
                filters -> filters.setLabcode(new SetFilter<>(newArrayList("Albumin", "Basophils"))),
                DUMMY_ACUITY_DATASETS);

        // Then 
        assertThat(availableFilters.getLabcode().getValues()).containsOnly("Albumin", "Basophils");
    }

    @Test
    public void shouldGetLabValuesWhenFilteredByLabCode() throws Exception {
        LabFilters availableFilters = getFiltersWithSetup(
                filters -> filters.setLabcode(new SetFilter<>(newArrayList("Albumin", "Basophils"))),
                DUMMY_ACUITY_DATASETS);

        softly.assertThat(availableFilters.getLabValue().getFrom()).isEqualTo(0.0D);
        softly.assertThat(availableFilters.getLabValue().getTo()).isEqualTo(52.0D);
    }

    @Test
    public void shouldFilterByLabcodesAndPopulationFilter() throws Exception {
        LabFilters availableFilters = getFiltersWithSetup(
                filters -> filters.setLabcode(new SetFilter<>(newArrayList("Albumin", "Basophils"))),
                filters -> filters.setCentreNumbers(new SetFilter<>(newArrayList("1"))),
                DUMMY_ACUITY_DATASETS);

        // Then
        assertThat(availableFilters.getMatchedItemsCount()).isEqualTo(1503);
    }

    @Test
    public void shouldFilterByCategory() throws Exception {
        // Given
        LabFilters availableFilters = getFiltersWithSetup(filters -> filters.setLabCategory(new SetFilter<>(newArrayList("Chemistry"))), DUMMY_ACUITY_DATASETS);

        // Then
        assertThat(availableFilters.getLabCategory().getValues()).containsOnly("Chemistry");
    }

    private LabFilters getFiltersWithSetup(final Consumer<LabFilters> filterSetter, Datasets datasets) {

        Collection<Lab> labs = labDatasetsDataProvider.loadData(datasets);
        final Collection<Subject> subjects = populationDatasetsDataProvider.loadData(datasets);

        LabFilters labFilters = new LabFilters();
        filterSetter.accept(labFilters);
        return (LabFilters) labFilterService.getAvailableFilters(newArrayList(labs), labFilters, subjects, PopulationFilters.empty());
    }

    private LabFilters getFiltersWithSetup(final Consumer<LabFilters> labFilterSetter, final Consumer<PopulationFilters> populationFilterSetter,
                                           Datasets datasets) {

        final Collection<Lab> labs = labDatasetsDataProvider.loadData(datasets);
        final Collection<Subject> subjects = populationDatasetsDataProvider.loadData(datasets);

        LabFilters labFilters = new LabFilters();
        labFilterSetter.accept(labFilters);
        PopulationFilters populationFilters = new PopulationFilters();
        populationFilterSetter.accept(populationFilters);
        return (LabFilters) labFilterService.getAvailableFilters(newArrayList(labs), labFilters, subjects, populationFilters);
    }
}
