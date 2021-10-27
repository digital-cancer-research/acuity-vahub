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

package com.acuity.visualisations.rawdatamodel.filters;

import com.acuity.visualisations.rawdatamodel.util.CombinedQueryBuilder;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Cerebrovascular;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.googlecode.cqengine.query.Query;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Collection;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class CerebrovascularFilters extends Filters<Cerebrovascular> {

    private SetFilter<String> eventType = new SetFilter<>();
    private DateRangeFilter eventStartDate = new DateRangeFilter();
    private SetFilter<String> eventTerm = new SetFilter<>();
    private SetFilter<String> aeNumber = new SetFilter<>();
    private SetFilter<String> primaryIschemicStroke = new SetFilter<>();
    private SetFilter<String> traumatic = new SetFilter<>();
    private SetFilter<String> symptomsDuration = new SetFilter<>();
    private SetFilter<String> intraHemorrhageLoc = new SetFilter<>();
    private SetFilter<String> intraHemorrhageOtherLoc = new SetFilter<>();
    private SetFilter<String> mrsPriorStroke = new SetFilter<>();
    private SetFilter<String> mrsDuringStrokeHosp = new SetFilter<>();
    private SetFilter<String> mrsCurrVisitOr90DAfterStroke = new SetFilter<>();
    private SetFilter<String> comment = new SetFilter<>();

    @JsonIgnore
    public static CerebrovascularFilters empty() {
        return new CerebrovascularFilters();
    }

    @Override
    public Query<Cerebrovascular> getQuery(Collection<String> subjectIds) {
        CombinedQueryBuilder<Cerebrovascular> cqb = new CombinedQueryBuilder<>(Cerebrovascular.class);
        if (subjectIds != null && !subjectIds.isEmpty()) {
            cqb.add(getFilterQuery(Cerebrovascular.Attributes.SUBJECT_ID, new SetFilter<>(subjectIds)));
        }
        return cqb.add(getFilterQuery(Cerebrovascular.Attributes.AE_NUMBER, aeNumber)).
                add(getFilterQuery(Cerebrovascular.Attributes.EVENT_TERM, eventTerm)).
                add(getFilterQuery(Cerebrovascular.Attributes.EVENT_TYPE, eventType)).
                add(getFilterQuery(Cerebrovascular.Attributes.TRAUMATIC, traumatic)).
                add(getFilterQuery(Cerebrovascular.Attributes.INTRA_HEMORRHAGE_LOC, intraHemorrhageLoc)).
                add(getFilterQuery(Cerebrovascular.Attributes.INTRA_HEMORRHAGE_OTHER_LOC, intraHemorrhageOtherLoc)).
                add(getFilterQuery(Cerebrovascular.Attributes.COMMENT, comment)).
                add(getFilterQuery(Cerebrovascular.Attributes.MRS_CURR_VISIT_OR_90D_AFTER, mrsCurrVisitOr90DAfterStroke)).
                add(getFilterQuery(Cerebrovascular.Attributes.MRS_DURING_STROKE_HOSP, mrsDuringStrokeHosp)).
                add(getFilterQuery(Cerebrovascular.Attributes.MRS_PRIOR_STROKE, mrsPriorStroke)).
                add(getFilterQuery(Cerebrovascular.Attributes.EVENT_START_DATE, eventStartDate)).
                add(getFilterQuery(Cerebrovascular.Attributes.PRIMARY_ISCHEMIC_STROKE, primaryIschemicStroke)).
                add(getFilterQuery(Cerebrovascular.Attributes.SYMPTOMS_DURATION, symptomsDuration)).
                build();
    }
}
