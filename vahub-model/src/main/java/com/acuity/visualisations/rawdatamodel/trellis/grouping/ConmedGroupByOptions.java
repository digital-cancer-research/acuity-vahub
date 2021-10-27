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

import com.acuity.visualisations.rawdatamodel.trellis.grouping.annotations.AcceptsAttributeContext;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.annotations.PopulationGroupingOption;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.annotations.RangeOption;
import com.acuity.visualisations.rawdatamodel.util.Attributes;
import com.acuity.visualisations.rawdatamodel.vo.EntityAttribute;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.plots.BarChartOptionRange;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Conmed;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.collections.MapUtils;

import java.util.Map;

public enum ConmedGroupByOptions implements GroupByOption<Conmed> {

    @AcceptsAttributeContext(required = false)
    @PopulationGroupingOption(PopulationGroupByOptions.ACTUAL_TREATMENT_ARM)
    ARM(Conmed.Attributes.ARM),
    ATC_CODE(Conmed.Attributes.ATC_CODE) {
        @Override
        public EntityAttribute<Conmed> getAttribute(Params params) {
            final Map<String, String> context = params == null ? null : (Map<String, String>) params.get(Param.CONTEXT);
            return MapUtils.isEmpty(context) ? getAttribute()
                    : EntityAttribute.attribute("ATC_CODE",
                    (Conmed conmed) -> conmed.getEvent().getAtcCode() == null ? ImmutableMap.of(Attributes.DEFAULT_EMPTY_VALUE, "")
                            : ImmutableMap.of(conmed.getEvent().getAtcCode(), context.get(conmed.getEvent().getAtcCode())));
        }
    },
    MEDICATION_NAME(Conmed.Attributes.MEDICATION_NAME),
    ANATOMICAL_GROUP(Conmed.Attributes.ANATOMICAL_GROUP),
    @RangeOption(RangeOption.RangeOptionType.DOUBLE)
    @AcceptsAttributeContext(required = false)
    DOSE(Conmed.Attributes.DOSE) {
        @Override
        public EntityAttribute<Conmed> getAttribute(Params params) {
            return getRangedAttribute(getAttribute(), "DOSE", params);
        }
    },
    DOSE_UNITS(Conmed.Attributes.DOSE_UNITS),
    ONGOING(Conmed.Attributes.ONGOING),
    CONMED_STARTED_PRIOR_TO_STUDY(Conmed.Attributes.CONMED_STARTED_PRIOR_TO_STUDY),
    CONMED_ENDED_PRIOR_TO_STUDY(Conmed.Attributes.CONMED_ENDED_PRIOR_TO_STUDY);

    private Conmed.Attributes attribute;

    ConmedGroupByOptions(Conmed.Attributes attribute) {
        this.attribute = attribute;
    }

    @Override
    public EntityAttribute<Conmed> getAttribute() {
        return attribute.getAttribute();
    }

    private static EntityAttribute<Conmed> getRangedAttribute(EntityAttribute<Conmed> attribute, String name, Params params) {
        final Map<String, BarChartOptionRange<?>> context = params == null ? null
                : (Map<String, BarChartOptionRange<?>>) params.get(Param.CONTEXT);
        return MapUtils.isEmpty(context) ? attribute
                : EntityAttribute.attribute(name, (Conmed conmed) -> context.get(conmed.getId()));
    }
}
