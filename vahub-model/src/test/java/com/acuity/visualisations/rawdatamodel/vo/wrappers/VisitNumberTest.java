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

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class VisitNumberTest {

    @Test
    public void testCompareTo() {
        VisitNumber visitNumberA = VisitNumber.fromValue(1d);
        VisitNumber visitNumberB = VisitNumber.fromValue(1.07);
        VisitNumber visitNumberC = VisitNumber.fromValue(7.0);
        VisitNumber visitNumberD = VisitNumber.fromValue(6.07);

        assertThat(visitNumberA).isLessThan(visitNumberB);
        assertThat(visitNumberB).isLessThan(visitNumberC);
        assertThat(visitNumberD).isLessThan(visitNumberC);
        assertThat(visitNumberD).isEqualByComparingTo(visitNumberD);
    }

    @Test
    public void testToString() {
        VisitNumber visitNumberA = VisitNumber.fromValue(2.0);
        VisitNumber visitNumberB = VisitNumber.fromValue(2.04);
        VisitNumber visitNumberC = VisitNumber.fromValue(2d);
        VisitNumber visitNumberD = VisitNumber.fromValue(2.99999);
        VisitNumber visitNumberE = VisitNumber.fromValue(2.00000001);

        assertThat(visitNumberA).hasToString("2");
        assertThat(visitNumberB).hasToString("2.04");
        assertThat(visitNumberC).hasToString("2");
        assertThat(visitNumberD).hasToString("2.99999");
        assertThat(visitNumberE).hasToString("2.00000001");
    }

    @Test
    public void testNormalizeVisitNumberString() {
        assertThat(VisitNumber.normalizeVisitNumberString("1")).isEqualTo("1");
        assertThat(VisitNumber.normalizeVisitNumberString("1.00")).isEqualTo("1");
        assertThat(VisitNumber.normalizeVisitNumberString("2.04")).isEqualTo("2.04");
        assertThat(VisitNumber.normalizeVisitNumberString("3.13")).isEqualTo("3.13");
    }
}
