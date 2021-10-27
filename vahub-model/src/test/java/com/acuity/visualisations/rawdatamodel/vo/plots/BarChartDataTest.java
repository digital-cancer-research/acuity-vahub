package com.acuity.visualisations.rawdatamodel.vo.plots;

import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class BarChartDataTest {

    @Test
    public void shouldCompareInNaturalOrder() {
        BarChartData barChartData1 = new BarChartData();
        barChartData1.setName("CTC Grade 1");

        BarChartData barChartData2 = new BarChartData();
        barChartData2.setName("CTC Grade 2");

        BarChartData barChartData3 = new BarChartData();
        barChartData3.setName("Empty");

        BarChartData barChartData4 = new BarChartData();
        barChartData4.setName("(Empty)");

        assertThat(barChartData1.compareTo(barChartData2)).isLessThan(0);
        assertThat(barChartData2.compareTo(barChartData3)).isLessThan(0);
        assertThat(barChartData3.compareTo(barChartData4)).isEqualTo(-1);

    }
}
