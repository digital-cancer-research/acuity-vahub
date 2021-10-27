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

import com.acuity.visualisations.rawdatamodel.vo.CategoryValue;
import com.google.common.base.Splitter;
import static com.google.common.collect.Lists.newArrayList;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toList;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

/**
 *
 * @author ksnd199
 */
public final class SplitterUtil {

    private SplitterUtil() {
    }

    public static List<CategoryValue> split(String value) {
        if (value == null) {
            return newArrayList();
        }

        return Splitter.on(",").omitEmptyStrings().trimResults().splitToList(value).stream().
                map(i -> {
                    List<String> categories = Splitter.on(":").omitEmptyStrings().trimResults().splitToList(i);
                    return new CategoryValue(categories.get(0), categories.get(1));
                }).collect(toList());
    }
    
    public static Map<String, String> splitAsMap(String value) {
         if (value == null) {
            return Maps.newHashMap();
        }

        return Splitter.on(",").omitEmptyStrings().trimResults().splitToList(value).stream().
                map(i -> {
                    List<String> categories = Splitter.on(":").omitEmptyStrings().trimResults().splitToList(i);
                    return new ImmutablePair<>(categories.get(0), categories.get(1));
                }).collect(Collectors.toMap(Pair::getLeft,  Pair::getRight));        
    }
}
