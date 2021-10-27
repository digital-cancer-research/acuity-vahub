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
import com.acuity.visualisations.rawdatamodel.filters.SurvivalStatusFilters;
import com.acuity.visualisations.rawdatamodel.service.BaseEventService;
import com.acuity.visualisations.rawdatamodel.service.ssv.OncologyPermission;
import com.acuity.visualisations.rawdatamodel.service.ssv.SsvSummaryTableService;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.SurvivalStatusGroupByOptions;
import com.acuity.visualisations.rawdatamodel.util.Column;
import com.acuity.visualisations.rawdatamodel.vo.SurvivalStatusRaw;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.SurvivalStatus;
import com.acuity.va.security.acl.domain.Datasets;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.acuity.visualisations.rawdatamodel.util.Constants.NOT_IMPLEMENTED;

@Service
@OncologyPermission
public class SurvivalStatusService extends BaseEventService<SurvivalStatusRaw, SurvivalStatus, SurvivalStatusGroupByOptions>
        implements SsvSummaryTableService {

    @Override
    public List<Map<String, String>> getSingleSubjectData(Datasets datasets, String subjectId) {
        return getSingleSubjectData(datasets, subjectId, SurvivalStatusFilters.empty());
    }

    @Override
    public List<Map<String, String>> getSingleSubjectData(Datasets datasets, String subjectId, Filters<SurvivalStatus> eventFilters) {
        return Collections.singletonList(getSingleSubjectColumns(Column.DatasetType.fromDatasets(datasets))
                .keySet().stream().collect(Collectors.toMap(k -> k, k -> NOT_IMPLEMENTED)));
    }

    @Override
    public Map<String, String> getSingleSubjectColumns(Column.DatasetType datasetType) {
        return ssvCommonService.getColumns(datasetType, SurvivalStatus.class, SurvivalStatusRaw.class);
    }

    @Override
    public List<Map<String, String>> getSingleSubjectData(Datasets datasets, String subjectId, boolean hasTumourAccess) {
        return hasTumourAccess ? getSingleSubjectData(datasets, subjectId, SurvivalStatusFilters.empty())
                : Collections.emptyList();
    }


    @Override
    public String getSsvTableName() {
        return "survivalStatus";
    }

    @Override
    public String getSsvTableDisplayName() {
        return "SURVIVAL STATUS";
    }

    @Override
    public String getHeaderName() {
        return "FOLLOW UP";
    }

    @Override
    public String getSubheaderName() {
        return "POST IP FOLLOW UP (PFS2 & SURVIVAL)";
    }

    @Override
    public double getOrder() {
        return 21;
    }
}
