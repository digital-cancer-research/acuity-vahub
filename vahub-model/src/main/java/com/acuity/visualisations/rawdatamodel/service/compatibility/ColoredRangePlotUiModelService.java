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

import com.acuity.visualisations.rawdatamodel.vo.compatibility.ColoredRangeChartSeries;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.OutputRangeChartEntry;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.RangeChartSeries;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ColoredRangePlotUiModelService extends RangePlotUiModelService {
    private final RangePlotColoringService coloringService;

    public ColoredRangePlotUiModelService(RangePlotColoringService coloringService) {
        this.coloringService = coloringService;
    }

    @Override
    protected RangeChartSeries rangeChartSeries(String name, List<OutputRangeChartEntry> entries) {
        return new ColoredRangeChartSeries(name, entries, coloringService.getColor(name));
    }
}
