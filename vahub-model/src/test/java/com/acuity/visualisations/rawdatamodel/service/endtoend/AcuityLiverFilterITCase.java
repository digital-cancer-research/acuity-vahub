package com.acuity.visualisations.rawdatamodel.service.endtoend;

import com.acuity.visualisations.config.annotation.TransactionalOracleITTest;
import com.acuity.visualisations.config.config.ApplicationModelConfigITCase;
import com.acuity.visualisations.rawdatamodel.dataproviders.LiverDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.PopulationDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.filters.DateRangeFilter;
import com.acuity.visualisations.rawdatamodel.filters.LiverFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.filters.RangeFilter;
import com.acuity.visualisations.rawdatamodel.filters.SetFilter;
import com.acuity.visualisations.rawdatamodel.service.filters.LiverFilterService;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Liver;
import com.acuity.va.security.acl.domain.Datasets;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collection;
import java.util.Collections;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_DATASETS;
import static com.acuity.visualisations.config.util.TestConstants.MULTI_DUMMY_ACUITY_DATASETS;
import static com.acuity.visualisations.rawdatamodel.util.DaysUtil.toDate;
import static com.acuity.visualisations.rawdatamodel.util.DaysUtil.truncLocalTime;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {ApplicationModelConfigITCase.class})
@TransactionalOracleITTest
public class AcuityLiverFilterITCase {

    @Autowired
    private LiverFilterService liverFilterService;

    @Autowired
    private LiverDatasetsDataProvider liversDataProvider;

    @Autowired
    private PopulationDatasetsDataProvider populationDataProvider;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test
    // Adopted from WhenRunningLiverFilterRepositoryITCase, WhenRunningLiverFilterServiceITCase
    public void getAvailableFilters() {
        LiverFilters filters = getFilters(DUMMY_ACUITY_DATASETS, LiverFilters.empty());
        softly.assertThat(filters.getMatchedItemsCount()).isEqualTo(6005);

        softly.assertThat(filters.getBaselineFlag().getValues()).containsOnly("Y", "N");

        softly.assertThat(filters.getDaysOnStudy().getFrom()).isEqualTo(-28);
        softly.assertThat(filters.getDaysOnStudy().getTo()).isEqualTo(747);
        //FIXME
        softly.assertThat(truncLocalTime(filters.getMeasurementTimePoint().getFrom())).isInSameDayAs(toDate("2014-07-22"));
        softly.assertThat(truncLocalTime(filters.getMeasurementTimePoint().getTo())).isInSameDayAs(toDate("2016-12-06"));

        softly.assertThat(filters.getVisitNumber().getFrom()).isEqualTo(1);
        softly.assertThat(filters.getVisitNumber().getTo()).isEqualTo(310);

        softly.assertThat(filters.getLabValue().getFrom()).isEqualTo(0);
        softly.assertThat(filters.getLabValue().getTo()).isEqualTo(2007);

        softly.assertThat(filters.getBaselineValue().getFrom()).isEqualTo(0.13);
        softly.assertThat(filters.getBaselineValue().getTo()).isEqualTo(1045);

        softly.assertThat(filters.getChangeFromBaselineValue().getFrom()).isEqualTo(-748);
        softly.assertThat(filters.getChangeFromBaselineValue().getTo()).isEqualTo(1072);

        softly.assertThat(filters.getPercentageChangeFromBaselineValue().getFrom()).isEqualTo(-100);
        softly.assertThat(filters.getPercentageChangeFromBaselineValue().getTo()).isEqualTo(1575.68);

        softly.assertThat(filters.getRefRangeNormValue().getFrom()).isEqualTo(-0.71);
        softly.assertThat(filters.getRefRangeNormValue().getTo()).isEqualTo(30.5);

        softly.assertThat(filters.getUpperRefValue().getFrom()).isEqualTo(0.57);
        softly.assertThat(filters.getUpperRefValue().getTo()).isEqualTo(306.0);

        softly.assertThat(filters.getLowerRefValue().getFrom()).isEqualTo(0);
        softly.assertThat(filters.getLowerRefValue().getTo()).isEqualTo(100);

        softly.assertThat(filters.getLabValueOverUpperRefValue().getFrom()).isEqualTo(0);
        softly.assertThat(filters.getLabValueOverUpperRefValue().getTo()).isEqualTo(20.67);

        softly.assertThat(filters.getLabValueOverLowerRefValue().getFrom()).isEqualTo(0.0);
        softly.assertThat(filters.getLabValueOverLowerRefValue().getTo()).isEqualTo(72);
    }

    @Test
    // Adopted from WhenRunningLiverFilterRepositoryITCase
    public void shouldListBaselineFlagsFromValidLiverFilter() {
        LiverFilters liverFilters = new LiverFilters();
        liverFilters.setBaselineFlag(new SetFilter<>(Collections.singleton("N"), false));
        LiverFilters filters = getFilters(DUMMY_ACUITY_DATASETS, liverFilters);

        softly.assertThat(filters.getMatchedItemsCount()).isEqualTo(5526);
        softly.assertThat(filters.getBaselineFlag().getValues()).containsOnly("N");
    }

