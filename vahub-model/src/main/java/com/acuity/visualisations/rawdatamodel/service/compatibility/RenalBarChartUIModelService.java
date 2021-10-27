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

import com.acuity.visualisations.rawdatamodel.axes.CountType;
import com.acuity.visualisations.rawdatamodel.service.plots.vo.BarChartCalculationObject;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.GroupByKey;
import com.acuity.visualisations.rawdatamodel.util.AlphanumEmptyLastComparator;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.HasStringId;
import com.acuity.visualisations.rawdatamodel.vo.HasSubject;
import com.acuity.visualisations.rawdatamodel.vo.plots.BarChartData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.X_AXIS;

@Service
public class RenalBarChartUIModelService extends ColoredBarChartUIModelService {

    public RenalBarChartUIModelService(@Autowired BarChartColoringService coloringService) {
        super(coloringService);
    }

    @Override
    <T extends BarChartData> Comparator<T> getColorByOptionComparator() {
        return (o1, o2) -> AlphanumEmptyLastComparator.getInstance().compare(o2.getName().toString(), o1.getName().toString());
    }

    @Override
    <T extends HasStringId & HasSubject, G extends Enum<G> & GroupByOption<T>> List<String> sortCategories(
            Map<GroupByKey<T, G>, BarChartCalculationObject<T>> barChart, CountType countType) {
        return barChart.entrySet().stream().map(e -> {
            Object category = e.getKey().getValue(X_AXIS) instanceof Map
                    ? ((Map) e.getKey().getValue(X_AXIS)).keySet().iterator().next()
                    : e.getKey().getValue(X_AXIS);
            return Objects.toString(getDefaultedGroupName(category));
        }).distinct().sorted(AlphanumEmptyLastComparator.getInstance()).collect(Collectors.toList());
    }
}
