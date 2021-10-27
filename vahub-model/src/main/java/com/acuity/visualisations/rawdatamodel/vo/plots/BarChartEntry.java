package com.acuity.visualisations.rawdatamodel.vo.plots;

import com.acuity.visualisations.rawdatamodel.vo.HasStringId;
import com.acuity.visualisations.rawdatamodel.vo.HasSubject;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.Comparator;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BarChartEntry<T extends HasStringId & HasSubject> implements Comparable<BarChartEntry<T>> {
    private Object category;
    private Double value;
    private Integer totalSubjects;
    @JsonIgnore
    private Collection<T> eventSet;

    public BarChartEntry(Object category, Double value) {
        this(category, value, null, null);
    }

    @Override
    public int compareTo(BarChartEntry<T> o) {
        if (o.getCategory() instanceof Comparable && this.getCategory() instanceof Comparable) {
            return Comparator.<Comparable>naturalOrder().compare((Comparable) this.getCategory(), (Comparable) o.getCategory());
        } else {
            return 0;
        }
    }
}
