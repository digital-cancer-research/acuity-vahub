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
