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

package com.acuity.visualisations.rawdatamodel.util;

import com.acuity.visualisations.rawdatamodel.vo.HasBaselineDate;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.google.common.collect.ImmutableList;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;

import java.util.Collections;
import java.util.Date;
import java.util.OptionalDouble;

import static com.acuity.visualisations.rawdatamodel.util.DaysUtil.toDate;
import static org.assertj.core.api.Java6Assertions.assertThat;

public class BaselineUtilTest {

    @Test
    public void shouldCalculateChangeFromBaseline() {
        // Given
        double value = 10.;
        double baseline = 100.;

        // When
        OptionalDouble result = BaselineUtil.changeFromBaseline(value, baseline);

        // Then
        assertThat(result.getAsDouble()).isEqualTo(-90.);
    }

    @Test
    public void shouldNotCalculateChangeFromBaselineWhenNull() {
        // Given
        Double value = 10.;
        Double baseline = null;

        // When
        OptionalDouble result = BaselineUtil.changeFromBaseline(value, baseline);

        // Then
        assertThat(result.isPresent()).isEqualTo(false);
    }

    @Test
    public void shouldCalculatePercentChangeFromBaseline() {
        // Given
        double value = 110.;
        double baseline = 100.;

        // When
        OptionalDouble result = BaselineUtil.percentChangeFromBaseline(value, baseline);

        // Then
        assertThat(result.getAsDouble()).isEqualTo(10.);
    }

    @Test
    public void shouldNotCalculatePercentChangeFromBaselineWhenNull() {
        // Given
        Double value = 110.;
        Double baseline = null;

        // When
        OptionalDouble result = BaselineUtil.percentChangeFromBaseline(value, baseline);

        // Then
        assertThat(result.isPresent()).isEqualTo(false);
    }

    @Test
    public void shouldNotFailOnEmptyList() {
        BaselineUtil.defineBaselinesForEvents(Collections.emptyList(), e -> Subject.builder().build(), (a, b) -> null);
    }

    @Test
    public void shouldNotFailOnNullKeyFunction() {
        BaselineUtil.defineBaselinesForEvents(Collections.singleton(() -> new Date(0)), e -> null, (a, b) -> null);
    }

    @Test
    public void shouldNotFailOnSubjectMismatch() {
        BaselineUtil.defineBaselinesForEvents(Collections.singleton(() -> new Date(0)), e -> Subject.builder().build(), (a, b) -> null);
    }

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test
    public void shouldChooseSummaryBaselineDateProperly() {
        HasBaselineDate earliestBeforeSubjectBaseline = new HasBaselineDateImplForSummaryCalculation(toDate("2013-01-01"));
        HasBaselineDate latestBeforeSubjectBaseline = new HasBaselineDateImplForSummaryCalculation(toDate("2014-01-01"));
        Date subjectBaselineDate = toDate("2014-12-02");
        HasBaselineDate earliestAfterSubjectBaseline = new HasBaselineDateImplForSummaryCalculation(toDate("2014-12-03"));
        HasBaselineDate latestAfterSubjectBaseline = new HasBaselineDateImplForSummaryCalculation(toDate("2015-02-02"));

        softly.assertThat(BaselineUtil.chooseSummaryBaselineDate(
                ImmutableList.of(
                        earliestBeforeSubjectBaseline,
                        latestBeforeSubjectBaseline,
                        earliestAfterSubjectBaseline,
                        latestAfterSubjectBaseline),
                Subject.builder()
                        .baselineDate(subjectBaselineDate)
                        .build()))
                .isEqualTo(latestBeforeSubjectBaseline.getBaselineDate());

        softly.assertThat(BaselineUtil.chooseSummaryBaselineDate(
                ImmutableList.of(
                        earliestBeforeSubjectBaseline,
                        earliestAfterSubjectBaseline,
                        latestAfterSubjectBaseline),
                Subject.builder()
                        .baselineDate(subjectBaselineDate)
                        .build()))
                .isEqualTo(earliestBeforeSubjectBaseline.getBaselineDate());

        softly.assertThat(BaselineUtil.chooseSummaryBaselineDate(
                ImmutableList.of(
                        earliestAfterSubjectBaseline,
                        latestAfterSubjectBaseline),
                Subject.builder()
                        .baselineDate(subjectBaselineDate)
                        .build()))
                .isEqualTo(earliestAfterSubjectBaseline.getBaselineDate());

        softly.assertThat(BaselineUtil.chooseSummaryBaselineDate(
                ImmutableList.of(
                        latestAfterSubjectBaseline),
                Subject.builder()
                        .baselineDate(subjectBaselineDate)
                        .build()))
                .isEqualTo(latestAfterSubjectBaseline.getBaselineDate());
    }

    @Getter
    @RequiredArgsConstructor
    private static class HasBaselineDateImplForSummaryCalculation implements HasBaselineDate {
        
        private final Date baselineDate;

        @Override
        public Date getEventDate() {
            throw new NotImplementedException("Method is not intended to used in this implementation!");
        }
    }
}
