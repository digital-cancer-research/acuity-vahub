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

import com.acuity.visualisations.rawdatamodel.service.PopulationService;
import com.acuity.visualisations.rawdatamodel.util.DaysUtil;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.va.security.acl.domain.Datasets;
import lombok.Builder;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.acuity.visualisations.rawdatamodel.util.Column.DatasetType;
import static org.apache.commons.lang3.StringUtils.defaultIfEmpty;

@Service
public class SingleSubjectViewSummaryService {

    @Autowired
    private List<SsvSummaryTableService> ssvTableServices;
    @Autowired
    private PopulationService populationService;

    private static final String FIELD_NAME_KEY = "name";
    private static final String DISPLAY_NAME_KEY = "displayName";
    private static final String VALUE_KEY = "value";

    public List<SsvTableMetadata> getMetadata(Datasets datasets, boolean hasTumourAccess) {
        return getMetadata(datasets, hasTumourAccess, null);
    }

    public List<SsvTableMetadata> getMetadata(Datasets datasets, boolean hasTumourAccess, String forTable) {
        return ssvTableServices.stream().filter(s -> forTable == null || forTable.equalsIgnoreCase(s.getSsvTableName()))
                .map(s -> SsvTableMetadata.builder().name(s.getSsvTableName())
                        .displayName(s.getSsvTableDisplayName())
                        .headerName(s.getHeaderName())
                        .subheaderName(s.getSubheaderName())
                        .order(s.getOrder())
                        .columns(s.getSingleSubjectColumns(DatasetType.fromDatasets(datasets)))
                        .hasTumourAccess(s.getClass().isAnnotationPresent(OncologyPermission.class) ? hasTumourAccess : null).build())
                .filter(s -> !s.getColumns().isEmpty())
                .sorted(Comparator.comparingDouble(SsvTableMetadata::getOrder))
                .collect(Collectors.toList());
    }

    public Map<String, List<Map<String, String>>> getData(Datasets datasets, String subjectId, boolean hasTumourAccess) {
        return getData(datasets, subjectId, hasTumourAccess, null);
    }

    public Map<String, List<Map<String, String>>> getData(Datasets datasets, String subjectId, boolean hasTumourAccess, String forTable) {
        return ssvTableServices.stream().filter(s -> forTable == null || forTable.equalsIgnoreCase(s.getSsvTableName()))
                .collect(Collectors.toMap(SsvSummaryTableService::getSsvTableName, s -> s.getSingleSubjectData(datasets, subjectId, hasTumourAccess)));
    }

    public List<Map<String, String>> getHeaderData(Datasets datasets, String subjectId) {
        Optional<Subject> subject = populationService.getSubject(datasets, subjectId);
        return subject.map(value -> getHeaderData(value).stream().filter(e -> !"".equals(e.get(FIELD_NAME_KEY)))
                .collect(Collectors.toList())).orElseGet(ArrayList::new);
    }

    List<Map<String, String>> getHeaderDataForPrinting(Subject subject) {

        List<Map<String, String>> headerData = getHeaderData(subject);

        final int nColumns = 3;

        List<Map<String, String>> data = new ArrayList<>();
        Map<String, String> row = new LinkedHashMap<>();

        for (int i = 0; i < headerData.size(); i++) {
            String value = "";
            Map<String, String> cellData = headerData.get(i);
            if (cellData.containsKey(DISPLAY_NAME_KEY) && !cellData.get(DISPLAY_NAME_KEY).isEmpty()) {
                value = String.format("%s: %s", cellData.get(DISPLAY_NAME_KEY), cellData.get(VALUE_KEY));
            }
            int colNumber = (i % nColumns) + 1;
            row.put("col" + colNumber, value);

            if ((i + 1) % nColumns == 0) {
                data.add(row);
                row = new LinkedHashMap<>();
            }
        }
        return data;
    }

    private List<Map<String, String>> getHeaderData(Subject subject) {

        List<Map<String, String>> data = new ArrayList<>();

        // row1
        data.add(getHeaderField("patientId", "PATIENT ID", defaultIfEmpty(subject.getSubjectCode(), "")));
        data.add(getHeaderField("studyDrug", "STUDY DRUG", subject.getStudyDrugs()));
        data.add(getHeaderField("startDate", "FIRST TREATMENT DAY", DaysUtil.toDisplayString(subject.getFirstTreatmentDate())));

        // row2
        data.add(getHeaderField("deathDate", "DEATH DATE", subject.getDateOfDeath() == null ? "N/A" : DaysUtil.toString(subject.getDateOfDeath())));
        data.add(getHeaderField("studyId", "STUDY ID", defaultIfEmpty(subject.getClinicalStudyCode(), "")));
        // withdrawal date must not be displayed if it was no withdrawal
        if (subject.getDateOfWithdrawal() != null) {
            data.add(getHeaderField("withdrawalDate", "WITHDRAWAL / COMPLETION DATE", DaysUtil.toString(subject.getDateOfWithdrawal())));
        } else {
            data.add(getHeaderField("withdrawalReason", "REASON FOR WITHDRAWAL / COMPLETION", defaultIfEmpty(subject.getReasonForWithdrawal(), "")));
        }

        // row3
        data.add(getHeaderField("", "", ""));
        data.add(getHeaderField("studyName", "STUDY NAME", defaultIfEmpty(subject.getClinicalStudyName(), "")));

        if (subject.getDateOfWithdrawal() != null) {
            data.add(getHeaderField("withdrawalReason", "REASON FOR WITHDRAWAL / COMPLETION", defaultIfEmpty(subject.getReasonForWithdrawal(), "")));
        } else {
            data.add(getHeaderField("", "", ""));
        }

        // row4
        data.add(getHeaderField("", "", ""));
        data.add(getHeaderField("studyPart", "STUDY PART", defaultIfEmpty(subject.getStudyPart(), "")));
        data.add(getHeaderField("", "", ""));

        data.add(getHeaderField("", "", ""));
        data.add(getHeaderField("datasetName", "DATASET", defaultIfEmpty(subject.getDatasetName(), "")));
        data.add(getHeaderField("", "", ""));

        return data;
    }

    private Map<String, String> getHeaderField(String fieldName, String displayName, String value) {

        Map<String, String> field = new LinkedHashMap<>();
        field.put(FIELD_NAME_KEY, fieldName);
        field.put(DISPLAY_NAME_KEY, displayName);
        field.put(VALUE_KEY, value);
        return field;
    }

    @Getter
    @Builder
    public static class SsvTableMetadata implements Serializable {

        private Map<String, String> columns = new LinkedHashMap<>();
        private String name;
        private String displayName;
        private String headerName;
        private String subheaderName;
        private double order;
        private Boolean hasTumourAccess;
    }
}
