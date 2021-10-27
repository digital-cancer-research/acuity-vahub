package com.acuity.visualisations.rawdatamodel.service.endtoend;

import com.acuity.visualisations.config.annotation.TransactionalOracleITTest;
import com.acuity.visualisations.config.config.ApplicationModelConfigITCase;
import com.acuity.visualisations.rawdatamodel.dataproviders.CardiacDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.PopulationDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.filters.CardiacFilters;
import com.acuity.visualisations.rawdatamodel.filters.DateRangeFilter;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.filters.RangeFilter;
import com.acuity.visualisations.rawdatamodel.filters.SetFilter;
import com.acuity.visualisations.rawdatamodel.service.filters.CardiacFilterService;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Cardiac;
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

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_DATASETS;
import static com.acuity.visualisations.config.util.TestConstants.MULTI_DUMMY_ACUITY_DATASETS;
import static com.acuity.visualisations.rawdatamodel.util.DaysUtil.toDate;
import static com.acuity.visualisations.rawdatamodel.util.DaysUtil.truncLocalTime;
import static java.util.Collections.singletonList;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {ApplicationModelConfigITCase.class})
@TransactionalOracleITTest
public class AcuityCardiacFilterITCase {

    @Autowired
    private CardiacFilterService cardiacFilterService;

    @Autowired
    private CardiacDatasetsDataProvider cardiacsDataProvider;

    @Autowired
    private PopulationDatasetsDataProvider populationDataProvider;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test
    // Adopted from WhenRunningCardiacFilterRepositoryITCase
    public void shouldListAllMeasurementCategoriesFromEmptyFilter() {
        CardiacFilters filters = getFilters(DUMMY_ACUITY_DATASETS, CardiacFilters.empty());

        softly.assertThat(filters.getMatchedItemsCount()).isEqualTo(4867);
        softly.assertThat(filters.getMeasurementCategory().getValues())
                .containsOnly("ECG", "Ejection fraction");

        softly.assertThat(filters.getMeasurementName().getValues())
                .containsOnly("QTcF - Fridericia's Correction Formula",
                        "Summary (Mean) PR Duration",
                        "Summary (Mean) QRS Duration",
                        "Summary (Mean) QT Duration",
                        "Summary (Mean) RR Duration",
                        "LVEF");

        softly.assertThat(filters.getVisitNumber().getFrom()).isEqualTo(1d);
        softly.assertThat(filters.getVisitNumber().getTo()).isEqualTo(306d);

        softly.assertThat(filters.getResultUnit().getValues()).containsNull();
        softly.assertThat(filters.getBaselineFlag().getValues()).containsOnly("Y", "N");
        softly.assertThat(filters.getClinicallySignificant().getValues()).containsNull();

        softly.assertThat(filters.getResultValue().getFrom()).isEqualTo(8d);
        softly.assertThat(filters.getResultValue().getTo()).isEqualTo(1270d);

        softly.assertThat(filters.getBaselineValue().getFrom()).isEqualTo(50d);
        softly.assertThat(filters.getBaselineValue().getTo()).isEqualTo(1270d);

        softly.assertThat(filters.getChangeFromBaselineValue().getFrom()).isEqualTo(-520d);
        softly.assertThat(filters.getChangeFromBaselineValue().getTo()).isEqualTo(690d);

        softly.assertThat(filters.getPercentageChangeFromBaselineValue().getFrom()).isEqualTo(-92.16d);
        softly.assertThat(filters.getPercentageChangeFromBaselineValue().getTo()).isEqualTo(1150d);

        softly.assertThat(filters.getDaysOnStudy().getFrom()).isEqualTo(-35);
        softly.assertThat(filters.getDaysOnStudy().getTo()).isEqualTo(781);

        softly.assertThat(truncLocalTime(filters.getMeasurementTimePoint().getFrom())).isInSameDayAs(toDate("2014-07-13"));
        softly.assertThat(truncLocalTime(filters.getMeasurementTimePoint().getTo())).isInSameDayAs(toDate("2016-11-05"));
    }

