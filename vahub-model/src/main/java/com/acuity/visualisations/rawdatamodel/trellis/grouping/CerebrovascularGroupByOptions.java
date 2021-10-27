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

package com.acuity.visualisations.rawdatamodel.trellis.grouping;

import com.acuity.visualisations.rawdatamodel.trellis.grouping.annotations.BinableOption;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.annotations.TimestampOption;
import com.acuity.visualisations.rawdatamodel.util.Attributes;
import com.acuity.visualisations.rawdatamodel.vo.EntityAttribute;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Cerebrovascular;

public enum CerebrovascularGroupByOptions implements GroupByOption<Cerebrovascular> {
    EVENT_TYPE(Cerebrovascular.Attributes.EVENT_TYPE),
    PRIMARY_ISCHEMIC_STROKE(Cerebrovascular.Attributes.PRIMARY_ISCHEMIC_STROKE),
    INTRA_HEMORRHAGE_LOC(Cerebrovascular.Attributes.INTRA_HEMORRHAGE_LOC),
    SYMPTOMS_DURATION(Cerebrovascular.Attributes.SYMPTOMS_DURATION),
    TRAUMATIC(Cerebrovascular.Attributes.TRAUMATIC),
    MRS_PRIOR_STROKE(Cerebrovascular.Attributes.MRS_PRIOR_STROKE),
    MRS_DURING_STROKE_HOSP(Cerebrovascular.Attributes.MRS_DURING_STROKE_HOSP),
    MRS_CURR_VISIT_OR_90D_AFTER(Cerebrovascular.Attributes.MRS_CURR_VISIT_OR_90D_AFTER),
    @BinableOption
    @TimestampOption
    START_DATE(Cerebrovascular.Attributes.EVENT_START_DATE) {
        @Override
        public EntityAttribute<Cerebrovascular> getAttribute(Params params) {
            return params == null ? getAttribute() : Attributes.getBinnedAttribute("START_DATE", params, Cerebrovascular::getStartDate);
        }
    };

    private Cerebrovascular.Attributes originAttribute;

    CerebrovascularGroupByOptions(Cerebrovascular.Attributes attribute) {
        this.originAttribute = attribute;
    }

    @Override
    public EntityAttribute<Cerebrovascular> getAttribute() {
        return originAttribute.getAttribute();
    }

}
