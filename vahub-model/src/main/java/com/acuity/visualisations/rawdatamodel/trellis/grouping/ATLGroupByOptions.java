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

import com.acuity.visualisations.rawdatamodel.util.Attributes;
import com.acuity.visualisations.rawdatamodel.util.DaysUtil;
import com.acuity.visualisations.rawdatamodel.vo.EntityAttribute;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.AssessedTargetLesion;

/**
 * Assessed Target Lesion group-by options
 */
public enum ATLGroupByOptions implements GroupByOption<AssessedTargetLesion> {

    BEST_RESPONSE(AssessedTargetLesion.Attributes.BEST_RESPONSE),
    ASSESSMENT_RESPONSE(AssessedTargetLesion.Attributes.RESPONSE),
    PERCENTAGE_CHANGE(AssessedTargetLesion.Attributes.PERCENTAGE_CHANGE),
    LESION_PERCENTAGE_CHANGE(AssessedTargetLesion.Attributes.LESION_PERCENTAGE_CHANGE),
    ABSOLUTE_SUM(AssessedTargetLesion.Attributes.LESIONS_DIAMETER_PER_ASSESSMENT),
    ABSOLUTE_CHANGE(AssessedTargetLesion.Attributes.ABSOLUTE_CHANGE),
    LESION_DIAMETER(AssessedTargetLesion.Attributes.LESION_DIAMETER),
    SUBJECT_LESION(AssessedTargetLesion.Attributes.SUBJECT_LESION),
    DAYS_SINCE_FIRST_DOSE(AssessedTargetLesion.Attributes.LESION_DATE) {
        @Override
        public EntityAttribute<AssessedTargetLesion> getAttribute(Params params) {
            if (params == null || params.getParamMap().isEmpty()) {
                return getAttribute();
            } else {
                return Attributes.getBinnedAttribute("START_DATE", params,
                        t -> t.getEvent().getLesionDate());
            }
        }

        @Override
        public EntityAttribute<AssessedTargetLesion> getAttribute() {
            Params params = Params.builder()
                    .with(Param.BIN_SIZE, 1)
                    .with(Param.TIMESTAMP_TYPE, TimestampType.DAYS_SINCE_FIRST_DOSE)
                    .build();
            return getAttribute(params);
        }
    },
    ASSESSMENT_WEEK(null) {
        @Override
        public EntityAttribute<AssessedTargetLesion> getAttribute() {
            return EntityAttribute.attribute(ASSESSMENT_WEEK_ATTRIBUTE_NAME,
                    ATLGroupByOptions::getAssessmentWeekNumber);
        }
    },
    ASSESSMENT_WEEK_WITH_BASELINE(null) {
        @Override
        public EntityAttribute<AssessedTargetLesion> getAttribute() {
            return EntityAttribute.attribute(ASSESSMENT_WEEK_ATTRIBUTE_NAME,
                    t -> {
                        if (t.getEvent().isBaseline()) {
                            return "Baseline";
                        }

                        return "Week " + getAssessmentWeekNumber(t);
                    });
        }
    },
    ASSESSMENT_WEEK_WITH_INTEGER_BASELINE(null) {
        @Override
        public EntityAttribute<AssessedTargetLesion> getAttribute() {
            return EntityAttribute.attribute(ASSESSMENT_WEEK_ATTRIBUTE_NAME,
                    t -> {
                        if (t.getEvent().isBaseline()) {
                            return 0;
                        }

                        return getAssessmentWeekNumber(t);
                    });
        }
    },
    SUBJECT(AssessedTargetLesion.Attributes.SUBJECT);

    private static final String ASSESSMENT_WEEK_ATTRIBUTE_NAME = "ASSESSMENT_WEEK";

    private AssessedTargetLesion.Attributes origin;
    ATLGroupByOptions(AssessedTargetLesion.Attributes origin) {
        this.origin = origin;
    }

    @Override
    public EntityAttribute<AssessedTargetLesion> getAttribute() {
        return origin.getAttribute();
    }

    private static int getAssessmentWeekNumber(AssessedTargetLesion t) {
        int daysFromBaseline = DaysUtil.daysBetween(t.getSubject().getBaselineDate(), t.getEvent().getLesionDate()).orElse(0);
        int frequencyInWeeks = t.getEvent().getAssessmentFrequency();
        int lastAssessmentWeek = (daysFromBaseline / (frequencyInWeeks * 7)) * frequencyInWeeks;
        int halfAssessmentPeriod = frequencyInWeeks * 7 / 2;
        int daysFromLastBestAssessmentDay = daysFromBaseline - lastAssessmentWeek * 7;

        return daysFromLastBestAssessmentDay <= halfAssessmentPeriod
                ? (lastAssessmentWeek == 0 ? frequencyInWeeks : lastAssessmentWeek) : lastAssessmentWeek + frequencyInWeeks;
    }

}
