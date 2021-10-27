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
import com.acuity.visualisations.rawdatamodel.vo.wrappers.LiverDiag;
import com.googlecode.cqengine.query.Query;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Collection;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class LiverDiagFilters extends Filters<LiverDiag> {

    public static LiverDiagFilters empty() {
        return new LiverDiagFilters();
    }

    protected SetFilter<String> liverDiagInv = new SetFilter<>();
    protected SetFilter<String> liverDiagInvSpec = new SetFilter<>();
    protected DateRangeFilter liverDiagInvDate = new DateRangeFilter();
    protected RangeFilter<Integer> studyDayLiverDiagInv = new RangeFilter<>();
    protected SetFilter<String> liverDiagInvResult = new SetFilter<>();
    protected RangeFilter<Integer> potentialHysLawCaseNum = new RangeFilter<>();

    @Override
    public Query<LiverDiag> getQuery(Collection<String> subjectIds) {
        CombinedQueryBuilder<LiverDiag> cqb = new CombinedQueryBuilder<>(LiverDiag.class);
        if (subjectIds != null && !subjectIds.isEmpty()) {
            cqb.add(getFilterQuery(LiverDiag.Attributes.SUBJECT_ID, new SetFilter<>(subjectIds)));
        }
        return cqb
                .add(getFilterQuery(LiverDiag.Attributes.LIVER_DIAG_INVESTIGATION, liverDiagInv))
                .add(getFilterQuery(LiverDiag.Attributes.LIVER_DIAG_INVESTIGATION_SPEC, liverDiagInvSpec))
                .add(getFilterQuery(LiverDiag.Attributes.LIVER_DIAG_INVESTIGATION_DATE, liverDiagInvDate))
                .add(getFilterQuery(LiverDiag.Attributes.STUDY_DAY_LIVER_DIAG_INVESTIGATION, studyDayLiverDiagInv))
                .add(getFilterQuery(LiverDiag.Attributes.LIVER_DIAG_INVESTIGATION_RESULT, liverDiagInvResult))
                .add(getFilterQuery(LiverDiag.Attributes.POTENTIAL_HYS_LAW_CASE_NUM, potentialHysLawCaseNum))
                .build();
    }
}
