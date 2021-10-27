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

import com.acuity.visualisations.rawdatamodel.filters.CvotEndpointFilters;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.CvotEndpoint;

public class CvotEndpointFilterSummaryStatistics implements FilterSummaryStatistics<CvotEndpoint> {

    private CvotEndpointFilters cvotEndpointFilters = new CvotEndpointFilters();
    private int count = 0;

    @Override
    public void accept(Object value) {
        count++;
        CvotEndpoint cvotEndpoint = (CvotEndpoint) value;

        cvotEndpointFilters.getStudyId().completeWithValue(cvotEndpoint.getSubject().getClinicalStudyCode());
        cvotEndpointFilters.getStudyPart().completeWithValue(cvotEndpoint.getSubject().getStudyPart());
        cvotEndpointFilters.getSubjectId().completeWithValue(cvotEndpoint.getSubject().getSubjectId());
        cvotEndpointFilters.getAeNumber().completeWithValue(cvotEndpoint.getAeNumber());
        cvotEndpointFilters.getStartDate().completeWithValue(cvotEndpoint.getEvent().getStartDate());
        cvotEndpointFilters.getTerm().completeWithValue(cvotEndpoint.getEvent().getTerm());
        cvotEndpointFilters.getCategory1().completeWithValue(cvotEndpoint.getEvent().getCategory1());
        cvotEndpointFilters.getCategory2().completeWithValue(cvotEndpoint.getEvent().getCategory2());
        cvotEndpointFilters.getCategory3().completeWithValue(cvotEndpoint.getEvent().getCategory3());
        cvotEndpointFilters.getDescription1().completeWithValue(cvotEndpoint.getEvent().getDescription1());
        cvotEndpointFilters.getDescription2().completeWithValue(cvotEndpoint.getEvent().getDescription2());
        cvotEndpointFilters.getDescription3().completeWithValue(cvotEndpoint.getEvent().getDescription3());
        cvotEndpointFilters.setMatchedItemsCount(count);
    }

    @Override
    public void combine(FilterSummaryStatistics<CvotEndpoint> other) {

        CvotEndpointFilterSummaryStatistics otherCvot = (CvotEndpointFilterSummaryStatistics) other;
        count += otherCvot.count;

        cvotEndpointFilters.getStudyId().complete(otherCvot.cvotEndpointFilters.getStudyId());
        cvotEndpointFilters.getStudyPart().complete(otherCvot.cvotEndpointFilters.getStudyPart());
        cvotEndpointFilters.getSubjectId().complete(otherCvot.cvotEndpointFilters.getSubjectId());
        cvotEndpointFilters.getAeNumber().complete(otherCvot.cvotEndpointFilters.getAeNumber());
        cvotEndpointFilters.getStartDate().complete(otherCvot.cvotEndpointFilters.getStartDate());
        cvotEndpointFilters.getTerm().complete(otherCvot.cvotEndpointFilters.getTerm());
        cvotEndpointFilters.getCategory1().complete(otherCvot.cvotEndpointFilters.getCategory1());
        cvotEndpointFilters.getCategory2().complete(otherCvot.cvotEndpointFilters.getCategory2());
        cvotEndpointFilters.getCategory3().complete(otherCvot.cvotEndpointFilters.getCategory3());
        cvotEndpointFilters.getDescription1().complete(otherCvot.cvotEndpointFilters.getDescription1());
        cvotEndpointFilters.getDescription2().complete(otherCvot.cvotEndpointFilters.getDescription2());
        cvotEndpointFilters.getDescription3().complete(otherCvot.cvotEndpointFilters.getDescription3());
        cvotEndpointFilters.setMatchedItemsCount(count);
    }

    @Override
    public final CvotEndpointFilters getFilters() {
        return cvotEndpointFilters;
    }

    @Override
    public final int count() {
        return count;
    }

    @Override
    public CvotEndpointFilterSummaryStatistics build() {
        return this;
    }

    @Override
    public FilterSummaryStatistics<CvotEndpoint> newStatistics() {
        return new CvotEndpointFilterSummaryStatistics();
    }

    @Override
    public String toString() {
        return String.format(
                "%s{biomarkerFilters=%s}",
                this.getClass().getSimpleName(),
                this.cvotEndpointFilters.toString());
    }
}
