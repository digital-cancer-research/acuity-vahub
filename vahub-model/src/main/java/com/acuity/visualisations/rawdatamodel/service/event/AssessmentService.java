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

package com.acuity.visualisations.rawdatamodel.service.event;

import com.acuity.visualisations.rawdatamodel.filters.AssessmentFilters;
import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.service.BaseEventService;
import com.acuity.visualisations.rawdatamodel.service.ssv.OncologyPermission;
import com.acuity.visualisations.rawdatamodel.service.ssv.SsvSummaryTableService;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.AssessmentGroupByOptions;
import com.acuity.visualisations.rawdatamodel.util.Column.DatasetType;
import com.acuity.visualisations.rawdatamodel.vo.AssessmentRaw;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Assessment;
import com.acuity.va.security.acl.domain.Datasets;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@OncologyPermission
public class AssessmentService extends BaseEventService<AssessmentRaw, Assessment, AssessmentGroupByOptions> implements SsvSummaryTableService {

    @Override
    public List<Map<String, String>> getSingleSubjectData(Datasets datasets, String subjectId) {
        return getSingleSubjectData(datasets, subjectId, AssessmentFilters.empty());
    }

    @Override
    public Map<String, String> getSingleSubjectColumns(DatasetType datasetType) {
        return ssvCommonService.getColumns(datasetType, Assessment.class, AssessmentRaw.class);
    }

    @Override
    public List<Map<String, String>> getSingleSubjectData(Datasets datasets, String subjectId, boolean hasTumourAccess) {
        return hasTumourAccess ? getSingleSubjectData(datasets, subjectId, AssessmentFilters.empty())
                : Collections.emptyList();
    }

    @Override
    public List<Map<String, String>> getSingleSubjectData(Datasets datasets, String subjectId, Filters<Assessment> filters) {
        Predicate<Assessment> predicate = assessment -> assessment.getSubjectId().equals(subjectId)
                && assessment.getEvent().getVisitDate() != null
                && assessment.getEvent().getBaselineDate() != null
                && !assessment.getEvent().getVisitDate().before(assessment.getEvent().getBaselineDate());
        final FilterResult<Assessment> filteredData = getFilteredData(datasets, filters,
                PopulationFilters.empty(), null, predicate);
        List<Assessment> sortedNtls = filteredData.stream()
                .sorted(Comparator.comparing(ntl -> ntl.getEvent().getVisitDate()))
                .collect(Collectors.toList());
        return ssvCommonService.getColumnData(DatasetType.fromDatasets(datasets), sortedNtls);
    }

    @Override
    public String getSsvTableName() {
        return "newLesion";
    }

    @Override
    public String getSsvTableDisplayName() {
        return "NEW LESIONS";
    }

    @Override
    public String getHeaderName() {
        return "RECIST RESPONSE";
    }

    @Override
    public double getOrder() {
        return 18;
    }
}
