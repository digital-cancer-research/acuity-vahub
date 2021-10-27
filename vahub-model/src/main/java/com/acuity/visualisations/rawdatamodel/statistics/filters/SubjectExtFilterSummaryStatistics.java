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

package com.acuity.visualisations.rawdatamodel.statistics.filters;

import com.acuity.visualisations.rawdatamodel.filters.SubjectExtFilters;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.SubjectExt;

public class SubjectExtFilterSummaryStatistics implements FilterSummaryStatistics<SubjectExt> {

    private SubjectExtFilters subjectExtFilters = new SubjectExtFilters();
    private int count = 0;

    @Override
    public void accept(Object value) {
        count++;
        SubjectExt subjectExt = (SubjectExt) value;
        
        subjectExtFilters.getDiagnosisDate().completeWithValue(subjectExt.getEvent().getDiagnosisDate());
        subjectExtFilters.getDaysFromDiagnosisDate().completeWithValue(subjectExt.getEvent().getDaysFromDiagnosisDate());
        subjectExtFilters.getRecentProgressionDate().completeWithValue(subjectExt.getEvent().getRecentProgressionDate());

        subjectExtFilters.setMatchedItemsCount(count);
    }

    @Override
    public void combine(FilterSummaryStatistics<SubjectExt> other) {
        SubjectExtFilterSummaryStatistics otherSubjectExt = (SubjectExtFilterSummaryStatistics) other;
        
        subjectExtFilters.getDiagnosisDate().complete(otherSubjectExt.subjectExtFilters.getDiagnosisDate());
        subjectExtFilters.getDaysFromDiagnosisDate().complete(otherSubjectExt.subjectExtFilters.getDaysFromDiagnosisDate());
        subjectExtFilters.getRecentProgressionDate().complete(otherSubjectExt.subjectExtFilters.getRecentProgressionDate());

        subjectExtFilters.setMatchedItemsCount(count);
    }

    @Override
    public SubjectExtFilters getFilters() {
        return subjectExtFilters;
    }

    @Override
    public int count() {
        return count;
    }

    @Override
    public FilterSummaryStatistics<SubjectExt> build() {
        return this;
    }

    @Override
    public FilterSummaryStatistics<SubjectExt> newStatistics() {
        return new SubjectExtFilterSummaryStatistics();
    }

    @Override
    public String toString() {
        return String.format(
                "%s{subjectExtFilters=%s}",
                this.getClass().getSimpleName(),
                this.subjectExtFilters.toString());
    }
}