    @Test
    // Adopted from WhenRunningCardiacFilterRepositoryMergingITCase
    public void shouldListAllMeasurementCategoriesFromEmptyFilterMultiDataset() {
        CardiacFilters filters = getFilters(MULTI_DUMMY_ACUITY_DATASETS, CardiacFilters.empty());

        softly.assertThat(filters.getMatchedItemsCount()).isEqualTo(9759);

        softly.assertThat(filters.getMeasurementName().getValues())
                .containsOnly("QTcF - Fridericia's Correction Formula",
                        "Summary (Mean) PR Duration",
                        "Summary (Mean) QRS Duration",
                        "Summary (Mean) QT Duration",
                        "Summary (Mean) RR Duration",
                        "LVEF");

        softly.assertThat(filters.getVisitNumber().getFrom()).isEqualTo(1d);
        softly.assertThat(filters.getVisitNumber().getTo()).isEqualTo(306d);

        softly.assertThat(filters.getBaselineFlag().getValues()).containsOnly("Y", "N");

        softly.assertThat(filters.getBaselineValue().getFrom()).isEqualTo(50d);
        softly.assertThat(filters.getBaselineValue().getTo()).isEqualTo(1270d);

        softly.assertThat(filters.getChangeFromBaselineValue().getFrom()).isEqualTo(-520d);
        softly.assertThat(filters.getChangeFromBaselineValue().getTo()).isEqualTo(690d);

        softly.assertThat(filters.getPercentageChangeFromBaselineValue().getFrom()).isEqualTo(-92.16d);
        softly.assertThat(filters.getPercentageChangeFromBaselineValue().getTo()).isEqualTo(1150d);

        softly.assertThat(filters.getDaysOnStudy().getFrom()).isEqualTo(-35);
        softly.assertThat(filters.getDaysOnStudy().getTo()).isEqualTo(781);

        softly.assertThat(truncLocalTime(filters.getMeasurementTimePoint().getFrom())).isInSameDayAs(toDate("2014-07-13"));
        softly.assertThat(truncLocalTime(filters.getMeasurementTimePoint().getTo())).isInSameDayAs(toDate("2016-11-05"));
    }

    @Test
    // Adopted from WhenRunningCardiacFilterRepositoryMergingITCase
    public void shouldGetAvailableFiltersWithMeasurementFilterMultiDataset() {
        CardiacFilters cardiacFilters = new CardiacFilters();
        cardiacFilters.setMeasurementName(new SetFilter<>(singletonList("QTcF - Fridericia's Correction Formula")));

        CardiacFilters filters = getFilters(MULTI_DUMMY_ACUITY_DATASETS, cardiacFilters);

        softly.assertThat(filters.getVisitNumber().getFrom()).isEqualTo(1d);
        softly.assertThat(filters.getVisitNumber().getTo()).isEqualTo(306d);

        softly.assertThat(filters.getBaselineFlag().getValues()).containsOnly("Y", "N");

        softly.assertThat(filters.getBaselineValue().getFrom()).isEqualTo(312d);
        softly.assertThat(filters.getBaselineValue().getTo()).isEqualTo(541d);

        softly.assertThat(filters.getChangeFromBaselineValue().getFrom()).isEqualTo(-220d);
        softly.assertThat(filters.getChangeFromBaselineValue().getTo()).isEqualTo(126d);

        softly.assertThat(filters.getPercentageChangeFromBaselineValue().getFrom()).isEqualTo(-40.67);
        softly.assertThat(filters.getPercentageChangeFromBaselineValue().getTo()).isEqualTo(36.63);

        softly.assertThat(filters.getDaysOnStudy().getFrom()).isEqualTo(-27);
        softly.assertThat(filters.getDaysOnStudy().getTo()).isEqualTo(781);

        softly.assertThat(truncLocalTime(filters.getMeasurementTimePoint().getFrom())).isInSameDayAs(toDate("2014-07-16"));
        softly.assertThat(truncLocalTime(filters.getMeasurementTimePoint().getTo())).isInSameDayAs(toDate("2016-11-05"));
    }

