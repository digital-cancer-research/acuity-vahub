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
import com.acuity.visualisations.rawdatamodel.vo.wrappers.CIEvent;

public enum CIEventGroupByOptions implements GroupByOption<CIEvent> {

    FINAL_DIAGNOSIS(CIEvent.Attributes.FINAL_DIAGNOSIS),
    ISHEMIC_SYMTOMS(CIEvent.Attributes.ISHEMIC_SYMTOMS),
    CI_SYMPTOMS_DURATION(CIEvent.Attributes.CI_SYMPTOMS_DURATION),
    DID_SYMPTOMS_PROMPT_UNS_HOSP(CIEvent.Attributes.DID_SYMPTOMS_PROMPT_UNS_HOSP),
    EVENT_SUSP_DUE_TO_STENT_THROMB(CIEvent.Attributes.EVENT_SUSP_DUE_TO_STENT_THROMB),
    PREVIOUS_ECG_AVAILABLE(CIEvent.Attributes.PREVIOUS_ECG_AVAILABLE),
    ECG_AT_THE_EVENT_TIME(CIEvent.Attributes.ECG_AT_THE_EVENT_TIME),
    WERE_LOCAL_CARDIAC_BIOMARKERS_DRAWN(CIEvent.Attributes.WERE_LOCAL_CARDIAC_BIOMARKERS_DRAWN),
    CORONARY_ANGIOGRAPHY(CIEvent.Attributes.CORONARY_ANGIOGRAPHY),
    @TimestampOption
    @BinableOption
    START_DATE(CIEvent.Attributes.START_DATE) {
        @Override
        public EntityAttribute<CIEvent> getAttribute(Params params) {
            return params == null ? getAttribute() : Attributes.getBinnedAttribute("START_DATE", params, CIEvent::getStartDate);
        }
    };

    @Override
    public EntityAttribute<CIEvent> getAttribute() {
        return originAttribute.getAttribute();
    }

    private CIEvent.Attributes originAttribute;

    CIEventGroupByOptions(CIEvent.Attributes attribute) {
        this.originAttribute = attribute;
    }
}
