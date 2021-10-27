package com.acuity.visualisations.rawdatamodel.vo.plots;

import com.acuity.visualisations.rawdatamodel.util.AlphanumEmptyLastComparator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Comparator;
import java.util.List;

@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class BarChartData implements Comparable<BarChartData> {
    private Object name;
    private List<?> categories;
    private List<? extends BarChartEntry> series;

    @Override
    public int compareTo(BarChartData o) {
        if (o.getName() instanceof String && this.getName() instanceof String) {
            return AlphanumEmptyLastComparator.getInstance().compare((String) this.getName(), (String) o.getName());
        } else {
            if (o.getName() instanceof Comparable && this.getName() instanceof Comparable) {
                return Comparator.<Comparable>naturalOrder().compare((Comparable) this.getName(), (Comparable) o.getName());
            } else {
                return 0;
            }
        }
    }
}
