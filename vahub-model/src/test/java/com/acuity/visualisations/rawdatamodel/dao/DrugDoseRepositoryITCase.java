package com.acuity.visualisations.rawdatamodel.dao;

import com.acuity.visualisations.config.annotation.TransactionalOracleITTest;
import com.acuity.visualisations.config.config.ApplicationModelConfigITCase;
import com.acuity.visualisations.rawdatamodel.util.DaysUtil;
import com.acuity.visualisations.rawdatamodel.vo.DrugDoseRaw;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_VA_ID;
import static com.acuity.visualisations.rawdatamodel.util.DateUtils.toDateTime;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {ApplicationModelConfigITCase.class})
@TransactionalOracleITTest
public class DrugDoseRepositoryITCase {
    @Autowired
    private DrugDoseRepository drugDoseRepository;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test
    public void shouldGetDosesByDatasetIdWithCorrectValue() {
        List<DrugDoseRaw> doses = drugDoseRepository.getRawData(DUMMY_ACUITY_VA_ID);
        softly.assertThat(doses.size()).isEqualTo(799);

        DrugDoseRaw dose = doses.stream().filter(t -> t.getId().equals("323ee81715f3447099d0197dec43aefb")).findFirst().get();

        softly.assertThat(dose.getSubjectId()).isEqualTo("3824bea4759042d3b9cbcb9fa45f6b28");
        softly.assertThat(dose.getDrug()).isEqualTo("AZD1234");
        softly.assertThat(dose.getDose()).isEqualTo(60.0);
        softly.assertThat(DaysUtil.truncLocalTime(dose.getStartDate())).isInSameDayAs(DaysUtil.truncLocalTime(toDateTime("2015-11-14T00:00:00")));
        softly.assertThat(DaysUtil.truncLocalTime(dose.getEndDate())).isInSameDayAs(DaysUtil.truncLocalTime(toDateTime("2015-11-14T00:00:00")));
        softly.assertThat(dose.getFrequency()).isEqualTo(2);
        softly.assertThat(dose.getFrequencyName()).isEqualTo("BID");
        softly.assertThat(dose.getActionTaken()).isEqualTo("Dose Not Changed");
        softly.assertThat(dose.getReasonForActionTaken()).isNull();
        softly.assertThat(dose.getDoseUnit()).isEqualTo("mg");
        softly.assertThat(dose.getStudyDrugCategory()).isNull();
        softly.assertThat(dose.getTotalDailyDose()).isNull();
        softly.assertThat(dose.getPlannedDose()).isNull();
        softly.assertThat(dose.getPlannedDoseUnits()).isNull();
        softly.assertThat(dose.getPlannedNoDaysTreatment()).isNull();
        softly.assertThat(dose.getFormulation()).isNull();
        softly.assertThat(dose.getRoute()).isNull();
        softly.assertThat(dose.getReasonForActionTaken()).isNull();
        softly.assertThat(dose.getMainReasonForActionTakenSpec()).isNull();
        softly.assertThat(dose.getAeNumCausedActionTaken()).isEmpty();
        softly.assertThat(dose.getAePtCausedActionTaken()).isEmpty();
        softly.assertThat(dose.getReasonForTherapy()).isNull();
        softly.assertThat(dose.getTreatmentCycleDelayed()).isNull();
        softly.assertThat(dose.getReasonTreatmentCycleDelayed()).isNull();
        softly.assertThat(dose.getReasonTreatmentCycleDelayedOther()).isNull();
        softly.assertThat(dose.getAeNumCausedTreatmentCycleDelayed()).isEmpty();
        softly.assertThat(dose.getAePtCausedTreatmentCycleDelayed()).isEmpty();
        softly.assertThat(dose.getMedicationCode()).isNull();
        softly.assertThat(dose.getMedicationDictionaryText()).isNull();
        softly.assertThat(dose.getAtcCode()).isNull();
        softly.assertThat(dose.getAtcDictionaryText()).isNull();
        softly.assertThat(dose.getMedicationPt()).isNull();
        softly.assertThat(dose.getMedicationGroupingName()).isNull();
        softly.assertThat(dose.getActiveIngredient()).isNull();
    }

}
