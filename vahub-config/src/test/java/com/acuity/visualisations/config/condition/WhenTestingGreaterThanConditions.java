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

package com.acuity.visualisations.config.condition;

import static com.acuity.visualisations.config.condition.Conditions.greaterThan;
import static com.acuity.visualisations.config.condition.Conditions.greaterThanOrEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import org.assertj.core.util.Lists;
import org.junit.Test;

/**
 *
 * @author ksnd199
 */
public class WhenTestingGreaterThanConditions {

    // GreaterThan Integer
    @Test
    public void shouldPassTestGreaterThanCondition1() {
        assertThat(Lists.newArrayList(1, 2, 3)).are(greaterThan(-1));
    }

    @Test
    public void shouldPassTestGreaterThanCondition2() {
        assertThat(Lists.newArrayList(1, 2, 3)).are(greaterThan(0));
    }

    @Test(expected = AssertionError.class)
    public void shouldFailTestGreaterThanCondition1() {
        assertThat(Lists.newArrayList(1, 2, 3)).are(greaterThan(1));
    }

    @Test(expected = AssertionError.class)
    public void shouldFailTestGreaterThanCondition2() {
        assertThat(Lists.newArrayList(1, 2, 3)).are(greaterThan(2));
    }

    // GreaterThan Double
    @Test
    public void shouldPassTestGreaterThanDoubleCondition1() {
        assertThat(Lists.newArrayList(1.0, 2.0, 3.0)).are(greaterThan(-1.0));
    }

    @Test
    public void shouldPassTestGreaterThanDoubleCondition2() {
        assertThat(Lists.newArrayList(1.0, 2.0, 3.0)).are(greaterThan(0.0));
    }

    @Test(expected = AssertionError.class)
    public void shouldFailTestGreaterThanDoubleCondition1() {
        assertThat(Lists.newArrayList(1.0, 2.0, 3.0)).are(greaterThan(1.0));
    }

    @Test(expected = AssertionError.class)
    public void shouldFailTestGreaterThanDoubleCondition2() {
        assertThat(Lists.newArrayList(1.0, 2.0, 3.0)).are(greaterThan(2.0));
    }

    // GreaterThanOrEqualTo Integer
    @Test
    public void shouldPassTestGreaterThanOrEqualToCondition1() {
        assertThat(Lists.newArrayList(1, 2, 3)).are(greaterThanOrEqualTo(0));
    }

    @Test
    public void shouldPassTestGreaterThanOrEqualToCondition2() {
        assertThat(Lists.newArrayList(1, 2, 3)).are(greaterThanOrEqualTo(1));
    }

    @Test(expected = AssertionError.class)
    public void shouldFailTestGreaterThanOrEqualToCondition1() {
        assertThat(Lists.newArrayList(1, 2, 3)).are(greaterThanOrEqualTo(2));
    }

    @Test(expected = AssertionError.class)
    public void shouldFailTestGreaterThanOrEqualToondition2() {
        assertThat(Lists.newArrayList(1, 2, 3)).are(greaterThanOrEqualTo(3));
    }

    // GreaterThanOrEqualTo Double
    @Test
    public void shouldPassTestGreaterThanOrEqualToDoubleCondition1() {
        assertThat(Lists.newArrayList(1.0, 2.0, 3.0)).are(greaterThanOrEqualTo(0.0));
    }

    @Test
    public void shouldPassTestGreaterThanOrEqualToDoubleCondition2() {
        assertThat(Lists.newArrayList(1.0, 2.0, 3.0)).are(greaterThanOrEqualTo(1.0));
    }

    @Test(expected = AssertionError.class)
    public void shouldFailTestGreaterThanOrEqualToDoubleCondition1() {
        assertThat(Lists.newArrayList(1.0, 2.0, 3.0)).are(greaterThanOrEqualTo(2.0));
    }

    @Test(expected = AssertionError.class)
    public void shouldFailTestGreaterThanOrEqualToDoubleCondition2() {
        assertThat(Lists.newArrayList(1.0, 2.0, 3.0)).are(greaterThanOrEqualTo(3.0));
    }
}
