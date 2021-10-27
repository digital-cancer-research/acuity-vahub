package com.acuity.visualisations.rawdatamodel.util;

import com.acuity.visualisations.rawdatamodel.trellis.grouping.annotations.DateFormattedOption;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.HasStringId;
import com.acuity.visualisations.rawdatamodel.vo.HasSubjectId;
import com.acuity.visualisations.rawdatamodel.vo.plots.BarChartDateFormattedOption;

import java.util.Collections;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

public final class DateFormattedOptionTransformer {

    private DateFormattedOptionTransformer() {
    }

    public static <T extends HasSubjectId & HasStringId, G extends Enum<G> & GroupByOption<T>>
    Function<G, ?> getDateFormattedFunction(FilterResult<T> filtered) {
        return o -> {
            final DateFormattedOption dateFormattedOptionAnnotation = GroupByOption.getDateFormattedAnnotation(o);
            if (dateFormattedOptionAnnotation == null) {
                return Collections.emptyList();
            }
            return filtered.getFilteredEvents().stream()
                    .collect(toMap(
                            HasStringId::getId,
                            event -> {
                                String value = (String) o.getAttribute().getFunction().apply(event);
                                return value != null
                                        ? new BarChartDateFormattedOption(value, dateFormattedOptionAnnotation.format())
                                        : BarChartDateFormattedOption.EMPTY;
                            }));
        };
    }
}
