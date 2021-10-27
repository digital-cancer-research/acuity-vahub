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

package com.acuity.visualisations.common.cache;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.toList;

@Data
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class ClearCacheStatus implements Serializable {

    private List<String> clearedCacheNames = new ArrayList<>();
    private List<String> retainedCacheNames = new ArrayList<>();

    public ClearCacheStatus add(ClearCacheStatus otherStatus) {
        ClearCacheStatus newCacheStatus = new ClearCacheStatus();

        List<String> allClearedCaches = Stream.concat(otherStatus.clearedCacheNames.stream(), clearedCacheNames.stream())
                .distinct().sorted().collect(toList());

        List<String> allRetainedCacheNames1 = newArrayList();
        allRetainedCacheNames1.addAll(retainedCacheNames);
        allRetainedCacheNames1.removeAll(otherStatus.clearedCacheNames);

        List<String> allRetainedCacheNames2 = newArrayList();
        allRetainedCacheNames2.addAll(otherStatus.retainedCacheNames);
        allRetainedCacheNames2.removeAll(clearedCacheNames);

        List<String> allRetainedCacheNames = Stream.concat(allRetainedCacheNames1.stream(), allRetainedCacheNames2.stream())
                .distinct().sorted().collect(toList());

        newCacheStatus.clearedCacheNames = allClearedCaches;
        newCacheStatus.retainedCacheNames = allRetainedCacheNames;

        return newCacheStatus;
    }

    public void addClearedCacheNames(List<String> otherClearedCacheNames) {
        List<String> allClearedCaches = Stream.concat(otherClearedCacheNames.stream(), clearedCacheNames.stream())
                .distinct().sorted().collect(toList());

        clearedCacheNames = allClearedCaches;
    }
}
