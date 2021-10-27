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

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RangePlotColoringService extends ColoringService {
    public static final String LIGHT_BLUE = "#88CCEE";
    public static final String BLUE = "#00AAFF";
    private final Map<String, String> colorsByName = new ConcurrentHashMap<>();

    private String getColorFromMap(String value) {
        return this.colorsByName.computeIfAbsent(value.toLowerCase(), v -> COLORS[this.colorsByName.size() % COLORS.length]);
    }

    public String getColor(String value) {
        String color;
        switch ((value == null ? "" : value).toLowerCase()) {
            case "all":
                color = LIGHT_BLUE;
                break;
            case "placebo":
                color = BLUE;
                break;
            default:
                color = getColorFromMap(value);
                break;
        }
        return color;
    }
}
