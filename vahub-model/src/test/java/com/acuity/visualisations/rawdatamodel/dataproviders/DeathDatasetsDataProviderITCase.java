package com.acuity.visualisations.rawdatamodel.dataproviders;

import com.acuity.visualisations.config.annotation.TransactionalOracleITTest;
import com.acuity.visualisations.config.config.ApplicationModelConfigITCase;
import com.acuity.visualisations.rawdatamodel.util.DaysUtil;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Death;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_2_ACUITY_DATASETS;

@TransactionalOracleITTest
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ApplicationModelConfigITCase.class})
public class DeathDatasetsDataProviderITCase {
    @Autowired
    private DeathDatasetsDataProvider deathDatasetsDataProvider;
    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test
    public void testLoadDataFromAcuityUploadedFile() {

        Collection<Death> result = deathDatasetsDataProvider.loadData(DUMMY_2_ACUITY_DATASETS);

        softly.assertThat(result).hasSize(46);

        List<Death> deathsPerSubject = result.stream()
                .filter(e -> "305327754c3244699c727cac7287f8c2".equals(e.getSubjectId()))
                .collect(Collectors.toList());

        softly.assertThat(deathsPerSubject.size()).isEqualTo(2);

        Death death = deathsPerSubject.stream()
                .filter(e -> "6c2e1cdf88bb42afa28dbac841fa13e7".equals(e.getId()))
                .findFirst().get();

        softly.assertThat(death.getEvent().getDeathCause()).isEqualTo("ARTHRALGIA AGGRAVATED");
        softly.assertThat(DaysUtil.truncLocalTime(death.getEvent().getDateOfDeath())).isInSameDayAs("2015-06-27");
        softly.assertThat(death.getEvent().getAutopsyPerformed()).isNull();
        softly.assertThat(death.getEvent().getDesignation()).isEqualTo("Secondary");
        softly.assertThat(death.getEvent().getDiseaseUnderInvestigationDeath()).isNull();
        softly.assertThat(death.getEvent().getHlt()).isNull();
        softly.assertThat(death.getEvent().getLlt()).isNull();
        softly.assertThat(death.getEvent().getPreferredTerm()).isNull();
        softly.assertThat(death.getEvent().getSoc()).isNull();
    }
}
