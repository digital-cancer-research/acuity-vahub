package com.acuity.visualisations.rawdatamodel.vo.plots;

import com.acuity.visualisations.rawdatamodel.util.AlphanumEmptyLastComparator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Comparator;

/*
* Note: this class has an ordering that is
* inconsistent with equals.
* */

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class LineChartEntry implements Comparable<LineChartEntry> {
    private Object x;
    private Object y;
    private Object name;
    private Object colorBy;
    private Object sortBy;

    @Override
    public int compareTo(LineChartEntry o) {
        if (o.getSortBy() instanceof String && this.getSortBy() instanceof String) {
            return AlphanumEmptyLastComparator.getInstance().compare((String) this.getSortBy(), (String) o.getSortBy());
        } else {
            if (o.getSortBy() instanceof Comparable && this.getSortBy() instanceof Comparable) {
                return Comparator.<Comparable>naturalOrder().compare((Comparable) this.getSortBy(), (Comparable) o.getSortBy());
            } else {
                return 0;
            }
        }
    }
}

