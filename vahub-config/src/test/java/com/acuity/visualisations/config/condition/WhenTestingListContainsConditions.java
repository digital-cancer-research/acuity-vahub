package com.acuity.visualisations.config.condition;

import static com.acuity.visualisations.config.condition.Conditions.in;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Lists.newArrayList;
import org.junit.Test;

/**
 *
 * @author ksnd199
 */
public class WhenTestingListContainsConditions {

    @Test
    public void shouldPassTestListContainsCondition1() {
        assertThat(newArrayList("1", "2", "3")).are(in(newArrayList("1", "2", "3", "4")));
    }

    @Test(expected = AssertionError.class)
    public void shouldFailTestListContainsCondition1() {
        assertThat(newArrayList("1", "2", "3")).are(in(newArrayList("1", "2", "4")));
    }
}
