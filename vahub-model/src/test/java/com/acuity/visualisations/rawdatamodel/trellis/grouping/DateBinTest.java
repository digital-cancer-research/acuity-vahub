package com.acuity.visualisations.rawdatamodel.trellis.grouping;

import com.acuity.visualisations.rawdatamodel.util.DaysUtil;
import org.assertj.core.api.Assertions;
import org.junit.Test;

public class DateBinTest {
    @Test
    public void testGetBin() throws Exception {
        final Bin bin1 = Bin.newInstance(DaysUtil.toDate("1981-03-24"), 50);
        final Bin bin2 = Bin.newInstance(DaysUtil.toDate("1981-03-23"), 50);
        System.out.println(bin1);
        System.out.println(bin2);
        Assertions.assertThat(bin1).hasToString("1981-03-24 - 1981-05-12");
        Assertions.assertThat(bin2).hasToString("1981-02-02 - 1981-03-23");
    }
    @Test
    public void testGetNextBin() throws Exception {
        final Bin bin = Bin.newInstance(DaysUtil.toDate("1981-03-24"), 50);
        final Bin nextBin = bin.getNextBin();
        System.out.println(bin);
        System.out.println(nextBin);
        Assertions.assertThat(bin).hasToString("1981-03-24 - 1981-05-12");
        Assertions.assertThat(nextBin).hasToString("1981-05-13 - 1981-07-01");
    }

}
