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
