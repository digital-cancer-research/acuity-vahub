package com.acuity.visualisations.config.condition;

import static com.acuity.visualisations.config.condition.Conditions.lessThan;
import static com.acuity.visualisations.config.condition.Conditions.lessThanOrEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import org.assertj.core.util.Lists;
import org.junit.Test;

/**
 *
 * @author ksnd199
 */
public class WhenTestingLessThanConditions {

    // LessThan Integer
    @Test
    public void shouldPassTestLessThanCondition1() {
        assertThat(Lists.newArrayList(1, 2, 3)).are(lessThan(5));
    }

    @Test
    public void shouldPassTestLessThanCondition2() {
        assertThat(Lists.newArrayList(1, 2, 3)).are(lessThan(4));
    }

    @Test(expected = AssertionError.class)
    public void shouldFailTestLessThanCondition1() {
        assertThat(Lists.newArrayList(1, 2, 3)).are(lessThan(3));
    }

    @Test(expected = AssertionError.class)
    public void shouldFailTestLessThanCondition2() {
        assertThat(Lists.newArrayList(1, 2, 3)).are(lessThan(2));
    }

    // LessThan Double
    @Test
    public void shouldPassTestLessThanDoubleCondition1() {
        assertThat(Lists.newArrayList(1.0, 2.0, 3.0)).are(lessThan(4.0));
    }

    @Test
    public void shouldPassTestLessThanDoubleCondition2() {
        assertThat(Lists.newArrayList(1.0, 2.0, 3.0)).are(lessThan(5.0));
    }

    @Test(expected = AssertionError.class)
    public void shouldFailTestLessThanDoubleCondition1() {
        assertThat(Lists.newArrayList(1.0, 2.0, 3.0)).are(lessThan(3.0));
    }

    @Test(expected = AssertionError.class)
    public void shouldFailTestLessThanDoubleCondition2() {
        assertThat(Lists.newArrayList(1.0, 2.0, 3.0)).are(lessThan(2.0));
    }

    // LessThanOrEqualTo Integer
    @Test
    public void shouldPassTestLessThanOrEqualToCondition1() {
        assertThat(Lists.newArrayList(1, 2, 3)).are(lessThanOrEqualTo(3));
    }

    @Test
    public void shouldPassTestLessThanOrEqualToCondition2() {
        assertThat(Lists.newArrayList(1, 2, 3)).are(lessThanOrEqualTo(4));
    }

    @Test(expected = AssertionError.class)
    public void shouldFailTestLessThanOrEqualToCondition1() {
        assertThat(Lists.newArrayList(1, 2, 3)).are(lessThanOrEqualTo(2));
    }

    @Test(expected = AssertionError.class)
    public void shouldFailTestLessThanOrEqualToondition2() {
        assertThat(Lists.newArrayList(1, 2, 3)).are(lessThanOrEqualTo(1));
    }

    // LessThanOrEqualTo Double
    @Test
    public void shouldPassTestLessThanOrEqualToDoubleCondition1() {
        assertThat(Lists.newArrayList(1.0, 2.0, 3.0)).are(lessThanOrEqualTo(3.0));
    }

    @Test
    public void shouldPassTestLessThanOrEqualToDoubleCondition2() {
        assertThat(Lists.newArrayList(1.0, 2.0, 3.0)).are(lessThanOrEqualTo(4.0));
    }

    @Test(expected = AssertionError.class)
    public void shouldFailTestLessThanOrEqualToDoubleCondition1() {
        assertThat(Lists.newArrayList(1.0, 2.0, 3.0)).are(lessThanOrEqualTo(2.0));
    }

    @Test(expected = AssertionError.class)
    public void shouldFailTestLessThanOrEqualToDoubleCondition2() {
        assertThat(Lists.newArrayList(1.0, 2.0, 3.0)).are(lessThanOrEqualTo(1.0));
    }
}
