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

package com.acuity.visualisations.rawdatamodel.service.plots;

import com.acuity.visualisations.rawdatamodel.trellis.grouping.CtDnaGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.CtDna;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.acuity.visualisations.rawdatamodel.vo.wrappers.CtDna.NO_MUTATIONS_DETECTED_VALUE;

/**
 * Service to build a line chart for CtDna plot
 */
@Service
public class CtDnaLineChartService extends LineChartService<CtDna, CtDnaGroupByOptions> {

    /**
     * Series that contain only VAF <= 0.002 or 'no mutations detected' must be excluded
     */
    @Override
    protected boolean isSeriesValid(List<CtDna> events) {
        return events.stream().anyMatch(e -> e.getEvent().getReportedVafCalculated() > NO_MUTATIONS_DETECTED_VALUE);
    }
}
