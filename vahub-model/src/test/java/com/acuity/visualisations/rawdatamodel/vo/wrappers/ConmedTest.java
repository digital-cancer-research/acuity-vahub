/*
 * Copyright 2021 The University of Manchester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
