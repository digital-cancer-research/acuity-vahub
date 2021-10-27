package com.acuity.visualisations.rawdatamodel.filters;

import org.junit.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class MultiValueIntRangeSetFilterTest {

    @Test
    public void testInitializationWithFromAndTo() {
        MultiValueIntRangeSetFilter filter = new MultiValueIntRangeSetFilter(1, 10);

        assertThat(filter.getFrom()).isEqualTo(1);
        assertThat(filter.getTo()).isEqualTo(10);
        assertThat(filter.getValues()).containsOnly(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    }

    @Test
    public void testInitializationWithValues() {
        MultiValueIntRangeSetFilter filter = new MultiValueIntRangeSetFilter(Arrays.asList(2, 5, 7));

        assertThat(filter.getFrom()).isEqualTo(2);
        assertThat(filter.getTo()).isEqualTo(7);
        assertThat(filter.getValues()).containsOnly(2, 5, 7);
    }
}