    @Test
    // Adopted from WhenRunningCardiacFilterRepositoryITCase
    public void shouldListMeasurementCategoriesSubsetFromValidFilter() {
        CardiacFilters cardiacFilters = new CardiacFilters();
        cardiacFilters.setMeasurementCategory(new SetFilter<>(singletonList("Ejection fraction")));
        CardiacFilters filters = getFilters(DUMMY_ACUITY_DATASETS, cardiacFilters);

        softly.assertThat(filters.getMeasurementCategory().getValues()).containsOnly("Ejection fraction");
        softly.assertThat(filters.getMatchedItemsCount()).isEqualTo(287);
    }

    @Test
    // Adopted from WhenRunningCardiacFilterRepositorySsvITCase
    public void shouldListMeasurementNameSubsetFromValidFilterCardiac() {
        CardiacFilters cardiacFilters = new CardiacFilters();
        cardiacFilters.setMeasurementName(new SetFilter<>(Arrays.asList("Summary (Mean) PR Duration", "Summary (Mean) QRS Duration")));
        CardiacFilters filters = getFilters(DUMMY_ACUITY_DATASETS, cardiacFilters);

        softly.assertThat(filters.getMeasurementName().getValues()).containsOnly("Summary (Mean) PR Duration", "Summary (Mean) QRS Duration");
        softly.assertThat(filters.getMatchedItemsCount()).isEqualTo(1853);
    }

    @Test
    // Adopted from WhenRunningCardiacFilterRepositoryITCase
    public void shouldReturnDaysOnStudySubsetFromValidQtcfFilter() {
        CardiacFilters cardiacFilters = new CardiacFilters();
        cardiacFilters.setDaysOnStudy((new RangeFilter<>(300, 400)));
        CardiacFilters filters = getFilters(DUMMY_ACUITY_DATASETS, cardiacFilters);

        softly.assertThat(filters.getDaysOnStudy().getFrom()).isGreaterThanOrEqualTo(307);
        softly.assertThat(filters.getDaysOnStudy().getTo()).isLessThanOrEqualTo(399);
        softly.assertThat(filters.getMatchedItemsCount()).isEqualTo(279);
    }

    @Test
    // Adopted from WhenRunningCardiacFilterRepositoryMergingITCase
    public void shouldReturnDaysOnStudySubsetFromValidQtcfFilterMultiDataset() {
        CardiacFilters cardiacFilters = new CardiacFilters();
        cardiacFilters.setDaysOnStudy((new RangeFilter<>(0, 98)));
        CardiacFilters filters = getFilters(MULTI_DUMMY_ACUITY_DATASETS, cardiacFilters);

        softly.assertThat(filters.getDaysOnStudy().getFrom()).isGreaterThanOrEqualTo(0);
        softly.assertThat(filters.getDaysOnStudy().getTo()).isLessThanOrEqualTo(98);
    }

    @Test
    // Adopted from WhenRunningCardiacFilterRepositoryITCase
    public void shouldReturnVisitNumbersSubsetFromValidQtcfFilter() {
        CardiacFilters cardiacFilters = new CardiacFilters();
        cardiacFilters.setVisitNumber((new RangeFilter<>(40d, 50d)));
        CardiacFilters filters = getFilters(DUMMY_ACUITY_DATASETS, cardiacFilters);

        softly.assertThat(filters.getVisitNumber().getFrom()).isGreaterThanOrEqualTo(41d);
        softly.assertThat(filters.getVisitNumber().getTo()).isLessThanOrEqualTo(50d);
        softly.assertThat(filters.getMatchedItemsCount()).isEqualTo(135);
    }

