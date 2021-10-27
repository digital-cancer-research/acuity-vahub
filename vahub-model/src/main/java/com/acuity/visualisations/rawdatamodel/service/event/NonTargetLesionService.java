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

import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.filters.NonTargetLesionFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.service.BaseEventService;
import com.acuity.visualisations.rawdatamodel.service.ssv.OncologyPermission;
import com.acuity.visualisations.rawdatamodel.service.ssv.SsvSummaryTableService;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.NonTargetLesionGroupByOptions;
import com.acuity.visualisations.rawdatamodel.util.Column;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.NonTargetLesionRaw;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.NonTargetLesion;
import com.acuity.va.security.acl.domain.Datasets;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.acuity.visualisations.rawdatamodel.util.Column.DatasetType;

@Service
@OncologyPermission
public class NonTargetLesionService extends BaseEventService<NonTargetLesionRaw, NonTargetLesion, NonTargetLesionGroupByOptions>
        implements SsvSummaryTableService {

    @Override
    public List<Map<String, String>> getSingleSubjectData(Datasets datasets, String subjectId) {
        return getSingleSubjectData(datasets, subjectId, NonTargetLesionFilters.empty());
    }

    @Override
    public List<Map<String, String>> getSingleSubjectData(Datasets datasets, String subjectId, boolean hasTumourAccess) {
        return hasTumourAccess ? getSingleSubjectData(datasets, subjectId, NonTargetLesionFilters.empty())
                : Collections.emptyList();
    }

    @Override
    public List<Map<String, String>> getSingleSubjectData(Datasets datasets, String subjectId, Filters<NonTargetLesion> filters) {
        Predicate<NonTargetLesion> t = ntl -> ntl.getEvent().getLesionDate() != null && ntl.getEvent().getBaselineDate() != null
                && !ntl.getEvent().getLesionDate().before(ntl.getEvent().getBaselineDate());
        final FilterResult<NonTargetLesion> filteredData = getFilteredData(datasets, filters,
                PopulationFilters.empty(), null, s -> s.getSubjectId().equals(subjectId));
        List<NonTargetLesion> sortedNtls = filteredData.stream()
                .sorted(Comparator.comparing(ntl -> ntl.getEvent().getLesionDate(), Comparator.nullsLast(Comparator.naturalOrder())))
                .filter(t)
                .collect(Collectors.toList());
        return ssvCommonService.getColumnData(Column.DatasetType.fromDatasets(datasets), sortedNtls);
    }

    @Override
    public Map<String, String> getSingleSubjectColumns(DatasetType datasetType) {
        return ssvCommonService.getColumns(datasetType, NonTargetLesion.class, NonTargetLesionRaw.class);
    }

    @Override
    public String getSsvTableName() {
        return "nontargetLesion";
    }

    @Override
    public String getSsvTableDisplayName() {
        return "NON-TARGET LESION";
    }

    @Override
    public String getSubheaderName() {
        return "NON-TARGET LESION";
    }

    @Override
    public String getHeaderName() {
        return "RECIST RESPONSE";
    }

    @Override
    public double getOrder() {
        return 17;
    }
}
