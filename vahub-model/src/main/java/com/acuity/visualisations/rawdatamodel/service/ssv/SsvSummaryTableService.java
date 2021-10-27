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

package com.acuity.visualisations.rawdatamodel.service.ssv;

import com.acuity.va.security.acl.domain.Datasets;

import java.util.List;
import java.util.Map;

import static com.acuity.visualisations.rawdatamodel.util.Column.DatasetType;

public interface SsvSummaryTableService {

    List<Map<String, String>> getSingleSubjectData(Datasets datasets, String subjectId);

    Map<String, String> getSingleSubjectColumns(DatasetType datasetType);

    String getSsvTableName();

    String getSsvTableDisplayName();

    String getHeaderName();

    default String getSubheaderName() {
        return getSsvTableDisplayName();
    }

    double getOrder();

    default List<Map<String, String>> getSingleSubjectData(Datasets datasets, String subjectId, boolean hasTumourAccess) {
        return getSingleSubjectData(datasets, subjectId);
    }
}