    @Test
    // Adopted from WhenRunningCardiacFilterRepositoryITCase
    public void shouldReturnResultValuesSubsetFromValidQtcfFilter() {
        CardiacFilters cardiacFilters = new CardiacFilters();
        cardiacFilters.setResultValue((new RangeFilter<>(0d, 500d)));
        CardiacFilters filters = getFilters(DUMMY_ACUITY_DATASETS, cardiacFilters);

        softly.assertThat(filters.getResultValue().getFrom()).isGreaterThanOrEqualTo(8d);
        softly.assertThat(filters.getResultValue().getTo()).isLessThanOrEqualTo(500d);
        softly.assertThat(filters.getMatchedItemsCount()).isEqualTo(3962);
    }

    @Test
    // Adopted from WhenRunningCardiacFilterRepositoryITCase
    public void shouldReturnBaselineValuesSubsetFromValidQtcfFilter() {
        CardiacFilters cardiacFilters = new CardiacFilters();
        cardiacFilters.setBaselineValue((new RangeFilter<>(0d, 100d)));
        CardiacFilters filters = getFilters(DUMMY_ACUITY_DATASETS, cardiacFilters);

        softly.assertThat(filters.getBaselineValue().getFrom()).isGreaterThanOrEqualTo(0d);
        softly.assertThat(filters.getBaselineValue().getTo()).isLessThanOrEqualTo(100d);
        softly.assertThat(filters.getMatchedItemsCount()).isEqualTo(1095);
    }

    @Test
    // Adopted from WhenRunningCardiacFilterRepositoryITCase
    public void shouldReturnChangeFromBaselineSubsetFromValidQtcfFilter() {
        CardiacFilters cardiacFilters = new CardiacFilters();
        cardiacFilters.setChangeFromBaselineValue((new RangeFilter<>(20d, 50d)));
        CardiacFilters filters = getFilters(DUMMY_ACUITY_DATASETS, cardiacFilters);

        softly.assertThat(filters.getChangeFromBaselineValue().getFrom()).isGreaterThanOrEqualTo(20d);
        softly.assertThat(filters.getChangeFromBaselineValue().getTo()).isLessThanOrEqualTo(50d);
        softly.assertThat(filters.getMatchedItemsCount()).isEqualTo(594);
    }

    @Test
    // Adopted from WhenRunningCardiacFilterRepositoryITCase
    public void shouldReturnAllPercentChangeFromBaselineFromValidQtcfFilter() {
        CardiacFilters cardiacFilters = new CardiacFilters();
        cardiacFilters.setPercentageChangeFromBaselineValue((new RangeFilter<>(0d, 50d)));
        CardiacFilters filters = getFilters(DUMMY_ACUITY_DATASETS, cardiacFilters);

        softly.assertThat(filters.getPercentageChangeFromBaselineValue().getFrom()).isGreaterThanOrEqualTo(0d);
        softly.assertThat(filters.getPercentageChangeFromBaselineValue().getTo()).isLessThanOrEqualTo(50d);
        softly.assertThat(filters.getMatchedItemsCount()).isEqualTo(3082);
    }

    @Test
    // Adopted from WhenRunningCardiacFilterRepositoryITCase
    public void shouldListAllStudyPeriods() {
        CardiacFilters filters = getFilters(DUMMY_ACUITY_DATASETS, CardiacFilters.empty());
        softly.assertThat(filters.getStudyPeriods().getValues()).containsNull();
    }

    @Test
    // Adopted from WhenRunningCardiacFilterRepositoryITCase
    public void shouldReturnMeasurementTimePointSubsetFromValidQtcfFilter() {
        CardiacFilters cardiacFilters = new CardiacFilters();
        cardiacFilters.setMeasurementTimePoint(new DateRangeFilter(toDate("2014-07-22"), toDate("2016-10-30")));
        CardiacFilters filters = getFilters(DUMMY_ACUITY_DATASETS, cardiacFilters);

        softly.assertThat(truncLocalTime(filters.getMeasurementTimePoint().getFrom())).isInSameDayAs("2014-07-22");
        softly.assertThat(truncLocalTime(filters.getMeasurementTimePoint().getTo())).isInSameDayAs("2016-10-30");

        softly.assertThat(filters.getMatchedItemsCount()).isEqualTo(4851);
    }

