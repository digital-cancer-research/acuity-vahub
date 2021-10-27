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

package com.acuity.visualisations.rawdatamodel.service.timeline;


import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.filters.PatientDataFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.service.BaseEventService;
import com.acuity.visualisations.rawdatamodel.service.timeline.data.TimelineTrack;
import com.acuity.visualisations.rawdatamodel.service.timeline.data.patient.PatientDataEvent;
import com.acuity.visualisations.rawdatamodel.service.timeline.data.patient.PatientDataEventDetails;
import com.acuity.visualisations.rawdatamodel.service.timeline.data.patient.PatientDataTests;
import com.acuity.visualisations.rawdatamodel.service.timeline.data.patient.PatientDataTestsDetails;
import com.acuity.visualisations.rawdatamodel.service.timeline.data.patient.SubjectPatientDataDetail;
import com.acuity.visualisations.rawdatamodel.service.timeline.data.patient.SubjectPatientDataSummary;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.PatientDataGroupByOptions;
import com.acuity.visualisations.rawdatamodel.util.Attributes;
import com.acuity.visualisations.rawdatamodel.util.DaysUtil;
import com.acuity.visualisations.rawdatamodel.vo.EntityAttribute;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption.Params;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption.TimestampType;
import com.acuity.visualisations.rawdatamodel.vo.PatientDataRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.timeline.day.hour.DateDayHour;
import com.acuity.visualisations.rawdatamodel.vo.timeline.day.hour.DayHourConvertor;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.PatientData;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.SubjectAwareWrapper;
import com.acuity.va.security.acl.domain.Datasets;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import static com.acuity.visualisations.common.util.ObjectConvertor.toDouble;
import static com.acuity.visualisations.rawdatamodel.vo.GroupByOption.Param.TIMESTAMP_TYPE;
import static com.acuity.visualisations.rawdatamodel.vo.GroupByOption.TimestampType.DAYS_HOURS_SINCE_FIRST_DOSE;
import static com.acuity.visualisations.rawdatamodel.vo.GroupByOption.TimestampType.DAYS_SINCE_FIRST_DOSE;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Service
public class TimelinePatientDataService
        extends BaseEventService<PatientDataRaw, PatientData, PatientDataGroupByOptions>
        implements BaseEventTimelineService<PatientData> {

    public List<SubjectPatientDataSummary> getPatientDataSummaries(Datasets datasets,
                                                                   Params xAxisParams,
                                                                   PatientDataFilters patientDataFilters,
                                                                   PopulationFilters populationFilters) {
        final FilterResult<PatientData> filteredData = getFilteredData(datasets, patientDataFilters, populationFilters);

        final Collection<PatientData> patientData = filteredData.getFilteredEvents();
        final Map<Subject, List<PatientData>> pdBySubject = patientData.stream()
                .collect(groupingBy(PatientData::getSubject));
        final Map<Subject, Map<Date, List<PatientData>>> pdBySubjectAndDate = pdBySubject.entrySet().stream()
                .collect(toMap(Map.Entry::getKey, e -> groupByDate(e.getValue())));

        return pdBySubjectAndDate.entrySet().stream()
                .map(e -> new SubjectPatientDataSummary(e.getKey().getSubjectId(),
                    e.getKey().getSubjectCode(),
                    getPatientDataEvents(e, xAxisParams)))
                .sorted(Comparator.comparing(SubjectPatientDataSummary::getSubject))
                .collect(toList());
    }

    public List<SubjectPatientDataDetail> getPatientDataDetails(Datasets datasets,
                                                                Params xAxisParams,
                                                                Set<String> subjectIds, PatientDataFilters patientDataFilters,
                                                                PopulationFilters populationFilters) {
        final FilterResult<PatientData> filteredData = getFilteredData(datasets, patientDataFilters, populationFilters);
        final Collection<PatientData> patientReportedData = filteredData.getFilteredEvents().stream()
                .filter(e -> subjectIds.contains(e.getSubjectId())).collect(toList());
        final Map<Subject, List<PatientData>> pdBySubject = patientReportedData.stream()
                .collect(groupingBy(PatientData::getSubject));
        final Map<Subject, Map<String, List<PatientData>>> byTestName = pdBySubject.entrySet().stream()
                .collect(toMap(Map.Entry::getKey, e -> groupByTestName(e.getValue())));

        return byTestName.entrySet().stream()
                .map(e -> new SubjectPatientDataDetail(e.getKey().getSubjectId(),
                        e.getKey().getSubjectCode(),
                        getPatientDataTests(e.getValue(), xAxisParams)))
                .sorted(Comparator.comparing(SubjectPatientDataDetail::getSubject))
                .collect(toList());
    }

    private Map<Date, List<PatientData>> groupByDate(List<PatientData> list) {
        return list.stream().filter(pd -> pd.getEvent().getMeasurementDate() != null)
                .collect(groupingBy(pd -> DaysUtil.truncLocalTime(pd.getEvent().getMeasurementDate())));
    }

    private Map<String, List<PatientData>> groupByTestName(List<PatientData> list) {
        return list.stream().filter(pd -> pd.getEvent().getMeasurementName() != null)
                .collect(groupingBy(t -> t.getEvent().getMeasurementName(), TreeMap::new, toList()));
    }

    // for PatientDataEvent start is calculated truncating hours of from- and to- dates
    private List<PatientDataEvent> getPatientDataEvents(Map.Entry<Subject, Map<Date, List<PatientData>>> map,
                                                        Params xAxisParams) {
        return map.getValue().entrySet().stream()
                .map(e -> {
                    PatientData groupByPatientData = new PatientData(PatientDataRaw.builder().measurementDate(e.getKey()).build(),
                            map.getKey());
                    return new PatientDataEvent(getPatientDataEventDetails(e.getValue(), xAxisParams),
                            e.getValue().size(),
                            getDateDayHour(groupByPatientData, xAxisParams, true));
                })
                .collect(toList());
    }

    private List<PatientDataTests> getPatientDataTests(Map<String, List<PatientData>> map,
                                                       Params xAxisParams) {
        return map.entrySet().stream()
                .map(e -> new PatientDataTests(e.getKey(), getPatientDataTestsDetails(e.getValue(), xAxisParams)))
                .collect(toList());
    }

    private List<PatientDataEventDetails> getPatientDataEventDetails(List<PatientData> list,
                                                                     Params xOption) {
        return list.stream().map(t -> new PatientDataEventDetails(t.getEvent().getMeasurementName(),
                t.getEvent().getValue(),
                t.getEvent().getUnit(),
                getDateDayHour(t, xOption)))
                .collect(toList());
    }

    private List<PatientDataTestsDetails> getPatientDataTestsDetails(List<PatientData> list,
                                                                     Params xAxisParams) {
        return list.stream().map(t -> new PatientDataTestsDetails(getDateDayHour(t, xAxisParams),
                t.getEvent().getValue(),
                t.getEvent().getUnit()))
                .collect(toList());
    }

    private DateDayHour getDateDayHour(PatientData patientData, Params xAxisParams) {
        return getDateDayHour(patientData, xAxisParams, false);
    }

    private DateDayHour getDateDayHour(PatientData patientData, Params xAxisParams, boolean truncateHours) {
        if (truncateHours) {
            xAxisParams = getParamsWithHoursTruncated(xAxisParams);
        }
        Double dayHour = getDayHourToMeasurementDate(patientData, xAxisParams);
        DateDayHour startDate = new DateDayHour(patientData.getEvent().getMeasurementDate(), dayHour);

        Double doseDayHour = getDayHourToMeasurementDate(patientData, truncateHours ? DAYS_SINCE_FIRST_DOSE : DAYS_HOURS_SINCE_FIRST_DOSE);
        startDate.setDoseDayHour(doseDayHour);
        startDate.setDayHourAsString(truncateHours
                ? DayHourConvertor.getDayAsString(dayHour)
                : DayHourConvertor.getDayHourAsString(dayHour));
        Double studyDay = doseDayHour >= 0 ? doseDayHour + 1 : doseDayHour;
        startDate.setStudyDayHourAsString(truncateHours
                ? DayHourConvertor.getDayAsString(studyDay)
                : DayHourConvertor.getDayHourAsString(studyDay));

        return startDate;
    }

    private Params getParamsWithHoursTruncated(Params xAxisParams) {
        final Params.ParamsBuilder builder = Params.builder();
        xAxisParams.getParamMap().entrySet().stream()
                .filter(e -> !e.getKey().equals(TIMESTAMP_TYPE)).forEach(b -> builder.with(b.getKey(), b.getValue()));
        return builder.with(TIMESTAMP_TYPE,
                TimestampType.truncateTimestampHours(xAxisParams.getTimestampType())).build();
    }

    private Double getDayHourToMeasurementDate(PatientData patientData, TimestampType timestampType) {
        Params params = Params.builder().with(TIMESTAMP_TYPE, timestampType).build();
        return getDayHourToMeasurementDate(patientData, params);
    }

    private Double getDayHourToMeasurementDate(PatientData patientData, Params params) {
        EntityAttribute<PatientData> measurementDateXOption = PatientDataGroupByOptions
                .MEASUREMENT_DATE.getAttribute(params);
        return toDouble(Attributes.get(measurementDateXOption, patientData));
    }

    @Override
    public List<PatientData> getTimelineFilteredData(Datasets datasets, Filters<PatientData> filters, PopulationFilters populationFilters) {
        return new ArrayList<>(getFilteredData(datasets,
                filters == null ? PatientDataFilters.empty() : filters, populationFilters)
                .getFilteredEvents());
    }

    @Override
    public List<Subject> getTimelineFilteredSubjects(Datasets datasets, Filters<PatientData> filters, PopulationFilters populationFilters) {
        return getFilteredData(datasets, filters, populationFilters).getFilteredEvents().stream()
                .map(SubjectAwareWrapper::getSubject).distinct().collect(toList());
    }

    @Override
    public TimelineTrack getTimelineTrack() {
        return TimelineTrack.PATIENT_DATA;
    }
}
