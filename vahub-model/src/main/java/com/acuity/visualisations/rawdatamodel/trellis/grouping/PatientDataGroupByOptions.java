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
import com.acuity.visualisations.rawdatamodel.vo.wrappers.PatientData;

public enum PatientDataGroupByOptions implements GroupByOption<PatientData> {

    SUBJECT(PatientData.Attributes.SUBJECT),
    SUBJECT_ID(PatientData.Attributes.SUBJECT_ID),

    @TimestampOption
    @BinableOption
    MEASUREMENT_DATE(PatientData.Attributes.MEASUREMENT_DATE) {

        @Override
        public EntityAttribute<PatientData> getAttribute(Params params) {
            return Attributes.getDateAttribute("MEASUREMENT_DATE", params, e -> e.getEvent().getMeasurementDate());
        }
    };

    private PatientData.Attributes attribute;

    PatientDataGroupByOptions(PatientData.Attributes attribute) {
        this.attribute = attribute;
    }

    @Override
    public EntityAttribute<PatientData> getAttribute() {
        return attribute.getAttribute();
    }
}

