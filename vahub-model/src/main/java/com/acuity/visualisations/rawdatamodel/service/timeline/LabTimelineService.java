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

import com.acuity.visualisations.common.vo.DayZeroType;
import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.filters.LabFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.service.event.LabService;
import com.acuity.visualisations.rawdatamodel.service.timeline.data.TimelineTrack;
import com.acuity.visualisations.rawdatamodel.util.Constants;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.LabRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.timeline.day.hour.DateDayHour;
import com.acuity.visualisations.rawdatamodel.vo.timeline.labs.Categories;
import com.acuity.visualisations.rawdatamodel.vo.timeline.labs.Labcodes;
import com.acuity.visualisations.rawdatamodel.vo.timeline.labs.LabsDetailsEvent;
import com.acuity.visualisations.rawdatamodel.vo.timeline.labs.LabsSummaryEvent;
import com.acuity.visualisations.rawdatamodel.vo.timeline.labs.SubjectLabsCategories;
import com.acuity.visualisations.rawdatamodel.vo.timeline.labs.SubjectLabsDetail;
import com.acuity.visualisations.rawdatamodel.vo.timeline.labs.SubjectLabsSummary;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Lab;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.SubjectAwareWrapper;
import com.acuity.va.security.acl.domain.Datasets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.acuity.visualisations.rawdatamodel.util.DayHourUtils.extractDaysHours;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service
public class LabTimelineService implements BaseEventTimelineService<Lab> {

    @Autowired
    private LabService labService;

    public List<SubjectLabsCategories> getTimelineCategories(Datasets datasets,
                                                             Filters<Lab> filters,
                                                             PopulationFilters populationFilters,
                                                             DayZeroType dayZeroType,
                                                             String dayZeroOption) {

        List<Lab> labs = getTimelineFilteredData(datasets, filters, populationFilters);


        Map<Subject, Map<String, Map<Date, List<Lab>>>> subjectCategoryDateGroups = labs.stream()
                .collect(groupingBy(Lab::getSubject,
                        groupingBy(lab -> lab.getEvent().getCategory(),
                                groupingBy(Lab::getMeasurementTimePoint))));

        return mapCompatibleTimelineCategoriesResult(subjectCategoryDateGroups, dayZeroType, dayZeroOption);
    }

    public List<SubjectLabsSummary> getTimelineSummaries(Datasets datasets,
                                                         Filters<Lab> filters,
                                                         PopulationFilters populationFilters,
                                                         DayZeroType dayZeroType,
                                                         String dayZeroOption) {

        List<Lab> labs = getTimelineFilteredData(datasets, filters, populationFilters);

        Map<Subject, Map<Date, List<Lab>>> subjectDateGroups = labs.stream()
                .collect(groupingBy(Lab::getSubject,
                        groupingBy(Lab::getMeasurementTimePoint)));

        return mapCompatibleTimelineSummaryResult(subjectDateGroups, dayZeroType, dayZeroOption);
    }

    public List<SubjectLabsDetail> getTimelineDetails(Datasets datasets,
                                                      Filters<Lab> filters,
                                                      PopulationFilters populationFilters,
                                                      DayZeroType dayZeroType,
                                                      String dayZeroOption) {

        List<Lab> labs = getTimelineFilteredData(datasets, filters, populationFilters);

        Map<Subject, Map<String, List<Lab>>> subjectLabcodeGroups = labs.stream()
                .collect(groupingBy(Lab::getSubject,
                        groupingBy(lab -> lab.getEvent().getLabCode())));

        return mapCompatibleTimelineDetailsResult(subjectLabcodeGroups, dayZeroType, dayZeroOption);
    }

    private static List<LabsSummaryEvent> mapLabsGroupedByDate(Map<Date, List<Lab>> labsGroupedByDate,
                                                               DayZeroType dayZeroType, String dayZeroOption) {
        return labsGroupedByDate.entrySet().stream().map(dateGroup -> {
            Lab sampleLab = dateGroup.getValue().get(0);
            DateDayHour start = extractDaysHours(sampleLab.getSubject(), dayZeroType, dayZeroOption,
                    sampleLab.getMeasurementTimePoint());
            LabStats stats = dateGroup.getValue().stream()
                    .collect(LabStats::new, LabStats::accept, LabStats::combine);

            LabsSummaryEvent out = new LabsSummaryEvent();
            out.setVisitNumber(sampleLab.getEvent().getVisitNumber());
            out.setStart(start);
            out.setNumAboveReferenceRange(stats.countAboveRange);
            out.setNumBelowReferenceRange(stats.countBelowRange);
            return out;

        }).sorted(Comparator.comparing(s -> s.getStart().getDate()))
                .collect(toList());
    }

    private List<SubjectLabsSummary> mapCompatibleTimelineSummaryResult(
            Map<Subject, Map<Date, List<Lab>>> subjectDateGroups, DayZeroType dayZeroType, String dayZeroOption) {
        return subjectDateGroups.entrySet().stream().map(subjectDateGroup -> {
            Subject subject = subjectDateGroup.getKey();

            List<LabsSummaryEvent> events = mapLabsGroupedByDate(subjectDateGroup.getValue(),
                    dayZeroType, dayZeroOption);

            SubjectLabsSummary out = new SubjectLabsSummary();
            out.setSubject(subject.getSubjectCode());
            out.setSubjectId(subject.getSubjectId());
            out.setSex(subject.getSex());
            out.setEvents(events);
            return out;
        }).sorted(Comparator.comparing(SubjectLabsSummary::getSubjectId))
                .collect(Collectors.toList());
    }

