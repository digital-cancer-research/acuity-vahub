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

import com.acuity.visualisations.rawdatamodel.filters.AeFilters;
import com.acuity.visualisations.rawdatamodel.filters.DrugDoseFilters;
import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.service.PopulationService;
import com.acuity.visualisations.rawdatamodel.service.ssv.SsvSummaryTableService;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Ae;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.DrugDose;
import com.acuity.va.security.acl.domain.Datasets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.acuity.visualisations.rawdatamodel.util.Column.DatasetType;
import static com.acuity.visualisations.rawdatamodel.util.Constants.NOT_IMPLEMENTED;
import static com.acuity.visualisations.rawdatamodel.util.DaysUtil.toDateTimeString;

@Service
public class DoseLimitingService extends AeService implements SsvSummaryTableService {
    @Autowired
    private PopulationService populationService;
    @Autowired
    private DrugDoseService drugDoseService;

    private static final String[] COLUMN_NAMES = {"study drug", "dose", "dose unit", "ae", "start date", "end date",
            "dlt", "protocol definition"};
    private static final String[] COLUMN_DESCRIPTIONS = {"Study drug", "Dose", "Dose unit", "AE", "Start date", "End date",
            "DLT", "Protocol definition"};

    private static final String DOSE_LIMITING_OF_AE = "Yes";
    private static final String ACTIVE_DOSING = "Active_dosing";

    @Override
    public List<Map<String, String>> getSingleSubjectData(Datasets datasets, String subjectId) {
        return getSingleSubjectData(datasets, subjectId, AeFilters.empty());
    }

    @Override
    public List<Map<String, String>> getSingleSubjectData(Datasets datasets, String subjectId, Filters<Ae> filters) {

        FilterResult<Ae> filteredData = getFilteredData(datasets, filters,
                PopulationFilters.empty(), null, s -> s.getSubjectId().equals(subjectId));

        List<Ae> dlts = filteredData.stream()
                .filter(t -> DOSE_LIMITING_OF_AE.equalsIgnoreCase(t.getEvent().getDoseLimitingToxicity()))
                .sorted(Comparator.comparing(Ae::getStartDate, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(Comparator.comparing(t -> t.getEvent().getPt(), Comparator.nullsLast(Comparator.naturalOrder()))))
                .collect(Collectors.toList());

        FilterResult<DrugDose> activeDrugDoses = drugDoseService.getFilteredData(datasets, DrugDoseFilters.empty(),
                PopulationFilters.empty(), null, t -> (subjectId.equals(t.getSubjectId())
                        && ACTIVE_DOSING.equalsIgnoreCase(t.getEvent().getPeriodType()) && t.getEvent().getStartDate() != null));

        List<Map<String, String>> doseLimitingSummary = new ArrayList<>();

        dlts
                .forEach(dlt -> {
                    Date aeStartDate = dlt.getStartDate();
                    if (aeStartDate == null) {
                        doseLimitingSummary.add(getDoseLimitingRawWithoutDrug(dlt));
                    } else {
                        List<DrugDose> limitedDrugDoses = activeDrugDoses.stream()
                                .filter(t -> {
                                    if (t.getEndDate() == null) {
                                        return !aeStartDate.before(t.getStartDate());
                                    }
                                    return !(aeStartDate.before(t.getStartDate()) || aeStartDate.after(t.getEndDate()));
                                })
                                .collect(Collectors.toList());
                        if (limitedDrugDoses.isEmpty()) {
                            doseLimitingSummary.add(getDoseLimitingRawWithoutDrug(dlt));
                        }
                        limitedDrugDoses.forEach(d -> doseLimitingSummary.add(getDoseLimitingRow(
                                d.getEvent().getDrug() == null ? "" : d.getEvent().getDrug(),
                                d.getEvent().getDose() == null ? "" : String.valueOf(d.getEvent().getDose()),
                                d.getEvent().getDoseUnit() == null ? "" : d.getEvent().getDoseUnit(),
                                dlt.getEvent().getPt() == null ? "" : dlt.getEvent().getPt(),
                                dlt.getStartDate() == null ? "" : toDateTimeString(dlt.getStartDate()),
                                dlt.getEndDate() == null ? "" : toDateTimeString(dlt.getEndDate()),
                                dlt.getEvent().getDoseLimitingToxicity() == null ? "" : dlt.getEvent().getDoseLimitingToxicity(),
                                NOT_IMPLEMENTED)));
                    }
                });

        return doseLimitingSummary;
    }

    @Override
    public Map<String, String> getSingleSubjectColumns(DatasetType datasetType) {
        return IntStream.range(0, COLUMN_NAMES.length).boxed()
                .collect(Collectors.toMap(i -> COLUMN_NAMES[i], i -> COLUMN_DESCRIPTIONS[i],
                        (i1, i2) -> i1, LinkedHashMap::new));
    }

    @Override
    public String getSsvTableName() {
        return "doseLimiting";
    }

    @Override
    public String getSsvTableDisplayName() {
        return "DOSE LIMITING TOXICITIES (DLT)";
    }

    @Override
    public String getHeaderName() {
        return "STUDY DRUG";
    }

    @Override
    public double getOrder() {
        return 14;
    }

    private Map<String, String> getDoseLimitingRow(String... values) {
        return IntStream.range(0, Integer.min(values.length, COLUMN_NAMES.length)).boxed()
                .collect(Collectors.toMap(i -> COLUMN_NAMES[i], i -> values[i]));
    }

    private Map<String, String> getDoseLimitingRawWithoutDrug(Ae ae) {
        return getDoseLimitingRow("", "", "", ae.getEvent().getPt() == null ? "" : ae.getEvent().getPt(),
                ae.getStartDate() == null ? "" : toDateTimeString(ae.getStartDate()),
                ae.getEndDate() == null ? "" : toDateTimeString(ae.getEndDate()),
                ae.getEvent().getDoseLimitingToxicity() == null ? "" : ae.getEvent().getDoseLimitingToxicity(), NOT_IMPLEMENTED);
    }
}
