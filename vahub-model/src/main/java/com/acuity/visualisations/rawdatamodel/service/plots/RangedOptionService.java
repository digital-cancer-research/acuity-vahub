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

package com.acuity.visualisations.rawdatamodel.service.plots;

import com.acuity.visualisations.rawdatamodel.trellis.grouping.annotations.RangeOption;
import com.acuity.visualisations.rawdatamodel.util.DaysUtil;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.HasStringId;
import com.acuity.visualisations.rawdatamodel.vo.HasSubjectId;
import com.acuity.visualisations.rawdatamodel.vo.plots.BarChartOptionRange;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
public class RangedOptionService {
    private static final int CHUNK_NUMBER = 6;

    public <T extends HasSubjectId & HasStringId, G extends Enum<G> & GroupByOption<T>> Function<G, ?> getRangeFunction(FilterResult<T> filtered,
                                                                                                                        Boolean isPopulationOption,
                                                                                                                        Object param) {
        return o -> {
            final RangeOption rangeOptionAnnotation = GroupByOption.getRangeOptionAnnotation(o);
            switch (rangeOptionAnnotation.value()) {
                case DATE:
                    return getDateIntervals(filtered.getFilteredEvents(), o.getAttribute().getFunction(), isPopulationOption);
                case LONG:
                    return getLongIntervals(filtered.getFilteredEvents(), o.getAttribute().getFunction(), isPopulationOption);
                case DOUBLE:
                    return getDoubleIntervals(filtered.getFilteredEvents(), o.getAttribute().getFunction(), isPopulationOption);
                case MAP_STRING_DOUBLE:
                    if (param == null) {
                        return getDoubleIntervalsInMap(filtered.getFilteredEvents(), o.getAttribute().getFunction(), isPopulationOption);
                    }
                    String strParam = (String) param;
                    Function<T, Map<? extends String, ? extends Double>> function
                            = (Function<T, Map<? extends String, ? extends Double>>) o.getAttribute().getFunction();
                    Map<String, Map<String, BarChartOptionRange<Double>>> resultMap = new HashMap<>();
                    resultMap.put(strParam,
                            getDoubleIntervals(filtered.getFilteredEvents(),
                                    function.andThen(e -> e.get(strParam)), isPopulationOption));
                    return resultMap;
                default:
                    return Collections.emptyMap();
            }
        };
    }

    private <T extends HasSubjectId & HasStringId> Map<String, Map<String, BarChartOptionRange<Double>>> getDoubleIntervalsInMap(Collection<T> events,
                                                                                                                                 Function<T, ?> function,
                                                                                                                                 Boolean isPopulationOption) {
        Map<String, Map<String, Long>> drugIdValuesMap = new HashMap<>();
        for (T event : events) {
            Map<String, Double> map = (Map<String, Double>) function.apply(event);
            String id = isPopulationOption ? event.getSubjectId() : event.getId();
            map.forEach((drug, value) -> {
                Map<String, Long> idValueMap = drugIdValuesMap.containsKey(drug) ? drugIdValuesMap.get(drug) : new HashMap<>();
                Long number = value == null ? null : new Double(value * 100).longValue();
                    idValueMap.put(id, number);
                    drugIdValuesMap.put(drug, idValueMap);
            });
        }
        return convertToDoubleIntervalInMap(widthBucketForMap(drugIdValuesMap));
    }

    private <T extends HasSubjectId & HasStringId> Map<String, BarChartOptionRange<Date>> getDateIntervals(Collection<T> events,
                                                                                                           Function<T, ?> function,
                                                                                                           Boolean isPopulationOption) {
        Map<String, Long> idValuesMap = new HashMap<>();
        for (T event : events) {
            final Date apply = (Date) function.apply(event);
            idValuesMap.put(isPopulationOption ? event.getSubjectId() : event.getId(),
                    apply == null ? null : DaysUtil.truncLocalTime(apply.getTime()));
        }
        return convertToDateInterval(widthBucket(idValuesMap));
    }

    private <T extends HasSubjectId & HasStringId> Map<String, BarChartOptionRange<Double>> getDoubleIntervals(Collection<T> events,
                                                                                                               Function<T, ?> function,
                                                                                                               Boolean isPopulationOption) {
        Map<String, Long> idValuesMap = new HashMap<>();
        for (T event : events) {
            final Number number = (Number) function.apply(event);
            final Double value = number == null ? null : number.doubleValue();
            idValuesMap.put(isPopulationOption ? event.getSubjectId() : event.getId(),
                    value == null ? null : new Double(value * 100).longValue());
        }
        return convertToDoubleInterval(widthBucket(idValuesMap));
    }

