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

import java.util.HashMap;
import java.util.Map;

import static com.acuity.visualisations.rawdatamodel.service.compatibility.ColoringService.Colors.BLACK;
import static com.acuity.visualisations.rawdatamodel.service.compatibility.ColoringService.Colors.BLUE;
import static com.acuity.visualisations.rawdatamodel.service.compatibility.ColoringService.Colors.GRAY;
import static com.acuity.visualisations.rawdatamodel.service.compatibility.ColoringService.Colors.PURPLE;
import static com.acuity.visualisations.rawdatamodel.service.compatibility.ColoringService.Colors.RED;
import static com.acuity.visualisations.rawdatamodel.service.compatibility.ColoringService.Colors.WHITE;
import static com.acuity.visualisations.rawdatamodel.service.compatibility.ColoringService.Colors.YELLOW;

@Service
public class TumourChartColoringService extends ColoringService {

    protected static final Map<Object, Colors> ASSESSMENT_RESPONSE_COLORS = new HashMap<>();

    static {
        ASSESSMENT_RESPONSE_COLORS.put("Missing Target Lesions", YELLOW);
        ASSESSMENT_RESPONSE_COLORS.put("Partial Response", BLUE);
        ASSESSMENT_RESPONSE_COLORS.put("Stable Disease", GRAY);
        ASSESSMENT_RESPONSE_COLORS.put("Progressive Disease", RED);
        ASSESSMENT_RESPONSE_COLORS.put("Not Evaluable", BLACK);
        ASSESSMENT_RESPONSE_COLORS.put("No Assessment", BLACK);
        ASSESSMENT_RESPONSE_COLORS.put("Complete Response", PURPLE);
        ASSESSMENT_RESPONSE_COLORS.put("No Evidence of Disease", PURPLE);
    }

    public String getColor(Object value) {
        return ASSESSMENT_RESPONSE_COLORS.getOrDefault(value, WHITE).getCode();
    }

}
