/*
 * Copyright 2021 The University of Manchester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
