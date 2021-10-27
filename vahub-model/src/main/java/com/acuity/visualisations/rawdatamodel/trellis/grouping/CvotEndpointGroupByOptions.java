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

import com.acuity.visualisations.rawdatamodel.trellis.TrellisCategories;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.annotations.BinableOption;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.annotations.PopulationGroupingOption;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.annotations.TimestampOption;
import com.acuity.visualisations.rawdatamodel.util.Attributes;
import com.acuity.visualisations.rawdatamodel.vo.EntityAttribute;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.CvotEndpoint;

public enum CvotEndpointGroupByOptions implements GroupByOption<CvotEndpoint> {

    @PopulationGroupingOption(PopulationGroupByOptions.STUDY_CODE)
    STUDY_ID(CvotEndpoint.Attributes.STUDY_ID),
    @PopulationGroupingOption(PopulationGroupByOptions.STUDY_PART_ID)
    STUDY_PART(CvotEndpoint.Attributes.STUDY_PART),
    @PopulationGroupingOption(PopulationGroupByOptions.ACTUAL_TREATMENT_ARM)
    ARM(CvotEndpoint.Attributes.ARM),

    TERM(CvotEndpoint.Attributes.TERM),
    CATEGORY_1(CvotEndpoint.Attributes.CATEGORY_1),
    CATEGORY_2(CvotEndpoint.Attributes.CATEGORY_2),
    CATEGORY_3(CvotEndpoint.Attributes.CATEGORY_3),
    DESCRIPTION_1(CvotEndpoint.Attributes.DESCRIPTION_1),
    DESCRIPTION_2(CvotEndpoint.Attributes.DESCRIPTION_2),
    DESCRIPTION_3(CvotEndpoint.Attributes.DESCRIPTION_3),

    @BinableOption
    @TimestampOption
    START_DATE(CvotEndpoint.Attributes.START_DATE) {
        @Override
        public EntityAttribute<CvotEndpoint> getAttribute(Params params) {
            return params == null ? getAttribute() : Attributes.getBinnedAttribute("startDate", params, CvotEndpoint::getStartDate);
        }
    };

    private CvotEndpoint.Attributes originAttribute;

    CvotEndpointGroupByOptions(CvotEndpoint.Attributes originAttribute) {
        this.originAttribute = originAttribute;
    }


    @Override
    public EntityAttribute<CvotEndpoint> getAttribute() {
        return originAttribute.getAttribute();
    }

    public TrellisCategories category() {
        return TrellisCategories.NON_MANDATORY_SERIES;
    }
}
