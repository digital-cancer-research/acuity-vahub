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

import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.service.PopulationService;
import com.acuity.visualisations.rawdatamodel.service.dod.DoDCommonService;
import com.acuity.visualisations.rawdatamodel.util.Column;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.OutputSSVSummaryData;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.OutputSSVSummaryMetadata;
import com.acuity.va.security.acl.domain.Datasets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.acuity.visualisations.rawdatamodel.vo.Subject.COHORT_OTHER_FIELD_NAME;

@Service
public class SSVSummaryService {

    private static final String DEFAULT_GROUP = "Default group";
    @Autowired
    private PopulationService populationService;

    @Autowired
    private DoDCommonService doDCommonService;

    public OutputSSVSummaryMetadata getSingleSubjectMetadata(Datasets datasets) {
        FilterResult<Subject> filteredData = populationService.getFilteredData(datasets, PopulationFilters.empty());
        return OutputSSVSummaryMetadata.builder()
                .demography(getExistingPopulationColumns(datasets, filteredData, Column.Type.DEMOGRAPHY))
                .study(getExistingPopulationColumns(datasets, filteredData, Column.Type.STUDY_INFO))
                .medicalHistories(filteredData.getFilteredResult().stream()
                        .anyMatch(subject -> subject.getMedicalHistories() != null
                                && subject.getMedicalHistories().stream().anyMatch(Objects::nonNull)))
                .build();
    }

    private Map<String, String> getExistingPopulationColumns(Datasets datasets, FilterResult<Subject> filteredData, Column.Type columnType) {
        Map<String, String> availableColumns = doDCommonService
                .getDoDColumns(Column.DatasetType.fromDatasets(datasets), filteredData.getFilteredResult(), columnType);
        // We need to exclude Default group value only for cohort other column and only for SSV table (DOD should remain the same)
        if (Column.Type.STUDY_INFO.equals(columnType)
                && filteredData.getFilteredEvents()
                .stream()
                .allMatch(subject -> DEFAULT_GROUP.equals(subject.getOtherCohort()))) {
            availableColumns.remove(COHORT_OTHER_FIELD_NAME);
        }
        return availableColumns;
    }

    public OutputSSVSummaryData getSingleSubjectData(Datasets datasets, String subjectId) {
        FilterResult<Subject> filteredPopulationData = populationService.getFilteredData(datasets, PopulationFilters.empty(),
                s -> s.getId().equals(subjectId));
        Subject subject = filteredPopulationData.getFilteredResult().stream().findFirst().get();
        return OutputSSVSummaryData.builder()
                .subjectId(subject.getSubjectCode())
                .demography(getDemographyData(datasets, filteredPopulationData).get(0))
                .study(getStudyInfoData(datasets, filteredPopulationData).get(0))
                .medicalHistories(subject.getMedicalHistoriesAsString())
                .build();
    }

    private List<Map<String, String>> getDemographyData(Datasets datasets, FilterResult<Subject> filteredData) {
        return doDCommonService.getColumnData(Column.DatasetType.fromDatasets(datasets), filteredData.getFilteredResult(), Column.Type.DEMOGRAPHY);
    }

    private List<Map<String, String>> getStudyInfoData(Datasets datasets, FilterResult<Subject> filteredData) {
        return doDCommonService.getColumnData(Column.DatasetType.fromDatasets(datasets), filteredData.getFilteredResult(), Column.Type.STUDY_INFO);
    }
}
