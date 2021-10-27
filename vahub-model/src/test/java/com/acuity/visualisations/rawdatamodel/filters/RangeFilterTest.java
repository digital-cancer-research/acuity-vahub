package com.acuity.visualisations.rawdatamodel.filters;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RangeFilterTest {

    @Test
    public void testIsValidEmpty() {
        RangeFilter<Integer> rangeFilter = new RangeFilter<>();

        assertThat(rangeFilter.isValid()).isFalse();
    }
    
    @Test
    public void testSetFrom() {
        RangeFilter<Integer> rangeFilter = new RangeFilter<>();
        rangeFilter.setFrom(6);
        assertThat(rangeFilter.getFrom()).isEqualTo(6);
    }

    @Test
    public void testSetTo() {
        RangeFilter<Integer> rangeFilter = new RangeFilter<>();
        rangeFilter.setTo(7);
        assertThat(rangeFilter.getTo()).isEqualTo(7);
    }

    @Test
    public void testIsValid() {
        RangeFilter<Integer> rangeFilter = new RangeFilter<>();
        assertThat(rangeFilter.isValid()).isFalse();
        rangeFilter.setTo(7);
        assertThat(rangeFilter.isValid()).isTrue();
        rangeFilter.setFrom(3);
        assertThat(rangeFilter.isValid()).isTrue();
        rangeFilter.setFrom(null);
        assertThat(rangeFilter.isValid()).isTrue();
        rangeFilter.setIncludeEmptyValues(true);
        assertThat(rangeFilter.isValid()).isTrue();
    }

    @Test
    public void testCompleteWithValue() {

        RangeFilter<Double> rangeFilter = new RangeFilter<>();
        rangeFilter.completeWithValue(1.3);
        assertThat(rangeFilter.getFrom()).isEqualTo(1.3);
        assertThat(rangeFilter.getTo()).isEqualTo(1.3);
        rangeFilter.completeWithValue(3.8);
        assertThat(rangeFilter.getFrom()).isEqualTo(1.3);
        assertThat(rangeFilter.getTo()).isEqualTo(3.8);
        rangeFilter.completeWithValue(9.8);
        assertThat(rangeFilter.getFrom()).isEqualTo(1.3);
        assertThat(rangeFilter.getTo()).isEqualTo(9.8);
        rangeFilter.completeWithValue(5.0);
        assertThat(rangeFilter.getFrom()).isEqualTo(1.3);
        assertThat(rangeFilter.getTo()).isEqualTo(9.8);
    }

    @Test
    public void testComplete() {

        RangeFilter<Double> rangeFilter = new RangeFilter<>();

        RangeFilter<Double> rangeFilter2 = new RangeFilter<>();
        rangeFilter2.completeWithValue(1.3);
        rangeFilter2.completeWithValue(3.8);
        rangeFilter.complete(rangeFilter2);
        assertThat(rangeFilter.getFrom()).isEqualTo(1.3);
        assertThat(rangeFilter.getTo()).isEqualTo(3.8);
        assertThat(rangeFilter.getIncludeEmptyValues()).isNull();

        RangeFilter<Double> rangeFilter3 = new RangeFilter<>();
        rangeFilter3.setFrom(0.7);
        rangeFilter.complete(rangeFilter3);
        assertThat(rangeFilter.getFrom()).isEqualTo(0.7);
        assertThat(rangeFilter.getTo()).isEqualTo(3.8);
        assertThat(rangeFilter.getIncludeEmptyValues()).isTrue();
    }
}
