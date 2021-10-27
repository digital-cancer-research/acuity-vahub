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

package com.acuity.visualisations.rest.model.request.liver;

import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptionsFiltered;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.LiverGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Liver;
import com.esotericsoftware.kryo.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class HysRequest extends LiverRequest {
    /**
     * Example:
     * "settings": {
     * "filterByTrellisOptions": [
     * {
     * "MEASUREMENT": "AST",
     * "ARM": "Placebo"
     * },
     * {
     * "MEASUREMENT": "AST",
     * "ARM": "SuperDex 10 mg"
     * },
     * {
     * "MEASUREMENT": "AST",
     * "ARM": "SuperDex 20 mg"
     * },
     * {
     * "MEASUREMENT": "ALT",
     * "ARM": "Placebo"
     * }
     * ]
     * }
     */
    @NotNull
    private ChartGroupByOptionsFiltered<Liver, LiverGroupByOptions> settings;
}
