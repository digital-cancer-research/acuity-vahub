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

import com.acuity.visualisations.rawdatamodel.vo.HasBaselineDate;
import com.acuity.visualisations.rawdatamodel.vo.HasEventDate;
import com.acuity.visualisations.rawdatamodel.vo.HasSubject;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.acuity.visualisations.rawdatamodel.util.ObjectUtil.anyNull;

public final class BaselineUtil {
    private BaselineUtil() {
    }

    public static OptionalDouble changeFromBaseline(Double value, Double baseline) {
        if (anyNull(value, baseline)) {
            return OptionalDouble.empty();
        }
        double changeFromBaseline = value - baseline;
        return OptionalDouble.of(changeFromBaseline);
    }

    public static OptionalDouble percentChangeFromBaseline(Double value, Double baseline) {
        if (anyNull(value, baseline) || baseline == 0) {
            return OptionalDouble.empty();
        }
        double percentChangeFromBaseline = ((value - baseline) / baseline) * 100;
        return OptionalDouble.of(percentChangeFromBaseline);
    }

    public static <T extends HasEventDate> Collection<T> defineBaselinesForEvents(Collection<T> events, Function<T, ? extends HasSubject> keyFunction,
                                                                                  BiFunction<T, T, T> baselineMerger) {
        // calculate keyfunction results for each actual event (they are used twice)
        Map<T, ? extends HasSubject> keyFunctionResults = events.stream()
                .map(e -> ImmutablePair.of(e, keyFunction.apply(e)))
                .filter(pair -> pair.getRight() != null)    // sometimes keyFunction may return null (for example, in case of non-dosed subjects)
                .collect(Collectors.toMap(ImmutablePair::getLeft, ImmutablePair::getRight));

        // find baseline for each key (result of keyFunction)
        Map<? extends HasSubject, T> baselines = keyFunctionResults.entrySet().stream()
                .collect(Collectors.groupingBy(Map.Entry::getValue, Collectors.mapping(Map.Entry::getKey, Collectors.toList())))
                .entrySet().stream()
                .filter(pair -> pair.getKey().getSubject().getBaselineDate() != null)
                .map(pair -> ImmutablePair.of(
                        pair.getKey(),
                        pair.getValue().stream()
                                .filter(ge -> ge.getEventDate() != null)
                                .min(composeBaselineEventComparator(pair.getKey().getSubject().getBaselineDate(), HasEventDate::getEventDate))))
                .filter(pair -> pair.getRight().isPresent())
                .collect(Collectors.toMap(ImmutablePair::getLeft, p -> p.getRight().get()));

        // enrich each event with corresponding baseline value if not null
        return events.stream()
                .map(e -> Optional.ofNullable(keyFunctionResults.get(e))
                        .map(baselines::get)
                        .map(r -> baselineMerger.apply(e, r))
                        .orElse(e))
                .collect(Collectors.toList());
    }

    public static Date chooseSummaryBaselineDate(Collection<? extends HasBaselineDate> events, Subject subject) {
        return events.stream()
                .filter(e -> e.getBaselineDate() != null)
                .collect(Collectors.groupingBy(HasBaselineDate::getBaselineDate))
                .values().stream()
                .map(list -> list.get(0))   // result of grouping, so can't be empty
                .min(composeBaselineEventComparator(subject.getBaselineDate(), HasBaselineDate::getBaselineDate))
                .map(HasBaselineDate::getBaselineDate)
                .orElse(null);
    }

    private static <T extends HasEventDate> Comparator<T> composeBaselineEventComparator(Date firstDoseDate, Function<T, Date> dateExtractor) {
        return Comparator
                .<T, Integer>comparing(hasEventDate -> firstDoseDate.before(dateExtractor.apply(hasEventDate)) ? 1 : 0)
                .thenComparing(hasEventDate -> Math.abs(firstDoseDate.getTime() - dateExtractor.apply(hasEventDate).getTime()));
    }
}
