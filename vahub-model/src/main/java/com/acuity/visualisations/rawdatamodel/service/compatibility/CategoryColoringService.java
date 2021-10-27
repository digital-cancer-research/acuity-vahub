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

package com.acuity.visualisations.rawdatamodel.service.compatibility;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static com.acuity.visualisations.rawdatamodel.util.Attributes.DEFAULT_EMPTY_VALUE_TO_LOWER_CASE;
import static com.acuity.visualisations.rawdatamodel.util.Constants.ALL_TO_LOWER_CASE;
import static com.acuity.visualisations.rawdatamodel.util.Constants.NULL;

public abstract class CategoryColoringService extends ColoringService {

    protected final Map<Object, Map<Object, String>> categoryColors = new ConcurrentHashMap<>();
    protected final Map<Object, AtomicInteger> categoryCounters = new ConcurrentHashMap<>();

    public String getColor(Object colorByValue, Object colorByOption) {
        String color;
        switch ((colorByValue == null ? "" : colorByValue.toString()).toLowerCase()) {
            case DEFAULT_EMPTY_VALUE_TO_LOWER_CASE:
                color = Colors.GRAY.getCode();
                break;
            case ALL_TO_LOWER_CASE:
                color = Colors.LIGHTSEAGREEN.getCode();
                break;
            default:
                color = getColorFromMap(colorByValue, colorByOption);
                break;
        }
        return color;
    }

    protected String getColorFromMap(Object colorByValue, Object colorByOption) {
        categoryColors.putIfAbsent(colorByOption, new ConcurrentHashMap<>());
        categoryCounters.putIfAbsent(colorByOption, new AtomicInteger());
        final Object categoryKey = colorByValue == null ? NULL : colorByValue.toString();
        return categoryColors.get(colorByOption).computeIfAbsent(categoryKey,
                arg -> {
                    AtomicInteger counter = categoryCounters.get(colorByOption);
                    return counter.intValue() < getStandardColors().length
                            ? getStandardColors()[counter.getAndIncrement()] : generateColor();
                });
    }

    protected String[] getStandardColors() {
        return COLORS;
    }
}
