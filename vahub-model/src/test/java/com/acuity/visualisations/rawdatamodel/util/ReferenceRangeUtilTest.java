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
