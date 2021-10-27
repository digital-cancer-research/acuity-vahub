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

import com.acuity.visualisations.rawdatamodel.axes.AxisOptions;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class AssessmentAxisOptions<T extends Enum<T>> extends AxisOptions<T> {

    private List<Integer> weeks;
    private AssessmentType[] assessmentTypes;

    public AssessmentAxisOptions(AxisOptions<T> options, List<Integer> weeks, AssessmentType[] assessmentTypes) {
        super(options.getOptions(), options.isHasRandomization(), options.getDrugs());
        this.weeks = weeks;
        this.assessmentTypes = assessmentTypes;
    }

    @JsonIgnore
    public static AssessmentType getAssessmentType(Map<GroupByOption.Param, Object> params) {
        if (params == null) {
            return AssessmentType.BEST_CHANGE;
        }
        final Object res = params.get(GroupByOption.Param.ASSESSMENT_TYPE);
        if (res == null) {
            return AssessmentType.BEST_CHANGE;
        } else if (res instanceof AssessmentAxisOptions.AssessmentType) {
            return (AssessmentType) res;
        } else if (res instanceof String) {
            return AssessmentType.valueOf((String) res);
        }
        throw new IllegalStateException("Unknown object type for AssessmentType");
    }

    public enum AssessmentType {
        BEST_CHANGE, WEEK
    }
}
