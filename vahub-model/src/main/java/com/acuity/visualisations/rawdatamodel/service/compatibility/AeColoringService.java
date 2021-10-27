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

import java.util.stream.Stream;


@Service
public class AeColoringService {

    public static final String DARK_BLUE = "#4363D8";
    public static final String BLUE = "#88CCEE";
    public static final String LIGHT_GREEN = "#B4DA50";
    public static final String YELLOW = "#F7D533";
    public static final String ORANGE = "#FE8C01";
    public static final String RED = "#D8181C";
    public static final String BLACK = "#000000";
    public static final String PINK = "#CC6677";

    private enum SeverityGrade {

        FIRST_LEVEL("1", LIGHT_GREEN),
        SECOND_LEVEL("2", YELLOW),
        THIRD_LEVEL("3", ORANGE),
        FOURTH_LEVEL("4", RED),
        FIFTH_LEVEL("5", BLACK),
        MILD("mild", LIGHT_GREEN),
        MODERATE("moderate", YELLOW),
        SEVERE("severe", ORANGE),
        EMPTY("empty", BLUE),
        OTHER("other", PINK);

        private String typeOfDay;
        private String color;

        SeverityGrade(String typeOfDay, String color) {
            this.typeOfDay = typeOfDay;
            this.color = color;
        }

        public String getTypeOfDay() {
            return typeOfDay;
        }
        public String getColor() {
            return color;
        }


        public static Stream<SeverityGrade> stream() {
            return Stream.of(SeverityGrade.values());
        }

    }

    public String getAeColor(String severity) {
        final String sev;
        String color;
        if (severity != null) {
            sev = severity.toLowerCase();
            color = SeverityGrade.stream()
                    .filter(v -> sev.contains(v.getTypeOfDay())).findFirst().orElse(SeverityGrade.OTHER).getColor();
        } else {
            color = PINK;
        }
        return color;
    }
}
