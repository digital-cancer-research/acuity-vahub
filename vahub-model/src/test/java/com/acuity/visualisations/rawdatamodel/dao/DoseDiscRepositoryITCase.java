package com.acuity.visualisations.rawdatamodel.dao;

import com.acuity.visualisations.config.annotation.TransactionalOracleITTest;
import com.acuity.visualisations.config.config.ApplicationModelConfigITCase;
import com.acuity.visualisations.rawdatamodel.util.DaysUtil;
import com.acuity.visualisations.rawdatamodel.vo.DoseDiscRaw;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_VA_ID;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {ApplicationModelConfigITCase.class})
@TransactionalOracleITTest
public class DoseDiscRepositoryITCase {

    @Autowired
    private DoseDiscRepository doseDiscRepository;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test
    public void shouldGetDoseDiscByDatasetIdWithCorrectValue() {
        List<DoseDiscRaw> events = doseDiscRepository.getRawData(DUMMY_ACUITY_VA_ID);
        softly.assertThat(events.size()).isEqualTo(111);

        DoseDiscRaw doseDisc = events.stream().filter(t -> t.getId().equals("2ffa49a6075c45a9af10adec7420443a")).findFirst().get();

        softly.assertThat(doseDisc.getSubjectId()).isEqualTo("99ebd3cc7adb42bd82309ab3e96992d1");
        softly.assertThat(doseDisc.getStudyDrug()).isEqualTo("AZD1234");
        softly.assertThat(DaysUtil.truncLocalTime(doseDisc.getDiscDate())).isInSameDayAs("2015-05-02");
        softly.assertThat(doseDisc.getDiscReason()).isEqualTo("Condition Under Investigation Worsened");
    }
}
