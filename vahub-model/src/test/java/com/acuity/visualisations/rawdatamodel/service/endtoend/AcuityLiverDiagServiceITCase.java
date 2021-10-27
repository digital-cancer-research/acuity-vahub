package com.acuity.visualisations.rawdatamodel.service.endtoend;

import com.acuity.visualisations.config.annotation.TransactionalOracleITTest;
import com.acuity.visualisations.config.config.ApplicationModelConfigITCase;
import com.acuity.visualisations.rawdatamodel.dataproviders.LiverDiagDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.PopulationDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.filters.LiverDiagFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.filters.SetFilter;
import com.acuity.visualisations.rawdatamodel.service.event.LiverDiagService;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_LIVER_DATASETS;
import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.groups.Tuple.tuple;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ApplicationModelConfigITCase.class})
@TransactionalOracleITTest
// TODO this test requires data in dataset
@Ignore
public class AcuityLiverDiagServiceITCase {

    @Autowired
    private LiverDiagService liverDiagService;
    @Autowired
    private LiverDiagDatasetsDataProvider liverDiagDatasetsDataProvider;
    @Autowired
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;
    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test
    public void shouldReturnListOfSubjectsForEmptyFilter() {
        List<String> subjects = liverDiagService.getSubjects(DUMMY_LIVER_DATASETS,
                LiverDiagFilters.empty(), PopulationFilters.empty());
        softly.assertThat(subjects).hasSize(5);
    }

    @Test
    public void shouldGetDetailsOnDemandData() {
        List<Map<String, String>> result = liverDiagService.getDetailsOnDemandData(DUMMY_LIVER_DATASETS,
                "c4916fcbf40b42f488aba8b50f56ba85", LiverDiagFilters.empty());

        softly.assertThat(result)
                .hasSize(3)
                .extracting("studyId", "studyPart", "subjectId",
                        "liverDiagInv", "liverDiagInvSpec", "liverDiagInvDate", "studyDayLiverDiagInv", "liverDiagInvResult", "potentialHysLawCaseNum")
                .contains(
                        tuple("liver", "A", "E00000002", "Ultrasound", null, "2017-02-16T12:00:00", 28, null, null),
                        tuple("liver", "A", "E00000002", "Ultrasound", null, "2017-02-21T12:00:00", 33, null, null),
                        tuple("liver", "A", "E00000002", "Serology for hepatitis E", "Anti-HEV IgM", "2017-02-21T12:00:00", 33, "Negative", null));
    }

    @Test
    public void shouldGetFilteredDetailsOnDemandData() {
        LiverDiagFilters filters = new LiverDiagFilters();
        filters.setLiverDiagInv(new SetFilter<>(newArrayList("Serology for hepatitis C")));
        List<Map<String, String>> result = liverDiagService.getDetailsOnDemandData(DUMMY_LIVER_DATASETS,
                "43b744ae2fdc43638b3ab17d129149da", filters);

        softly.assertThat(result)
                .hasSize(1)
                .extracting("studyId", "studyPart", "subjectId",
                        "liverDiagInv", "liverDiagInvSpec", "liverDiagInvDate", "studyDayLiverDiagInv", "liverDiagInvResult", "potentialHysLawCaseNum")
                .contains(
                        tuple("liver", "A", "E00000003", "Serology for hepatitis C", "Anti-HCV", "2017-03-19T12:00:00", 59, "Negative", null));
    }
}
