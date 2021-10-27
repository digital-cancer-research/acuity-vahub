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

import com.acuity.visualisations.rawdatamodel.trellis.TrellisOption;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.GroupByKey;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.OutputShiftPlotEntry;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.ShiftPlotData;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedChart;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedShiftPlot;
import com.acuity.visualisations.rawdatamodel.vo.plots.ShiftPlotCalculationObject;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.X_AXIS;

@Service
public class ShiftPlotUiModelService {

    public <T, G extends Enum<G> & GroupByOption<T>> List<TrellisedShiftPlot<T, G>> toTrellisedBoxPlot(
            Map<GroupByKey<T, G>, ShiftPlotCalculationObject> shiftPlot) {
        final Map<GroupByKey<T, G>, Map<GroupByKey<T, G>, ShiftPlotCalculationObject>> groupedByTrellis = shiftPlot.entrySet()
                .stream().collect(Collectors.groupingBy(
                        e -> e.getKey().limitedByTrellisOptions(),
                        Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)
                ));

        return groupedByTrellis.entrySet().stream().map(groupByTrellisEntry -> {
            final List<TrellisOption<T, G>> trellisOptions = groupByTrellisEntry.getKey().getTrellisByValues().entrySet().stream()
                    .map(option -> TrellisOption.of(option.getKey(), option.getValue())).collect(Collectors.toList());
            final String unit = groupByTrellisEntry.getValue().values().stream()
                    .map(ShiftPlotCalculationObject::getUnit)
                    .filter(Objects::nonNull)
                    .map(Object::toString).findFirst().orElse(null);
            final List<OutputShiftPlotEntry> stats = groupByTrellisEntry.getValue().entrySet().stream()
                    .filter(e -> e.getKey().getValue(X_AXIS) instanceof Double)
                    .map(e -> OutputShiftPlotEntry.of((Double) e.getKey().getValue(X_AXIS), e.getValue()))
                    .sorted()
                    .collect(Collectors.toList());
            return new TrellisedShiftPlot<>(trellisOptions, new ShiftPlotData(unit, stats));
        }).sorted(Comparator.comparing(TrellisedChart::getTrellisByString)).collect(Collectors.toList());
    }
}
