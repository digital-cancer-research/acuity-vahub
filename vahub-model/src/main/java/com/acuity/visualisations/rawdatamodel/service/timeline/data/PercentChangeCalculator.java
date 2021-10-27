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

import com.acuity.visualisations.rawdatamodel.vo.timeline.dose.DoseAndFrequency;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.OptionalDouble;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

@Slf4j
public final class PercentChangeCalculator {
    private PercentChangeCalculator() {
    }

    public static double calculate(
            Map<String, DoseAndFrequency> maxDosesByDrug,
            Map<String, DoseAndFrequency> currentDosesByDrug,
            Function<DoseAndFrequency, Double> getDosePer
    ) {
        log.trace("Max doses: {}", maxDosesByDrug);
        log.trace("Current doses: {}", currentDosesByDrug);

        Map<String, Double> percentChangesPerDrug = currentDosesByDrug.entrySet().stream()
                .collect(toMap(
                        Map.Entry::getKey,
                        e -> calculatePercentChange(getDosePer.apply(maxDosesByDrug.get(e.getKey())),
                                getDosePer.apply(e.getValue()))
                ));

        log.trace("Percent change per drug: {}", percentChangesPerDrug);

        OptionalDouble average = maxDosesByDrug.keySet().stream()
                .mapToDouble(drug -> percentChangesPerDrug.getOrDefault(drug, -100.0))
                .average();

        if (!average.isPresent()) {
            throw new IllegalStateException("Unable to calculate average value for: " + percentChangesPerDrug);
        }

        log.trace("Percent change for all drugs: {}", average.getAsDouble());

        return average.getAsDouble();
    }

    private static Double calculatePercentChange(double max, double current) {
        if (Double.compare(max, 0.0) == 0) {
            return 0.0;
        }

        return ((current - max) / max) * 100;
    }
}
