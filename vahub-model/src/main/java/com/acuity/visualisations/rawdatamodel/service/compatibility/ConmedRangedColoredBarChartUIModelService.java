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


import com.acuity.visualisations.rawdatamodel.vo.compatibility.OutputBarChartEntry;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TooltipInfoOutputBarChartEntry;
import com.acuity.visualisations.rawdatamodel.vo.plots.BarChartEntry;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Conmed;
import com.google.common.collect.ImmutableMap;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.function.Supplier;

/**
 * @deprecated just a temporary solution waiting for refactoring
 */
@Service
@Deprecated
public class ConmedRangedColoredBarChartUIModelService extends RangedColoredBarChartUIModelService {

    public ConmedRangedColoredBarChartUIModelService(BarChartColoringService coloringService) {
        super(coloringService);
    }

    @Override
    protected OutputBarChartEntry mapToOutputBarchartEntry(BarChartEntry barChartEntry,
                                                           Map<String, Integer> categoriesWithIndexes,
                                                           Supplier<Double> totalSubjectsCountSupplier) {
        String atcText = ((Conmed) barChartEntry.getEventSet().iterator().next()).getEvent().getAtcText();
        return new TooltipInfoOutputBarChartEntry(barChartEntry,
                categoriesWithIndexes.get(barChartEntry.getCategory().toString()) + 1,
                ImmutableMap.of(
                        "percentOfSubjects", 100 * barChartEntry.getTotalSubjects().doubleValue() / totalSubjectsCountSupplier.get(),
                        "atcText", atcText == null ? "" : atcText));
    }
}
