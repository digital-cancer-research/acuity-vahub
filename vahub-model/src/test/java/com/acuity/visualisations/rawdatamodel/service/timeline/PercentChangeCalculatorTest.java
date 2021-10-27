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

package com.acuity.visualisations.rawdatamodel.service.timeline;

import com.acuity.visualisations.rawdatamodel.vo.timeline.dose.DoseAndFrequency;
import com.acuity.visualisations.rawdatamodel.vo.timeline.dose.Frequency;
import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import java.util.Map;

import static com.acuity.visualisations.rawdatamodel.service.timeline.data.PercentChangeCalculator.calculate;
import static org.junit.Assert.assertEquals;

public class PercentChangeCalculatorTest {
    private static final Frequency ONCE_PER_DAY = new Frequency(null, 1);

    private static final String DRUG_A = "A";

    private static final String DRUG_B = "B";

    @Test
    public void testChangesForOneDrug() {
        Map<String, DoseAndFrequency> drugMaxDoses = ImmutableMap.of(
                DRUG_A,
                new DoseAndFrequency(DRUG_A, 1000.0, null, ONCE_PER_DAY)
        );
        Map<String, DoseAndFrequency> currentDoses = ImmutableMap.of(
                DRUG_A,
                new DoseAndFrequency(DRUG_A, 900.0, null, ONCE_PER_DAY)
        );

        double changePerDay = calculate(drugMaxDoses, currentDoses, DoseAndFrequency::getDosePerDay);

        assertEquals(-10, changePerDay, 0.001);
    }

    @Test
    public void testChangesForTwoDrugs() {
        Map<String, DoseAndFrequency> drugMaxDoses = ImmutableMap.of(
                DRUG_A, new DoseAndFrequency(DRUG_A, 1000.0, null, ONCE_PER_DAY),
                DRUG_B, new DoseAndFrequency(DRUG_B, 50.0, null, ONCE_PER_DAY)
        );

        Map<String, DoseAndFrequency> currentDoses = ImmutableMap.of(
                DRUG_A, new DoseAndFrequency(DRUG_A, 900.0, null, ONCE_PER_DAY),
                DRUG_B, new DoseAndFrequency(DRUG_B, 25.0, null, ONCE_PER_DAY)
        );

        double change = calculate(drugMaxDoses, currentDoses, DoseAndFrequency::getDosePerDay);

        assertEquals(-30, change, 0.001);
    }
}
