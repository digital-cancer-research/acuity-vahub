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
import com.acuity.visualisations.rawdatamodel.trellis.grouping.annotations.BinableOption;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.annotations.HasDrugOption;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.annotations.RangeOption;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.annotations.TimestampOption;
import com.acuity.visualisations.rawdatamodel.util.Attributes;
import com.acuity.visualisations.rawdatamodel.vo.EntityAttribute;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.plots.BarChartOptionRange;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Exacerbation;
import org.apache.commons.collections.MapUtils;

import java.util.Map;
import java.util.function.Function;

public enum ExacerbationGroupByOptions implements GroupByOption<Exacerbation> {

    NONE(null) {
        @Override
        public EntityAttribute<Exacerbation> getAttribute() {
            return EntityAttribute.attribute("ALL", e -> "All");
        }
    },
    STUDY_ID(Exacerbation.Attributes.STUDY_ID),
    STUDY_NAME(Exacerbation.Attributes.STUDY_NAME),
    STUDY_PART_ID(Exacerbation.Attributes.STUDY_PART_ID),
    @AcceptsAttributeContext(required = false)
    @RangeOption(RangeOption.RangeOptionType.LONG)
    DURATION_ON_STUDY(Exacerbation.Attributes.DURATION_ON_STUDY) {
        @Override
        public EntityAttribute<Exacerbation> getAttribute(Params params) {
            return getSubjectRangedAttribute(getAttribute(), "DURATION_ON_STUDY", params);
        }
    },
    @AcceptsAttributeContext(required = false)
    @RangeOption(RangeOption.RangeOptionType.DATE)
    RANDOMISATION_DATE(Exacerbation.Attributes.RANDOMISATION_DATE) {
        @Override
        public EntityAttribute<Exacerbation> getAttribute(Params params) {
            return getSubjectRangedAttribute(getAttribute(), "DATE_OF_RANDOMISATION", params);
        }
    },
    @TimestampOption(hasDuration = true)
    @BinableOption
    @AcceptsAttributeContext(required = false)
    OVERTIME_DURATION(Exacerbation.Attributes.START_DATE) {
        @Override
        public EntityAttribute<Exacerbation> getAttribute(Params params) {

            Function<Exacerbation, Object> startDate = Exacerbation::getStartDate;
            if (params != null) {
                Object bin = params.get(Param.CONTEXT);
                if (bin instanceof IntBin) {
                    return Attributes.getBinnedAttribute("OVERTIME_DURATION", params,
                            Exacerbation::getStartDate, (Bin) bin);
                }
            }

            return params == null ? getAttribute()
                    : Attributes.getBinnedAttribute("OVERTIME_DURATION", params,
                    startDate, Exacerbation::getEndDate);
        }
    },
    WITHDRAWAL(Exacerbation.Attributes.WITHDRAWAL),
    REASON_FOR_WITHDRAWAL(Exacerbation.Attributes.REASON_FOR_WITHDRAWAL),
    CENTRE(Exacerbation.Attributes.CENTRE),
    SEX(Exacerbation.Attributes.SEX),
    RACE(Exacerbation.Attributes.RACE),
    @AcceptsAttributeContext(required = false)
    @RangeOption(RangeOption.RangeOptionType.LONG)
    AGE(Exacerbation.Attributes.AGE) {
        @Override
        public EntityAttribute<Exacerbation> getAttribute(Params params) {
            return getSubjectRangedAttribute(getAttribute(), "AGE", params);
        }
    },
    @AcceptsAttributeContext(required = false)
    @RangeOption(RangeOption.RangeOptionType.DOUBLE)
    WEIGHT(Exacerbation.Attributes.WEIGHT) {
        @Override
        public EntityAttribute<Exacerbation> getAttribute(Params params) {
            return getSubjectRangedAttribute(getAttribute(), "WEIGHT", params);
        }
    },
    @AcceptsAttributeContext(required = false)
    @RangeOption(RangeOption.RangeOptionType.DOUBLE)
    HEIGHT(Exacerbation.Attributes.HEIGHT) {
        @Override
        public EntityAttribute<Exacerbation> getAttribute(Params params) {
            return getSubjectRangedAttribute(getAttribute(), "HEIGHT", params);
        }
    },
    @AcceptsAttributeContext(required = false)
    @RangeOption(RangeOption.RangeOptionType.DATE)
    FIRST_TREATMENT_DATE(Exacerbation.Attributes.FIRST_TREATMENT_DATE) {
        @Override
        public EntityAttribute<Exacerbation> getAttribute(Params params) {
            return getSubjectRangedAttribute(getAttribute(), "FIRST_TREATMENT_DATE", params);
        }
    },
    DEATH(Exacerbation.Attributes.DEATH),
    PLANNED_TREATMENT_ARM(Exacerbation.Attributes.PLANNED_TREATMENT_ARM),
    @AcceptsAttributeContext(required = false)
    @RangeOption(RangeOption.RangeOptionType.DATE)
    DATE_OF_DEATH(Exacerbation.Attributes.DATE_OF_DEATH) {
        @Override
        public EntityAttribute<Exacerbation> getAttribute(Params params) {
            return getSubjectRangedAttribute(getAttribute(), "DATE_OF_DEATH", params);
        }
    },
    @HasDrugOption
    DISCONTINUATION(Exacerbation.Attributes.DISC_MAP) {
        @Override
        public EntityAttribute<Exacerbation> getAttribute(Params params) {
            String drugName = params.getStr(Param.DRUG_NAME);
            if (drugName != null) {
                return EntityAttribute.attribute("DISCONTINUATION",
                        e -> e.getSubject().getDrugsDiscontinued().get(drugName));
            } else {
                return getAttribute();
            }
        }
    },
    @HasDrugOption
    MAIN_REASON_FOR_DISCONTINUATION(Exacerbation.Attributes.DISC_REASONS_MAP) {
        @Override
        public EntityAttribute<Exacerbation> getAttribute(Params params) {

            String drugName = params.getStr(Param.DRUG_NAME);
            if (drugName != null) {
                return EntityAttribute.attribute("MAIN_REASON_FOR_DISCONTINUATION",
                        e -> e.getSubject().getDrugDiscontinuationMainReason().get(drugName));
            } else {
                return getAttribute();
            }
        }
    },
    EXACERBATION_SEVERITY(Exacerbation.Attributes.CLASSIFICATION),
    HOSPITALISATION(Exacerbation.Attributes.HOSPITALISATION),
    EMERGENCY_ROOM_VISIT(Exacerbation.Attributes.EMERGENCY_ROOM_VISIT),
    DEPOT_CORTICOSTEROID_TREATMENT(Exacerbation.Attributes.DEPOT_CORTICOSTEROID_TREATMENT),
    INCREASED_INHALED_CORTICOSTEROID_TREATMENT(Exacerbation.Attributes.INCREASED_INHALED_CORTICOSTEROID_TREATMENT),
    SYSTEMIC_CORTICOSTEROID_TREATMENT(Exacerbation.Attributes.SYSTEMIC_CORTICOSTEROID_TREATMENT),
    ANTIBIOTICS_TREATMENT(Exacerbation.Attributes.ANTIBIOTICS_TREATMENT);

    private Exacerbation.Attributes origin;

    ExacerbationGroupByOptions(Exacerbation.Attributes origin) {
        this.origin = origin;
    }

    private static EntityAttribute<Exacerbation> getSubjectRangedAttribute(EntityAttribute<Exacerbation> attribute, String name, Params params) {
        final Map<String, BarChartOptionRange<?>> context = params == null ? null
                : (Map<String, BarChartOptionRange<?>>) params.get(Param.CONTEXT);
        return MapUtils.isEmpty(context) ? attribute
                : EntityAttribute.attribute(name, (Exacerbation exacerbation) -> context.get(exacerbation.getSubjectId()));
    }

    @Override
    public EntityAttribute<Exacerbation> getAttribute() {
        return origin.getAttribute();
    }

}