    @Test
    // Adopted from WhenRunningCardiacFilterRepositoryITCase
    public void shouldGetAvailableFiltersWithMeasurementFilter() {
        CardiacFilters cardiacFilters = new CardiacFilters();
        cardiacFilters.setMeasurementName(new SetFilter<>(singletonList("QTcF - Fridericia's Correction Formula")));
        CardiacFilters filters = getFilters(DUMMY_ACUITY_DATASETS, cardiacFilters);

        softly.assertThat(filters.getMeasurementName().getValues()).containsOnly("QTcF - Fridericia's Correction Formula");
        softly.assertThat(filters.getBaselineFlag().getValues()).containsOnly("Y", "N");

        softly.assertThat(filters.getDaysOnStudy().getFrom()).isEqualTo(-27);
        softly.assertThat(filters.getDaysOnStudy().getTo()).isEqualTo(781);

        softly.assertThat(filters.getVisitNumber().getFrom()).isEqualTo(1d);
        softly.assertThat(filters.getVisitNumber().getTo()).isEqualTo(306d);

        softly.assertThat(filters.getBaselineValue().getFrom()).isEqualTo(312d);
        softly.assertThat(filters.getBaselineValue().getTo()).isEqualTo(541d);

        softly.assertThat(filters.getChangeFromBaselineValue().getFrom()).isEqualTo(-220d);
        softly.assertThat(filters.getChangeFromBaselineValue().getTo()).isEqualTo(126d);

        softly.assertThat(filters.getPercentageChangeFromBaselineValue().getFrom()).isEqualTo(-40.67);
        softly.assertThat(filters.getPercentageChangeFromBaselineValue().getTo()).isEqualTo(36.63);

        softly.assertThat(truncLocalTime(filters.getMeasurementTimePoint().getFrom())).isInSameDayAs("2014-07-16");
        softly.assertThat(truncLocalTime(filters.getMeasurementTimePoint().getTo())).isInSameDayAs("2016-11-05T23:59:59.999");
    }

    @Test
    // Adopted from WhenRunningCardiacFilterRepositoryITCase
    public void shouldGetAvailableFiltersWithBslFilter() {
        CardiacFilters cardiacFilters = new CardiacFilters();
        cardiacFilters.setBaselineFlag((new SetFilter<>(singletonList("N"))));
        CardiacFilters filters = getFilters(DUMMY_ACUITY_DATASETS, cardiacFilters);

        softly.assertThat(filters.getBaselineFlag().getValues()).containsOnly("N");
    }

    @Test
    // Adopted from WhenRunningCardiacFilterRepositoryITCase
    public void shouldReturnClinicallySignificantFromValidQtcfFilter() {
        CardiacFilters cardiacFilters = new CardiacFilters();
        cardiacFilters.setClinicallySignificant((new SetFilter<>(singletonList("Yes"))));
        CardiacFilters filters = getFilters(DUMMY_ACUITY_DATASETS, cardiacFilters);

        softly.assertThat(filters.getClinicallySignificant().getValues()).containsOnly("Yes");
        softly.assertThat(filters.getMatchedItemsCount()).isEqualTo(10);
    }

    @Test
    // Adopted from WhenRunningCardiacFilterRepositoryITCase
    public void shouldReturnAllClinicallySignificantFromEmptyQtcfFilter() {
        CardiacFilters filters = getFilters(DUMMY_ACUITY_DATASETS, CardiacFilters.empty());

        softly.assertThat(filters.getClinicallySignificant().getValues()).containsOnly(null, "No", "Yes");
        softly.assertThat(filters.getMatchedItemsCount()).isEqualTo(4867);
    }

    private CardiacFilters getFilters(Datasets datasets, CardiacFilters eventFilters) {
        Collection<Cardiac> events = cardiacsDataProvider.loadData(datasets);
        Collection<Subject> subjects = populationDataProvider.loadData(datasets);
        return (CardiacFilters) cardiacFilterService.getAvailableFilters(events, eventFilters, subjects, PopulationFilters.empty());
    }
}