    @Test
    // Adopted from WhenRunningLiverFilterRepositoryITCase
    public void shouldFindAllMinMaxDaysOnStudyFromValidLiverFilterIncludeEmpty() {
        LiverFilters liverFilters = new LiverFilters();
        liverFilters.setDaysOnStudy(new RangeFilter<>(0, 100));
        LiverFilters filters = getFilters(DUMMY_ACUITY_DATASETS, liverFilters);

        softly.assertThat(filters.getMatchedItemsCount()).isEqualTo(2438);
        softly.assertThat(filters.getDaysOnStudy().getFrom()).isEqualTo(0);
        softly.assertThat(filters.getDaysOnStudy().getTo()).isEqualTo(100);
    }

    @Test
    // Adopted from WhenRunningLiverFilterRepositoryITCase
    public void shouldFindAllMinMaxVisitNumberValidLiverFilterIncludeEmpty() {
        LiverFilters liverFilters = new LiverFilters();
        liverFilters.setVisitNumber(new RangeFilter<>(2d, 5d));
        LiverFilters filters = getFilters(DUMMY_ACUITY_DATASETS, liverFilters);

        softly.assertThat(filters.getMatchedItemsCount()).isEqualTo(850);
        softly.assertThat(filters.getVisitNumber().getFrom()).isEqualTo(2);
        softly.assertThat(filters.getVisitNumber().getTo()).isEqualTo(5);
    }

    @Test
    // Adopted from WhenRunningLiverFilterRepositoryITCase
    public void shouldFindAllMinMaxLabValueValidLiverFilterIncludeEmpty() {
        LiverFilters liverFilters = new LiverFilters();
        liverFilters.setLabValue(new RangeFilter<>(5d, 5d));
        LiverFilters filters = getFilters(DUMMY_ACUITY_DATASETS, liverFilters);

        softly.assertThat(filters.getMatchedItemsCount()).isEqualTo(73);
        softly.assertThat(filters.getLabValue().getFrom()).isEqualTo(5);
        softly.assertThat(filters.getLabValue().getTo()).isEqualTo(5);
    }

    @Test
    // Adopted from WhenRunningLiverFilterRepositoryITCase
    public void shouldListBaselineValueFromValidLiverFilter() {
        LiverFilters liverFilters = new LiverFilters();
        liverFilters.setBaselineValue(new RangeFilter<>(20d, 30d));
        LiverFilters filters = getFilters(DUMMY_ACUITY_DATASETS, liverFilters);

        softly.assertThat(filters.getMatchedItemsCount()).isEqualTo(848);
        softly.assertThat(filters.getBaselineValue().getFrom()).isEqualTo(21);
        softly.assertThat(filters.getBaselineValue().getTo()).isEqualTo(30);
    }

    @Test
    // Adopted from WhenRunningLiverFilterRepositoryITCase
    public void shouldListChangeFromBaselineFromValidLiverFilter() {
        LiverFilters liverFilters = new LiverFilters();
        liverFilters.setChangeFromBaselineValue(new RangeFilter<>(20d, 30d));
        LiverFilters filters = getFilters(DUMMY_ACUITY_DATASETS, liverFilters);

        softly.assertThat(filters.getMatchedItemsCount()).isEqualTo(172);
        softly.assertThat(filters.getChangeFromBaselineValue().getFrom()).isEqualTo(20);
        softly.assertThat(filters.getChangeFromBaselineValue().getTo()).isEqualTo(30);
    }

    @Test
    // Adopted from WhenRunningLiverFilterRepositoryITCase
    public void shouldListPercentChangeFromBaselineFromValidLiverFilter() {
        LiverFilters liverFilters = new LiverFilters();
        liverFilters.setPercentageChangeFromBaselineValue(new RangeFilter<>(20d, 30d));
        LiverFilters filters = getFilters(DUMMY_ACUITY_DATASETS, liverFilters);

        softly.assertThat(filters.getMatchedItemsCount()).isEqualTo(326);
        softly.assertThat(filters.getPercentageChangeFromBaselineValue().getFrom()).isEqualTo(20);
        softly.assertThat(filters.getPercentageChangeFromBaselineValue().getTo()).isEqualTo(30);
    }

    @Test
    // Adopted from WhenRunningLiverFilterRepositoryITCase
    public void shouldListRefRangeNormValueFromValidLiverFilter() {
        LiverFilters liverFilters = new LiverFilters();
        liverFilters.setRefRangeNormValue(new RangeFilter<>(0d, 6d));
        LiverFilters filters = getFilters(DUMMY_ACUITY_DATASETS, liverFilters);

        softly.assertThat(filters.getMatchedItemsCount()).isEqualTo(5796);
        softly.assertThat(filters.getRefRangeNormValue().getFrom()).isEqualTo(0);
        softly.assertThat(filters.getRefRangeNormValue().getTo()).isEqualTo(5.96);
    }

