package com.acuity.visualisations.rawdatamodel.vo.plots;

import com.acuity.visualisations.rawdatamodel.util.Attributes;
import com.acuity.visualisations.rawdatamodel.util.DaysUtil;
import lombok.Value;

@Value
public class BarChartDateFormattedOption implements Comparable<BarChartDateFormattedOption> {
    private String date;
    private String format;

    public static final BarChartDateFormattedOption EMPTY = new BarChartDateFormattedOption(null, null);


    @Override
    public int compareTo(BarChartDateFormattedOption o) {
        if (this.date == null && o.date == null) {
            return 0;
        }
        if (this.date == null) {
            return 1;
        }
        if (o.date == null) {
            return -1;
        }
        return DaysUtil.toDate(this.date, this.format).compareTo(DaysUtil.toDate(o.date, o.format));
    }

    @Override
    public String toString() {
        if (this.date == null) {
            return Attributes.DEFAULT_EMPTY_VALUE;
        }
        return date;
    }
}