    private <T extends HasSubjectId & HasStringId> Map<String, BarChartOptionRange<Long>> getLongIntervals(Collection<T> events,
                                                                                                           Function<T, ?> function,
                                                                                                           Boolean isPopulationOption) {
        Map<String, Long> idValuesMap = new HashMap<>();
        for (T event : events) {
            final Number number = (Number) function.apply(event);
            final Long value = number == null ? null : number.longValue();
            idValuesMap.put(isPopulationOption ? event.getSubjectId() : event.getId(), value);
        }
        return widthBucket(idValuesMap);
    }

    private Map<String, Map<String, BarChartOptionRange<Double>>> convertToDoubleIntervalInMap(
            Map<String, Map<String, BarChartOptionRange<Long>>> map) {

        return map.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
                en -> en.getValue().entrySet().stream().filter(e -> e.getValue() != null)
                        .collect(Collectors.toMap(Map.Entry::getKey, entry -> {
                            Long left = entry.getValue().getLeft();
                            Long right = entry.getValue().getRight();
                            return new BarChartOptionRange<>(left.doubleValue() / 100, right.doubleValue() / 100);
                        }))));
    }

    private Map<String, BarChartOptionRange<Double>> convertToDoubleInterval(Map<String, BarChartOptionRange<Long>> map) {
        return map.entrySet().stream().filter(e -> e.getValue() != null)
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> {
                    Long left = entry.getValue().getLeft();
                    Long right = entry.getValue().getRight();
                    return new BarChartOptionRange<>(left.doubleValue() / 100, right.doubleValue() / 100);
                }));
    }

    private Map<String, BarChartOptionRange<Date>> convertToDateInterval(Map<String, BarChartOptionRange<Long>> map) {
        return map.entrySet().stream().filter(e -> e.getValue() != null)
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> {
                    Date left = new Date(entry.getValue().getLeft());
                    Date right = new Date(entry.getValue().getRight());
                    return new BarChartOptionRange<>(left, right);
                }));
    }

    private Map<String, Map<String, BarChartOptionRange<Long>>> widthBucketForMap(Map<String, Map<String, Long>> subjectIdValuesMap) {
        return subjectIdValuesMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> widthBucket(e.getValue())));
    }

    private Map<String, BarChartOptionRange<Long>> widthBucket(Map<String, Long> subjectIdValuesMap) {
        Map<String, BarChartOptionRange<Long>> res = new HashMap<>();

        List<List<Long>> valuesByIntervals = new ArrayList<>(CHUNK_NUMBER + 1);
        for (int i = 0; i < CHUNK_NUMBER; i++) {
            valuesByIntervals.add(new ArrayList<>());
        }

        Collection<Long> listOfNonNullValues = subjectIdValuesMap.values().stream().filter(v -> v != null).collect(toList());
        if (CollectionUtils.isNotEmpty(listOfNonNullValues)) {
            long min = Collections.min(listOfNonNullValues);
            long max = Collections.max(listOfNonNullValues);

            Map<String, Integer> subjectIdIntervalNumberMap = new HashMap<>();
            if (max == min) {
                subjectIdValuesMap.forEach((key, value) -> {
                    if (value != null) {
                        int chunkCount = 0;
                        valuesByIntervals.get(chunkCount).add(value);
                        subjectIdIntervalNumberMap.put(key, chunkCount);
                    }
                });
            } else {
                subjectIdValuesMap.forEach((key, value) -> {
                    if (value != null) {
                        int chunkCount = getBin(CHUNK_NUMBER, value, min, max);
                        valuesByIntervals.get(chunkCount).add(value);
                        subjectIdIntervalNumberMap.put(key, chunkCount);
                    }
                });
            }

            List<BarChartOptionRange<Long>> categories = new ArrayList<>(CHUNK_NUMBER + 1);
            for (int i = 0; i < CHUNK_NUMBER; i++) {
                categories.add(BarChartOptionRange.empty());
            }

            for (int i = 0; i < CHUNK_NUMBER; i++) {
                List<Long> interval = valuesByIntervals.get(i);
                if (!CollectionUtils.isEmpty(interval)) {
                    categories.set(i, new BarChartOptionRange<>(Collections.min(interval), Collections.max(interval)));
                }
            }

            subjectIdValuesMap.forEach((key, value) -> {
                Integer chunkCount = subjectIdIntervalNumberMap.get(key);
                if (chunkCount == null) {
                    res.put(key, null);
                } else {
                    res.put(key, categories.get(chunkCount));
                }
            });
        }

        return res;
    }

    private int getBin(int binCount, long value, long min, long max) {
        final long range = max - min + 1; // we increment range value by 1 as we include max as a boundary
        final long binSize = (long) Math.ceil((double) range / binCount);
        final long abs = value - min;
        return (int) (abs / binSize);
    }
}
