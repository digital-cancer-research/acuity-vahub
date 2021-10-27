package com.acuity.visualisations.rawdatamodel.vo.wrappers;

import com.acuity.visualisations.rawdatamodel.util.Constants;
import com.acuity.visualisations.rawdatamodel.vo.ConmedRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import org.apache.commons.lang3.time.DateUtils;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;

import java.util.Date;

import static com.acuity.visualisations.rawdatamodel.util.Attributes.DEFAULT_EMPTY_VALUE;

public class ConmedTest {

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test
    public void shouldProvideEmptyDatePriorToFieldsForNullBasicValues() {
        Conmed conmed = new Conmed(
                ConmedRaw.builder().build(),
                Subject.builder().build()
        );

        softly.assertThat(conmed.getConmedStartPriorToRandomisation()).isNull();
        softly.assertThat(conmed.getConmedEndPriorToRandomisation()).isNull();
        softly.assertThat(conmed.getConmedStartedPriorToStudy()).isEqualTo(DEFAULT_EMPTY_VALUE);
        softly.assertThat(conmed.getConmedEndedPriorToStudy()).isEqualTo(DEFAULT_EMPTY_VALUE);

        conmed = new Conmed(
                ConmedRaw.builder().build(),
                Subject.builder()
                        .dateOfRandomisation(new Date())
                        .firstTreatmentDate(new Date())
                        .build()
        );

        softly.assertThat(conmed.getConmedStartPriorToRandomisation()).isNull();
        softly.assertThat(conmed.getConmedEndPriorToRandomisation()).isNull();
        softly.assertThat(conmed.getConmedStartedPriorToStudy()).isEqualTo(DEFAULT_EMPTY_VALUE);
        softly.assertThat(conmed.getConmedEndedPriorToStudy()).isEqualTo(DEFAULT_EMPTY_VALUE);

        conmed = new Conmed(
                ConmedRaw.builder()
                        .startDate(new Date())
                        .endDate(new Date())
                        .build(),
                Subject.builder().build()
        );

        softly.assertThat(conmed.getConmedStartPriorToRandomisation()).isNull();
        softly.assertThat(conmed.getConmedEndPriorToRandomisation()).isNull();
        softly.assertThat(conmed.getConmedStartedPriorToStudy()).isEqualTo(DEFAULT_EMPTY_VALUE);
        softly.assertThat(conmed.getConmedEndedPriorToStudy()).isEqualTo(DEFAULT_EMPTY_VALUE);
    }

    @Test
    public void shouldProvideNonEmptyDatePriorToFieldsForFilledBasicValues() {
        Conmed conmed = new Conmed(
                ConmedRaw.builder()
                        .startDate(new Date())
                        .endDate(new Date())
                        .build(),
                Subject.builder()
                        .dateOfRandomisation(DateUtils.addDays(new Date(), 1))
                        .firstTreatmentDate(DateUtils.addDays(new Date(), 1))
                        .build()
        );

        softly.assertThat(conmed.getConmedStartPriorToRandomisation()).isEqualTo(Constants.YES);
        softly.assertThat(conmed.getConmedEndPriorToRandomisation()).isEqualTo(Constants.YES);
        softly.assertThat(conmed.getConmedStartedPriorToStudy()).isEqualTo(Constants.YES);
        softly.assertThat(conmed.getConmedEndedPriorToStudy()).isEqualTo(Constants.YES);

        conmed = new Conmed(
                ConmedRaw.builder()
                        .startDate(new Date())
                        .endDate(new Date())
                        .build(),
                Subject.builder()
                        .dateOfRandomisation(new Date())
                        .firstTreatmentDate(new Date())
                        .build()
        );

        softly.assertThat(conmed.getConmedStartPriorToRandomisation()).isEqualTo(Constants.NO);
        softly.assertThat(conmed.getConmedEndPriorToRandomisation()).isEqualTo(Constants.NO);
        softly.assertThat(conmed.getConmedStartedPriorToStudy()).isEqualTo(Constants.NO);
        softly.assertThat(conmed.getConmedEndedPriorToStudy()).isEqualTo(Constants.NO);

        conmed = new Conmed(
                ConmedRaw.builder()
                        .startDate(DateUtils.addDays(new Date(), 1))
                        .endDate(DateUtils.addDays(new Date(), 1))
                        .build(),
                Subject.builder()
                        .dateOfRandomisation(new Date())
                        .firstTreatmentDate(new Date())
                        .build()
        );

        softly.assertThat(conmed.getConmedStartPriorToRandomisation()).isEqualTo(Constants.NO);
        softly.assertThat(conmed.getConmedEndPriorToRandomisation()).isEqualTo(Constants.NO);
        softly.assertThat(conmed.getConmedStartedPriorToStudy()).isEqualTo(Constants.NO);
        softly.assertThat(conmed.getConmedEndedPriorToStudy()).isEqualTo(Constants.NO);
    }
}