    @Test
    // Adopted from WhenRunningLiverFilterRepositoryITCase
    public void shouldListLabValueOverUpperRefValueFromValidLiverFilter() {
        LiverFilters liverFilters = new LiverFilters();
        liverFilters.setLabValueOverUpperRefValue(new RangeFilter<>(3d, 5d));
        LiverFilters filters = getFilters(DUMMY_ACUITY_DATASETS, liverFilters);

        softly.assertThat(filters.getMatchedItemsCount()).isEqualTo(74);
        softly.assertThat(filters.getLabValueOverUpperRefValue().getFrom()).isEqualTo(3.03);
        softly.assertThat(filters.getLabValueOverUpperRefValue().getTo()).isEqualTo(5);
    }

    @Test
    // Adopted from WhenRunningLiverFilterRepositoryITCase
    public void shouldListLabValueOverLowerRefValueFromValidLiverFilter() {
        LiverFilters liverFilters = new LiverFilters();
        liverFilters.setLabValueOverLowerRefValue(new RangeFilter<>(10d, 15d));
        LiverFilters filters = getFilters(DUMMY_ACUITY_DATASETS, liverFilters);

        softly.assertThat(filters.getMatchedItemsCount()).isEqualTo(139);
        softly.assertThat(filters.getLabValueOverLowerRefValue().getFrom()).isEqualTo(10);
        softly.assertThat(filters.getLabValueOverLowerRefValue().getTo()).isEqualTo(15);
    }

    @Test
    // Adopted from WhenRunningLiverFilterRepositoryITCase
    public void shouldListUpperRefValueFromValidLiverFilter() {
        LiverFilters liverFilters = new LiverFilters();
        liverFilters.setUpperRefValue(new RangeFilter<>(10d, 40d));
        LiverFilters filters = getFilters(DUMMY_ACUITY_DATASETS, liverFilters);

        softly.assertThat(filters.getMatchedItemsCount()).isEqualTo(2799);
        softly.assertThat(filters.getUpperRefValue().getFrom()).isEqualTo(10);
        softly.assertThat(filters.getUpperRefValue().getTo()).isEqualTo(40);
    }

    @Test
    // Adopted from WhenRunningLiverFilterRepositoryITCase
    public void shouldListLowerRefValueFromValidLiverFilter() {
        LiverFilters liverFilters = new LiverFilters();
        liverFilters.setLowerRefValue(new RangeFilter<>(10d, 15d));
        LiverFilters filters = getFilters(DUMMY_ACUITY_DATASETS, liverFilters);

        softly.assertThat(filters.getMatchedItemsCount()).isEqualTo(573);
        softly.assertThat(filters.getLowerRefValue().getFrom()).isEqualTo(10);
        softly.assertThat(filters.getLowerRefValue().getTo()).isEqualTo(15);
    }

    @Test
    // Adopted from WhenRunningLiverFilterRepositoryITCase
    public void shouldFindAllMinMaxMeasurementTimePointFromValidLiverFilterIncludeEmpty() {
        LiverFilters liverFilters = new LiverFilters();

        liverFilters.setMeasurementTimePoint(new DateRangeFilter(toDate("2014-06-25"), toDate("2016-12-07"), false));
        LiverFilters filters = getFilters(DUMMY_ACUITY_DATASETS, liverFilters);

        softly.assertThat(filters.getMatchedItemsCount()).isEqualTo(6005);
        softly.assertThat(truncLocalTime(filters.getMeasurementTimePoint().getFrom()))
                .isInSameDayAs(toDate("2014-07-22"));
        softly.assertThat(truncLocalTime(filters.getMeasurementTimePoint().getTo()))
                .isInSameDayAs(toDate("2016-12-06"));
    }

    @Test
    // Adopted from WhenRunningLiverFilterServiceMergedITCase
    public void multiStudyTest() {
        LiverFilters filters = getFilters(MULTI_DUMMY_ACUITY_DATASETS, LiverFilters.empty());

        softly.assertThat(filters.getMatchedItemsCount()).isEqualTo(6068);
        softly.assertThat(filters.getBaselineValue().getFrom()).isEqualTo(0.13);
        softly.assertThat(filters.getBaselineValue().getTo()).isEqualTo(1045);
        softly.assertThat(filters.getPercentageChangeFromBaselineValue().getFrom()).isEqualTo(-100);
        softly.assertThat(filters.getPercentageChangeFromBaselineValue().getTo()).isEqualTo(1575.68);
    }

    private LiverFilters getFilters(Datasets datasets, LiverFilters eventFilters) {
        Collection<Liver> events = liversDataProvider.loadData(datasets);
        Collection<Subject> subjects = populationDataProvider.loadData(datasets);
        return (LiverFilters) liverFilterService.getAvailableFilters(events, eventFilters, subjects, PopulationFilters.empty());
    }
}
