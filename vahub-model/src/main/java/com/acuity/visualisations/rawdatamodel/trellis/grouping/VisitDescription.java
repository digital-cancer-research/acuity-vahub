package com.acuity.visualisations.rawdatamodel.trellis.grouping;

import com.acuity.visualisations.rawdatamodel.trellis.grouping.extractor.VisitDescriptionValueExtractor;
import com.acuity.visualisations.rawdatamodel.util.Attributes;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Comparator;
import java.util.Objects;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class VisitDescription extends VisitDescriptionValueExtractor implements Comparable<VisitDescription> {
    private String visitDescription;

    @Override
    public int compareTo(VisitDescription o) {
        int v1 = extractFrom(this);
        int v2 = extractFrom(o);
        if (v1 == v2) {
            return Objects.compare(this.getVisitDescription(), o.getVisitDescription(), Comparator.naturalOrder());
        } else {
            return Integer.compare(v1, v2);
        }
    }

    @Override
    public String toString() {
        return visitDescription == null ? Attributes.DEFAULT_EMPTY_VALUE : visitDescription;
    }
}
