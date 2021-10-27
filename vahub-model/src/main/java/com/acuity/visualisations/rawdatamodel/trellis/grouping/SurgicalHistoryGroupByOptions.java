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

import com.acuity.visualisations.rawdatamodel.vo.EntityAttribute;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.SurgicalHistory;

public enum SurgicalHistoryGroupByOptions implements GroupByOption<SurgicalHistory> {

    SUBJECT(SurgicalHistory.Attributes.SUBJECT);

    private SurgicalHistory.Attributes attribute;

    SurgicalHistoryGroupByOptions(SurgicalHistory.Attributes attribute) {
        this.attribute = attribute;
    }

    @Override
    public EntityAttribute<SurgicalHistory> getAttribute() {
        return attribute.getAttribute();
    }
}