    private List<SubjectLabsCategories> mapCompatibleTimelineCategoriesResult(
            Map<Subject, Map<String, Map<Date, List<Lab>>>> subjectCategoryDateGroups,
            DayZeroType dayZeroType, String dayZeroOption) {
        return subjectCategoryDateGroups.entrySet().stream().map(subjectCategoryDateGroup -> {
            Subject subject = subjectCategoryDateGroup.getKey();

            List<Categories> labcodes = subjectCategoryDateGroup.getValue().entrySet().stream().map(categoryDateGroup -> {
                List<LabsSummaryEvent> events = mapLabsGroupedByDate(categoryDateGroup.getValue(),
                        dayZeroType, dayZeroOption);

                Categories out = new Categories();
                out.setCategory(categoryDateGroup.getKey());
                out.setEvents(events);
                return out;
            }).sorted(Comparator.comparing(Categories::getCategory,
                    Comparator.nullsLast(Comparator.naturalOrder())))
                    .collect(Collectors.toList());

            SubjectLabsCategories out = new SubjectLabsCategories();
            out.setSubject(subject.getSubjectCode());
            out.setSubjectId(subject.getSubjectId());
            out.setSex(subject.getSex());
            out.setLabcodes(labcodes);
            return out;
        }).sorted(Comparator.comparing(SubjectLabsCategories::getSubjectId))
                .collect(Collectors.toList());
    }

    private List<SubjectLabsDetail> mapCompatibleTimelineDetailsResult(
            Map<Subject, Map<String, List<Lab>>> subjectLabcodeGroups,
            DayZeroType dayZeroType, String dayZeroOption) {

        return subjectLabcodeGroups.entrySet().stream().map(subjectLabcodeGroup -> {
            Subject subject = subjectLabcodeGroup.getKey();

            List<Labcodes> labcodes = subjectLabcodeGroup.getValue().entrySet().stream().map(labcodeGroup -> {
                Lab sampleLab = labcodeGroup.getValue().get(0);
                List<LabsDetailsEvent> events = labcodeGroup.getValue().stream().map(lab -> {
                    DateDayHour start = extractDaysHours(subject, dayZeroType, dayZeroOption,
                            lab.getMeasurementTimePoint());

                    LabRaw labRaw = lab.getEvent();
                    LabStats stats = Stream.of(lab)
                            .collect(LabStats::new, LabStats::accept, LabStats::combine);

                    LabsDetailsEvent out = new LabsDetailsEvent();
                    out.setId(labRaw.getId());
                    out.setVisitNumber(labRaw.getVisitNumber());
                    out.setLabcode(labRaw.getLabCode());
                    out.setBaselineValue(labRaw.getBaselineValue());
                    out.setBaselineFlag(Constants.BASELINE_FLAG_YES.equals(labRaw.getBaselineFlag()));
                    out.setValueRaw(labRaw.getResultValue());
                    out.setUnitRaw(labRaw.getUnit());
                    out.setValueChangeFromBaseline(labRaw.getChangeFromBaseline());
                    out.setUnitChangeFromBaseline(labRaw.getUnit());
                    out.setValuePercentChangeFromBaseline(labRaw.getPercentChangeFromBaseline());
                    out.setUnitPercentChangeFromBaseline("%");

                    out.setNumAboveReferenceRange(stats.countAboveRange);
                    out.setNumBelowReferenceRange(stats.countBelowRange);

                    out.setStart(start);
                    return out;
                }).sorted(Comparator.comparing(s -> s.getStart().getDate()))
                        .collect(toList());

                Labcodes out = new Labcodes();
                out.setLabcode(labcodeGroup.getKey());
                out.setRefHigh(sampleLab.getRefHigh());
                out.setRefLow(sampleLab.getRefLow());
                out.setEvents(events);

                return out;

            }).sorted(Comparator.comparing(Labcodes::getLabcode))
                    .collect(Collectors.toList());

            SubjectLabsDetail out = new SubjectLabsDetail();
            out.setSubject(subject.getSubjectCode());
            out.setSubjectId(subject.getSubjectId());
            out.setSex(subject.getSex());
            out.setLabcodes(labcodes);
            return out;
        }).sorted(Comparator.comparing(SubjectLabsDetail::getSubjectId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Lab> getTimelineFilteredData(Datasets datasets, Filters filters, PopulationFilters populationFilters) {
        FilterResult<Lab> results = labService.getFilteredData(datasets,
                filters == null ? LabFilters.empty() : (LabFilters) filters, populationFilters);

        return results.getFilteredResult().stream()
                .filter(l -> l.getMeasurementTimePoint() != null)
                .filter(l -> l.getResultValue() != null)
                .collect(Collectors.toList());
    }

    @Override
    public List<Subject> getTimelineFilteredSubjects(Datasets datasets, Filters filters, PopulationFilters populationFilters) {
        return getTimelineFilteredData(datasets, filters, populationFilters)
                .stream()
                .map(SubjectAwareWrapper::getSubject)
                .distinct()
                .collect(toList());
    }

    @Override
    public TimelineTrack getTimelineTrack() {
        return TimelineTrack.LABS;
    }

    /**
     * Accumulator object to gather different Labs statistics
     */
    private static class LabStats implements Consumer<Lab> {
        private int countAboveRange;
        private int countBelowRange;

        public void accept(Lab lab) {
            if (lab.getResultValue() != null) {
                if (lab.getRefHigh() != null && lab.getResultValue() > lab.getRefHigh()) {
                    countAboveRange++;
                }
                if (lab.getRefLow() != null && lab.getResultValue() < lab.getRefLow()) {
                    countBelowRange++;
                }
            }
        }

        public void combine(LabStats other) {
            countAboveRange += other.countAboveRange;
            countBelowRange += other.countBelowRange;
        }
    }

}
