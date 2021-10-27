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

import org.junit.Test;

import java.util.OptionalDouble;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class ReferenceRangeUtilTest {

    @Test
    public void shouldCalculateTimesUpperRefRange() {
        // Given
        double value = 1000.;
        double upperRefRange = 100.;

        // When
        OptionalDouble result = ReferenceRangeUtil.timesReferenceRange(value, upperRefRange);

        // Then
        assertThat(result.getAsDouble()).isEqualTo(10.);
    }

    @Test
    public void shouldNotCalculateUpperRefRangeWhenNull() {
        // Given
        Double value = 10.;
        Double upperRefRange = null;

        // When
        OptionalDouble result = ReferenceRangeUtil.timesReferenceRange(value, upperRefRange);

        // Then
        assertThat(result.isPresent()).isEqualTo(false);
    }

    @Test
    public void shouldNotCalculateTimesUpperRefRangeWhenRefRangeIs0() {
        // Given
        double value = 1000.;
        double upperRefRange = 0.;

        // When
        OptionalDouble result = ReferenceRangeUtil.timesReferenceRange(value, upperRefRange);

        // Then
        assertThat(result.isPresent()).isFalse();
    }

    @Test
    public void shouldCalculateReferenceRangeNormalisedValue() {
        // Given
        double value = 1000.;
        double lowerRefRange = 10.;
        double upperRefRange = 100.;

        // When
        OptionalDouble result = ReferenceRangeUtil.referenceRangeNormalisedValue(value, lowerRefRange, upperRefRange);

        // Then
        assertThat(result.getAsDouble()).isEqualTo(11.);
    }
}
