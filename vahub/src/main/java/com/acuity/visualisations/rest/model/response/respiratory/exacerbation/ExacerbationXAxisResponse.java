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

package com.acuity.visualisations.rest.model.response.respiratory.exacerbation;

import com.acuity.visualisations.rawdatamodel.axes.AxisOption;
import com.acuity.visualisations.rawdatamodel.axes.AxisOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ExacerbationGroupByOptions;

import java.util.List;

/**
 * Response for get x axis request
 */
public class ExacerbationXAxisResponse extends AxisOptions<ExacerbationGroupByOptions> {
    public ExacerbationXAxisResponse() {
        super(null, false, null);
    }

    public ExacerbationXAxisResponse(List<AxisOption<ExacerbationGroupByOptions>> axisOptions, boolean hasRandomization, List<String> drugs) {
        super(axisOptions, hasRandomization, drugs);
    }

    public ExacerbationXAxisResponse(AxisOptions<ExacerbationGroupByOptions> axisOptions) {
        super(axisOptions.getOptions(), axisOptions.isHasRandomization(), axisOptions.getDrugs());
    }
}
