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

import static com.acuity.visualisations.rawdatamodel.service.compatibility.AeColoringService.BLACK;
import static com.acuity.visualisations.rawdatamodel.service.compatibility.AeColoringService.DARK_BLUE;
import static com.acuity.visualisations.rawdatamodel.service.compatibility.AeColoringService.LIGHT_GREEN;
import static com.acuity.visualisations.rawdatamodel.service.compatibility.AeColoringService.ORANGE;
import static com.acuity.visualisations.rawdatamodel.service.compatibility.AeColoringService.PINK;
import static com.acuity.visualisations.rawdatamodel.service.compatibility.AeColoringService.RED;
import static com.acuity.visualisations.rawdatamodel.service.compatibility.AeColoringService.YELLOW;
import static com.acuity.visualisations.rawdatamodel.util.Attributes.DEFAULT_EMPTY_VALUE_TO_LOWER_CASE;

@Service
public class BarChartColoringService extends ColoringService {

    private static final String MILD = "(a) mild";
    private static final String MODERATE = "(b) moderate";
    private static final String SEVERE = "(c) severe";
    private static final String UNKNOWN_GRADE_EXACERBATIONS_COLOUR = "#B1C8ED";

    private static String getColor(int colorIndex) {
        if (colorIndex == 0) {
            return PINK;
        }
        return COLORS[(colorIndex + 1) % COLORS.length];
    }

    public String getColor(int counter, String value) {
        String color;
        switch ((value == null ? "" : value).toLowerCase()) {
            case "all":
                color = COLORS[1];
                break;
            case "yes":
            case "y":
                color = ORANGE;
                break;
            case "no":
            case "n":
                color = DARK_BLUE;
                break;
            case DEFAULT_EMPTY_VALUE_TO_LOWER_CASE:
                color = UNKNOWN_GRADE_EXACERBATIONS_COLOUR;
                break;
            case MILD:
                color = LIGHT_GREEN;
                break;
            case MODERATE:
                color = YELLOW;
                break;
            case SEVERE:
                color = ORANGE;
                break;

            case "ckd stage 1":
                color = LIGHT_GREEN;
                break;
            case "ckd stage 2":
                color = YELLOW;
                break;

            case "ckd stage 3":
                color = ORANGE;
                break;

            case "ckd stage 4":
                color = RED;
                break;

            case "ckd stage 5":
                color = BLACK;
                break;

            default:
                color = getColor(counter);
                break;
        }
        return color;
    }
}
