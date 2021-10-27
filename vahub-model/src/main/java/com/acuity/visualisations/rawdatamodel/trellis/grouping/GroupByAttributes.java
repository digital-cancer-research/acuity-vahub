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

package com.acuity.visualisations.rawdatamodel.trellis.grouping;

import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting;
import com.acuity.visualisations.rawdatamodel.util.Attributes;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class GroupByAttributes {

    private GroupByAttributes() {
    }

    /**
     * This groups events by result of attribute calculation.<br>
     * If attribute function returns collection, event is considered to be related to a number of groups - so event might appear in more that one group.<br>
     *<br>
     * When some attribute returns collection, event should be included into multiple groups per each collection element.<br>
     *<br>
     * When more than one grouping attributes returns collection,<br>
     * grouping should include event into keys for each combination of its collections elements (essentially, cross join).<br>
     *<br>
     * <br/><B>WE DON'T SUPPORT THIS COLLECTION RESULT FEATURE FOR TRELLIS OPTIONS SO FAR,<br>
     * CAUSE TRELLIS SUPPORTS MULTI ATTRIBUTES AND IT GETS MULTIPLICATION TOO COMPLICATED.<br>
     *<br>
     * SO PLEASE DON'T USE COLLECTION ATTRIBUTES AS TRELLIS OPTIONS!</B>
     *
    * */
    public static <T, G extends Enum<G> & GroupByOption<T>> Map<GroupByKey<T, G>, Collection<T>> group(Collection<T> events,
                                                                                                       ChartGroupByOptions<T, G> groupByOptions) {
        final Map<GroupByKey<T, G>, Collection<T>> res = new LinkedHashMap<>();
        res.putAll(events.parallelStream().map(e -> {
            final GroupByKey<T, G> value = Attributes.get(groupByOptions, e);
            final Collection<GroupByKey<T, G>> groupByKeys = expandKeyNestedCollections(value);
            final Set<GroupByKey<T, G>> key = new HashSet<>();
            key.addAll(groupByKeys);
            return key.stream().collect(Collectors.toMap(k -> k, k -> e));
        }).flatMap(e -> e.entrySet().stream())
                .collect(Collectors.groupingBy(Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue,
                                Collectors.toList()))));
        return res;
    }

    /*
    * Expanding combined keys collection by found collection attributes
    * */
    public static  <T, G extends Enum<G> & GroupByOption<T>> Collection<GroupByKey<T, G>> expandKeyNestedCollections(GroupByKey<T, G> groupByKey) {
        Collection<GroupByKey<T, G>> res = new ArrayList<>();
        res.add(groupByKey);
        for (ChartGroupBySetting setting : groupByKey.getValues().keySet()) {
                res = expandKeyNestedCollections(res, setting);
        }
        return res;
    }

    /*
    * Expanding combined keys collection by specific attribute
    * */
    private static <T, G extends Enum<G> & GroupByOption<T>> Collection<GroupByKey<T, G>> expandKeyNestedCollections(
            Collection<GroupByKey<T, G>> groupByKeys, ChartGroupBySetting setting) {
        final ArrayList<GroupByKey<T, G>> res = new ArrayList<>();
        for (GroupByKey<T, G> groupByKey : groupByKeys) {
            final Object values = groupByKey.getValues().get(setting);
            if (values instanceof Collection) {
                for (Object value : (Collection) values) {
                    final GroupByKey<T, G> copy = groupByKey.copyReplacing(setting, value);
                    res.add(copy);
                }
            } else {
                res.add(groupByKey);
            }
        }
        return res;
    }

}
