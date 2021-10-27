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

package com.acuity.visualisations.rawdatamodel.service.timeline.data;

import com.acuity.visualisations.rawdatamodel.util.DaysUtil;
import com.acuity.visualisations.rawdatamodel.vo.HasStartEndDate;
import lombok.Value;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * It groups events together, based on their intersection in time.
 * <p>
 * See TimelineCollectorTest.class for examples
 */
public final class TimelineCollector {

    private TimelineCollector() {
    }

    public static <T extends HasStartEndDate> List<TimelineBucket<T>> collect(Collection<T> continuousEvents,
                                                                              BiFunction<T, T, Boolean> eventEquality,
                                                                              boolean adjustEndDate) {

        List<Tick<T>> ticks = bindEvents(continuousEvents, adjustEndDate);

        List<TimelineBucket<T>> resultBuckets = new ArrayList<>();

        TimelineBucket<T> bucket = new TimelineBucket<>(null);

        List<T> events = new ArrayList<>();

        for (Tick<T> tick : ticks) {
            bucket.setEndDate(tick.getDate());

            if (!tick.getStartedEvents().isEmpty()) {
                events.addAll(tick.getStartedEvents());
            }
            if (!tick.getStoppedEvents().isEmpty()) {
                events.removeAll(tick.getStoppedEvents());
            }

            if (events.isEmpty()) {
                bucket = new TimelineBucket<>(null);
            } else {
                if (aContainedInB(events, bucket.getItems(), eventEquality)
                        && aContainedInB(bucket.getItems(), events, eventEquality)) {
                    if (!tick.getStartedEvents().isEmpty()) {
                        bucket.addItems(tick.getStartedEvents());
                    }
                } else {
                    bucket = new TimelineBucket<>(tick.getDate());
                    bucket.addItems(events);
                    resultBuckets.add(bucket);
                }
            }
        }
        return resultBuckets;
    }

    private static <T extends HasStartEndDate> boolean aContainedInB(List<T> listA, List<T> listB,
                                                                     BiFunction<T, T, Boolean> eventEquality) {
        for (T itemA : listA) {
            boolean found = false;
            for (T itemB : listB) {
                if (eventEquality.apply(itemA, itemB)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }
        return !listA.isEmpty();
    }

    private static <T extends HasStartEndDate> List<Tick<T>> bindEvents(Collection<T> events,
                                                                        boolean adjustEndDate) {
        Map<Date, Tick<T>> ticks = new HashMap<>();

        events.forEach(e -> {
            //Map should contain all Dates, not Timestamps to preserve equality
            Date startDate = e.getStartDate() == null ? null
                    : new Date(e.getStartDate().getTime());
            Date stopDate = e.getEndDate() == null ? null
                    : adjustEndDate ? DaysUtil.adjustEndDate(e.getEndDate()) : new Date(e.getEndDate().getTime());
            ticks.computeIfAbsent(startDate, tick -> new Tick<>(startDate)).getStartedEvents().add(e);
            ticks.computeIfAbsent(stopDate, tick -> new Tick<>(stopDate)).getStoppedEvents().add(e);
        });

        return ticks.values().stream().sorted(Comparator.comparing(Tick::getDate,
                Comparator.nullsLast(Comparator.naturalOrder()))).collect(Collectors.toList());
    }

    @Value
    private static final class Tick<T> {
        private Date date;
        private List<T> startedEvents = new ArrayList<>();
        private List<T> stoppedEvents = new ArrayList<>();

        private Tick(Date date) {
            this.date = date;
        }
    }
}
