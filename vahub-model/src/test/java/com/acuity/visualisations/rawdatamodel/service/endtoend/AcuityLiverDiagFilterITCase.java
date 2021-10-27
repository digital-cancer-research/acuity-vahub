package com.acuity.visualisations.rawdatamodel.service.endtoend;

import com.acuity.visualisations.config.annotation.TransactionalOracleITTest;
import com.acuity.visualisations.config.config.ApplicationModelConfigITCase;
import com.acuity.visualisations.rawdatamodel.dataproviders.LiverDiagDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.PopulationDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.filters.LiverDiagFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.filters.SetFilter;
import com.acuity.visualisations.rawdatamodel.service.filters.LiverDiagFilterService;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.LiverDiag;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collection;
import java.util.Collections;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_LIVER_DATASETS;
import static com.acuity.visualisations.rawdatamodel.util.DaysUtil.toDate;
import static com.acuity.visualisations.rawdatamodel.util.DaysUtil.truncLocalTime;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {ApplicationModelConfigITCase.class})
@TransactionalOracleITTest
// TODO this test requires data in dataset
@Ignore
public class AcuityLiverDiagFilterITCase {

    @Autowired
    private LiverDiagFilterService liverDiagFilterService;
    @Autowired
    private LiverDiagDatasetsDataProvider liverDiagDatasetsDataProvider;
    @Autowired
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;
    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test
    public void shouldListAllFromEmptyFilter() {
        Collection<LiverDiag> events = liverDiagDatasetsDataProvider.loadData(DUMMY_LIVER_DATASETS);
        Collection<Subject> subjects = populationDatasetsDataProvider.loadData(DUMMY_LIVER_DATASETS);

        LiverDiagFilters filters = (LiverDiagFilters) liverDiagFilterService.getAvailableFilters(events, LiverDiagFilters.empty(),
                subjects, PopulationFilters.empty());

        softly.assertThat(filters.getMatchedItemsCount()).isEqualTo(13);

        softly.assertThat(filters.getLiverDiagInv().getValues()).containsOnly("Liver biopsy", "MRI/MRCP", "Serology for hepatitis C",
                "Tox screening for ethanol", "Tox screening, other", "Ultrasound", "X-Ray", "Serology for hepatitis E");
        softly.assertThat(filters.getLiverDiagInvSpec().getValues()).containsOnly(null, "Anti-HEV IgM", "Anti-HCV", "Bile Cytology");
        softly.assertThat(truncLocalTime(filters.getLiverDiagInvDate().getFrom())).isInSameDayAs(toDate("2017-01-03"));
        softly.assertThat(truncLocalTime(filters.getLiverDiagInvDate().getTo())).isInSameDayAs(toDate("2017-03-22"));
        softly.assertThat(filters.getStudyDayLiverDiagInv().getFrom()).isEqualTo(-31);
        softly.assertThat(filters.getStudyDayLiverDiagInv().getTo()).isEqualTo(59);
        softly.assertThat(filters.getLiverDiagInvResult().getValues()).containsOnly(null, "stone", "biliary obstruction",
                "Small cyst", "Negative", "atypical cells");
        softly.assertThat(filters.getPotentialHysLawCaseNum().getFrom()).isEqualTo(1);
        softly.assertThat(filters.getPotentialHysLawCaseNum().getTo()).isEqualTo(2);
    }

    @Test
    public void shouldGetCorrectFilteredValues() {
        Collection<LiverDiag> events = liverDiagDatasetsDataProvider.loadData(DUMMY_LIVER_DATASETS);
        Collection<Subject> subjects = populationDatasetsDataProvider.loadData(DUMMY_LIVER_DATASETS);

        PopulationFilters populationFilters = new PopulationFilters();
        populationFilters.setSubjectId(new SetFilter<>(Collections.singleton("E00000003")));
        LiverDiagFilters liverDiagFilters = LiverDiagFilters.empty();
        liverDiagFilters.setLiverDiagInvResult(new SetFilter<>(Collections.singleton("Negative")));

        LiverDiagFilters filters = (LiverDiagFilters) liverDiagFilterService.getAvailableFilters(events, liverDiagFilters, subjects, populationFilters);

        softly.assertThat(filters.getMatchedItemsCount()).isEqualTo(1);

        softly.assertThat(filters.getLiverDiagInv().getValues()).containsOnly("Serology for hepatitis C");
        softly.assertThat(filters.getLiverDiagInvSpec().getValues()).containsOnly("Anti-HCV");
        softly.assertThat(truncLocalTime(filters.getLiverDiagInvDate().getFrom())).isInSameDayAs(toDate("2017-03-19"));
        softly.assertThat(truncLocalTime(filters.getLiverDiagInvDate().getTo())).isInSameDayAs(toDate("2017-03-19"));
        softly.assertThat(filters.getStudyDayLiverDiagInv().getFrom()).isEqualTo(59);
        softly.assertThat(filters.getStudyDayLiverDiagInv().getTo()).isEqualTo(59);
        softly.assertThat(filters.getLiverDiagInvResult().getValues()).containsOnly("Negative");
        softly.assertThat(filters.getPotentialHysLawCaseNum().getFrom()).isNull();
        softly.assertThat(filters.getPotentialHysLawCaseNum().getTo()).isNull();
    }
}
