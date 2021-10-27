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

import com.acuity.visualisations.rawdatamodel.filters.ChemotherapyFilters;
import com.acuity.visualisations.rawdatamodel.filters.DrugDoseFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.filters.RadiotherapyFilters;
import com.acuity.visualisations.rawdatamodel.filters.SubjectExtFilters;
import com.acuity.visualisations.rawdatamodel.filters.TherapyFilters;
import com.acuity.visualisations.rawdatamodel.service.PopulationService;
import com.acuity.visualisations.rawdatamodel.service.compatibility.TumourColumnRangeColoringService;
import com.acuity.visualisations.rawdatamodel.service.filters.ChemotherapyFilterService;
import com.acuity.visualisations.rawdatamodel.service.filters.RadiotherapyFilterService;
import com.acuity.visualisations.rawdatamodel.service.plots.TumourColumnRangeSelectionSupportService;
import com.acuity.visualisations.rawdatamodel.service.ssv.ColorInitializer;
import com.acuity.visualisations.rawdatamodel.trellis.TrellisOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptionsFiltered;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItem;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChemotherapyGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.PopulationGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.TumourTherapyGroupByOptions;
import com.acuity.visualisations.rawdatamodel.util.Attributes;
import com.acuity.visualisations.rawdatamodel.util.ColorbyCategoriesUtil;
import com.acuity.visualisations.rawdatamodel.util.Constants;
import com.acuity.visualisations.rawdatamodel.util.DaysUtil;
import com.acuity.visualisations.rawdatamodel.vo.FilterQuery;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.TumourTherapy;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.OutputColumnRangeChartData;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.OutputColumnRangeChartEntry;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.OutputMarkEntry;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedColumnRangeChart;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Chemotherapy;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.DrugDose;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Radiotherapy;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.SubjectAwareWrapper;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.SubjectExt;
import com.acuity.va.security.acl.domain.Datasets;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.COLOR_BY;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.SERIES_BY;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChemotherapyGroupByOptions.PREFERRED_MED;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.PopulationGroupByOptions.DOSE_COHORT;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.PopulationGroupByOptions.MAX_DOSE_PER_ADMIN_OF_DRUG;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.PopulationGroupByOptions.OTHER_COHORT;
import static com.acuity.visualisations.rawdatamodel.util.Constants.ALL;
import static com.acuity.visualisations.rawdatamodel.util.Constants.DEFAULT_GROUP;
import static com.acuity.visualisations.rawdatamodel.util.Constants.SUMMARY;
import static com.acuity.visualisations.rawdatamodel.util.Constants.YES;
import static com.acuity.visualisations.rawdatamodel.util.DaysUtil.weeksBetween;
import static com.acuity.visualisations.rawdatamodel.util.TrellisUtil.getTrellisOptions;
import static com.acuity.visualisations.rawdatamodel.util.TumourTherapyUtil.getLastTherapies;
import static com.acuity.visualisations.rawdatamodel.util.TumourTherapyUtil.getLastTherapiesNotCrossedAllowed;
import static com.acuity.visualisations.rawdatamodel.util.TumourTherapyUtil.getOutputColumnRangeChartEntry;
import static com.acuity.visualisations.rawdatamodel.util.TumourTherapyUtil.getTherapiesFromChemoRadio;
import static com.acuity.visualisations.rawdatamodel.util.TumourTherapyUtil.mergeTocAndTherapies;
import static com.acuity.visualisations.rawdatamodel.util.TumourTherapyUtil.straightforwardMerge;
import static com.acuity.visualisations.rawdatamodel.util.TumourTherapyUtil.withEmptyStartDatesPopulatedBySubject;
import static com.acuity.visualisations.rawdatamodel.vo.GroupByOption.Param.DRUG_NAME;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.Radiotherapy.RADIOTHERAPY_LABEL;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

@Service
public class TumourColumnRangeService implements ColorInitializer {

    @Autowired
    private ChemotherapyService chemotherapyService;
    @Autowired
    private ChemotherapyFilterService chemotherapyFilterService;
    @Autowired
    private RadiotherapyService radiotherapyService;
    @Autowired
    private RadiotherapyFilterService radiotherapyFilterService;
    @Autowired
    private PopulationService populationService;
    @Autowired
    private DrugDoseService drugDoseService;
    @Autowired
    private TumourColumnRangeColoringService coloringService;
    @Autowired
    private SubjectExtService subjectExtService;
    @Autowired
    private TumourColumnRangeSelectionSupportService selectionService;

    public List<TrellisedColumnRangeChart<Subject, PopulationGroupByOptions>>
    getTumourTherapyOnColumnRange(Datasets datasets, TherapyFilters therapyFilters, PopulationFilters populationFilters,
                                  ChartGroupByOptionsFiltered<Subject, PopulationGroupByOptions> tocSettings,
                                  ChartGroupByOptionsFiltered<TumourTherapy, TumourTherapyGroupByOptions> therapiesSettings) {

        ChartGroupByOptions.GroupByOptionAndParams colorBySetting = tocSettings.getSettings().getOptions().get(COLOR_BY);
        Map<String, TumourTherapy> timeOnCompound = getSubjectTimeOnCompound(datasets,
                populationFilters, therapyFilters);
        applyColoringToTimeOnCompoundData(timeOnCompound, colorBySetting, datasets);

        TumourTherapyGroupByOptions seriesByOption = therapiesSettings.getSettings().getOptions().get(SERIES_BY).getGroupByOption();
        Map<String, List<List<TumourTherapy>>> therapiesOfSubject = getTherapiesOfSubject(datasets, therapyFilters, populationFilters, seriesByOption);

        Map<String, List<List<TumourTherapy>>> therapiesAndTocOfSubject = mergeTocAndTherapies(timeOnCompound, therapiesOfSubject);

        OutputColumnRangeChartData columnRangePlot = new OutputColumnRangeChartData();
        // categories is a list of repeatable subject ids. Count of a subject ids corresponds to count of lines in chart for subject
        columnRangePlot.setCategories(therapiesAndTocOfSubject.entrySet().stream()
                .flatMap(e -> Collections.nCopies(e.getValue().size(), e.getKey()).stream())
                .collect(toList()));

        addDataToPlot(therapiesAndTocOfSubject, columnRangePlot);

        Collection<SubjectExt> subjectsExt = subjectExtService.getFilteredData(datasets, therapyFilters.getSubjectExtFilters(),
                populationFilters).getFilteredResult();

        final Map<String, Date> diagnosisDateBySubject = getDiagnosisDateBySubject(subjectsExt);
        final Map<String, Date> recentProgressionDateBySubject = getRecentProgressionDate(subjectsExt);
        addDateMarksToPlot(diagnosisDateBySubject, therapiesAndTocOfSubject, columnRangePlot.getDiagnosisDates());
        addDateMarksToPlot(recentProgressionDateBySubject, therapiesAndTocOfSubject, columnRangePlot.getProgressionDates());
        return columnRangePlot.getData().isEmpty() ? new ArrayList<>()
                : Collections.singletonList(new TrellisedColumnRangeChart<>(new ArrayList<>(), columnRangePlot));
    }

    private Map<String, Date> getRecentProgressionDate(Collection<SubjectExt> subjectsExt) {
        return subjectsExt.stream()
                .filter(t -> t.getEvent().getRecentProgressionDate() != null)
                .collect(toMap(SubjectAwareWrapper::getSubjectCode,
                        s -> s.getEvent().getRecentProgressionDate(),
                        (d1, d2) -> d1.after(d2) ? d1 : d2));
    }

    private Map<String, Date> getDiagnosisDateBySubject(Collection<SubjectExt> subjectsExt) {
        return subjectsExt.stream()
                .filter(s -> s.getEvent().getDiagnosisDate() != null)
                .collect(toMap(SubjectAwareWrapper::getSubjectCode,
                        s -> s.getEvent().getDiagnosisDate(),
                        (d1, d2) -> d1.before(d2) ? d1 : d2));
    }

    private void addDateMarksToPlot(Map<String, Date> recentProgressionDateBySubject,
                                    Map<String, List<List<TumourTherapy>>> therapiesAndTocOfSubject,
                                    List<OutputMarkEntry> outputMarkEntries) {
        AtomicInteger x = new AtomicInteger(0);
        therapiesAndTocOfSubject.forEach((key, values) -> {
            final TumourTherapy tumourTherapy = values.get(0).get(0);
            final Date recentProgressionDate = recentProgressionDateBySubject.get(tumourTherapy.getSubjectCode());
            if (recentProgressionDate != null) {
                String label = DaysUtil.toString(recentProgressionDate);
                int y = weeksBetween(tumourTherapy.getSubject().getFirstTreatmentDate(), recentProgressionDate).orElse(0);
                final OutputMarkEntry outputMarkEntry =
                        new OutputMarkEntry(x.get(), y, label);
                outputMarkEntries.add(outputMarkEntry);
                x.addAndGet(values.size());
            } else {
                x.addAndGet(values.size());
            }
        });
    }

    private void addDataToPlot(Map<String, List<List<TumourTherapy>>> therapiesAndTocOfSubject, OutputColumnRangeChartData columnRangePlot) {
        AtomicInteger rank = new AtomicInteger(0);
        therapiesAndTocOfSubject.forEach((subject, therapies) -> therapies.forEach(therapy -> {
            therapy.forEach(v -> {
                OutputColumnRangeChartEntry chartEntry = getOutputColumnRangeChartEntry(rank.get(), v);
                columnRangePlot.getData().add(chartEntry);
            });
            rank.incrementAndGet();
        }));
    }

    private Map<String, TumourTherapy> getSubjectTimeOnCompound(Datasets datasets, PopulationFilters populationFilters,
                                                                TherapyFilters therapyFilters) {

        Map<String, List<DrugDose>> dosesBySubject = getDosesBySubject(datasets, populationFilters);

        FilterResult<SubjectExt> filteredData = subjectExtService.getFilteredData(datasets, therapyFilters.getSubjectExtFilters(),
                populationFilters);
        return filteredData.stream()
                .filter(s -> s.getSubject().getFirstTreatmentDate() != null)
                .collect(toMap(SubjectAwareWrapper::getSubjectCode,
                        (SubjectExt s) -> getTOCTherapyForSubject(s.getSubject(), dosesBySubject.getOrDefault(s.getSubjectCode(),
                                new ArrayList<>())),
                        (s1, s2) -> s1,
                        () -> new TreeMap<>(Collections.reverseOrder())));
    }

    private Map<String, List<DrugDose>> getDosesBySubject(Datasets datasets, PopulationFilters populationFilters) {
        return drugDoseService.getFilteredDataForTumourColumnRangeService(datasets, DrugDoseFilters.empty(), populationFilters,
                DrugDose::isActive).stream().collect(groupingBy(DrugDose::getSubjectCode));
    }

    private TumourTherapy getTOCTherapyForSubject(Subject s, List<DrugDose> doses) {
        TumourTherapy therapyWithDoses = TumourTherapy.from(s);
        therapyWithDoses.setDoses(doses);
        therapyWithDoses.setDrugs(s.getDrugsDosed().entrySet().stream()
                .filter(d -> YES.equalsIgnoreCase(d.getValue()))
                .map(Map.Entry::getKey)
                .collect(toSet()));
        return therapyWithDoses;
    }

    private void applyColoringToTimeOnCompoundData(Map<String, TumourTherapy> timeOnCompound,
                                                   ChartGroupByOptions.GroupByOptionAndParams colorBySetting,
                                                   Datasets datasets) {
        timeOnCompound.values().forEach(e -> {
                    Object colorByOption;
                    String colorByValue;
                    if (colorBySetting == null) {
                        colorByOption = ALL;
                        colorByValue = ALL;
                     } else {
                        colorByOption = colorBySetting.getGroupByOption();
                        colorByValue = Objects.toString(Attributes.get(colorBySetting, e.getSubject()));
                    }
                    colorByOption = ColorbyCategoriesUtil.getDatasetColorByOption(datasets, colorByOption);
                    String color = coloringService.getColor(colorByValue, colorByOption);
                    e.setName(colorByValue);
                    e.setColor(color);
                }
        );
    }

    private Map<String, List<List<TumourTherapy>>> getTherapiesOfSubject(Datasets datasets, TherapyFilters therapyFilters,
                                                                         PopulationFilters populationFilters,
                                                                         TumourTherapyGroupByOptions seriesByOption) {

        final boolean isMostRecentTherapy = TumourTherapyGroupByOptions.MOST_RECENT_THERAPY.equals(seriesByOption);
        return isMostRecentTherapy
                ? getSubjectLastTherapy(datasets, therapyFilters, populationFilters).entrySet().stream()
                .collect(toMap(Map.Entry::getKey, t -> Collections.singletonList(t.getValue())))
                : getAllSubjectTherapy(datasets, therapyFilters, populationFilters);
    }

    public Map<String, List<TumourTherapy>> getSubjectLastTherapy(Datasets datasets, TherapyFilters therapyFilters,
                                                             PopulationFilters populationFilters) {

        List<TumourTherapy> therapies = getPreviousTherapies(datasets, ChemotherapyFilters.empty(),
                RadiotherapyFilters.empty(), populationFilters);
        Map<String, TumourTherapy> lastTherapiesNotFiltered = getLastTherapies(therapies);

        Map<String, List<Chemotherapy>> filteredChemotherapy = chemotherapyService.getFilteredData(datasets,
                therapyFilters.getChemotherapyFilters(), populationFilters).stream()
                .collect(groupingBy(SubjectAwareWrapper::getSubjectCode));
        Map<String, List<Radiotherapy>> filteredRadiotherapy = radiotherapyService.getFilteredData(datasets,
                therapyFilters.getRadiotherapyFilters(), populationFilters).stream()
                .collect(groupingBy(SubjectAwareWrapper::getSubjectCode));

        Map<String, TumourTherapy> withFilteredEvents = lastTherapiesNotFiltered.entrySet().stream().peek(e -> {
            List<Chemotherapy> filteredSubjectLastChemotherapy = e.getValue().getPreviousChemoTherapies().stream()
                    .filter(ch -> filteredChemotherapy.getOrDefault(e.getKey(), new ArrayList<>()).contains(ch)).collect(toList());
            List<Radiotherapy> filteredSubjectLastRadiotherapy = e.getValue().getPreviousRadioTherapies().stream()
                    .filter(ch -> filteredRadiotherapy.getOrDefault(e.getKey(), new ArrayList<>()).contains(ch)).collect(toList());
            e.getValue().setPreviousChemoTherapies(filteredSubjectLastChemotherapy);
            e.getValue().setPreviousRadioTherapies(filteredSubjectLastRadiotherapy);
        }).collect(toMap(Map.Entry::getKey, Map.Entry::getValue));

        List<TumourTherapy> therapiesFiltered = getTherapiesFromChemoRadio(withFilteredEvents.values().stream()
                        .flatMap(th -> th.getPreviousChemoTherapies().stream()).collect(toList()),
                withFilteredEvents.values().stream().flatMap(th -> th.getPreviousRadioTherapies().stream()).collect(toList()));

        final Map<String, List<TumourTherapy>> withStartDatesPopulated = withEmptyStartDatesPopulatedBySubject(therapiesFiltered);
        return getLastTherapiesNotCrossedAllowed(withStartDatesPopulated);
    }

    private Map<String, List<List<TumourTherapy>>> getAllSubjectTherapy(Datasets datasets, TherapyFilters therapyFilters,
                                                                        PopulationFilters populationFilters) {

        List<TumourTherapy> therapies = getPreviousTherapies(datasets, therapyFilters.getChemotherapyFilters(),
                therapyFilters.getRadiotherapyFilters(), populationFilters);
        Map<String, List<TumourTherapy>> withStartDatesPopulatedBySubject = withEmptyStartDatesPopulatedBySubject(therapies);
        return groupAllTherapies(withStartDatesPopulatedBySubject, datasets);
    }

    private List<TumourTherapy> getPreviousTherapies(Datasets datasets, ChemotherapyFilters chemotherapyFilters,
                                                                         RadiotherapyFilters radiotherapyFilters,
                                                                         PopulationFilters populationFilters) {
        FilterResult<Chemotherapy> chemotherapy = chemotherapyService.getFilteredData(datasets,
                chemotherapyFilters, populationFilters);
        FilterResult<Radiotherapy> radiotherapy = radiotherapyService.getFilteredData(datasets,
                radiotherapyFilters, populationFilters);
        return getTherapiesFromChemoRadio(chemotherapy.getFilteredEvents(),
                radiotherapy.getFilteredEvents());
    }

    public List<TrellisOptions<PopulationGroupByOptions>> getTOCColorBy(Datasets datasets, PopulationFilters populationFilters) {

        FilterResult<Subject> filtered = populationService.getFilteredData(datasets, populationFilters);

        List<TrellisOptions<PopulationGroupByOptions>> tocColorBy = getTrellisOptions(filtered.getFilteredResult(), DOSE_COHORT, OTHER_COHORT);

        Map<PopulationGroupByOptions, List> tocColorByNotFiltered = getTrellisOptions(filtered.getAllEvents(), DOSE_COHORT, OTHER_COHORT)
                .stream().collect(toMap(TrellisOptions::getTrellisedBy, TrellisOptions::getTrellisOptions));
        final Set<String> drugs = filtered.getFilteredResult().stream()
                .map(Subject::getDrugsDosed)
                .flatMap(subjectDrugsDosed -> subjectDrugsDosed.entrySet().stream())
                .filter(drugDosed -> Constants.YES.equals(drugDosed.getValue()))
                .map(Map.Entry::getKey)
                .collect(toSet());

        List<TrellisOptionsWithDrug> trellisOptionsWithDrugs = drugs.stream().map(drug -> {
            List<TrellisOptions<PopulationGroupByOptions>> optionsPerDrug = getTrellisOptions(filtered.getFilteredResult(),
                    MAX_DOSE_PER_ADMIN_OF_DRUG.<Subject, PopulationGroupByOptions>getGroupByOptionAndParams(
                            GroupByOption.Params.builder().with(DRUG_NAME, drug).build()));
            return new TrellisOptionsWithDrug(drug, MAX_DOSE_PER_ADMIN_OF_DRUG, optionsPerDrug
                    .stream()
                    .flatMap(o -> o.getTrellisOptions().stream()).collect(toList()));
        }).collect(toList());

        // Coloring option DOSE_COHORT, OTHER_COHORT must be filtered out, if the whole dataset contains nothing but
        // "Default group" value (it means that cohorts are not set up)
        final List<TrellisOptions<PopulationGroupByOptions>> result = tocColorBy.stream()
                .filter(options -> {
                    List optionsNotFiltered = tocColorByNotFiltered.get(options.getTrellisedBy());
                    optionsNotFiltered.forEach(o -> coloringService.getColor(o, options.getTrellisedBy()));
                    return !(optionsNotFiltered.size() == 1 && optionsNotFiltered.contains(DEFAULT_GROUP));
                })
                .collect(toList());
        result.addAll(trellisOptionsWithDrugs);
        return result;
    }

    public TherapyFilters getAvailableTherapyFilters(Datasets datasets, TherapyFilters therapyFilters,
                                                     PopulationFilters populationFilters,
                                                     ChartGroupByOptionsFiltered<TumourTherapy, TumourTherapyGroupByOptions> therapiesSettings) {

        final boolean isMostRecentTherapy = TumourTherapyGroupByOptions.MOST_RECENT_THERAPY
                .equals(therapiesSettings.getSettings().getOptions().get(SERIES_BY).getGroupByOption());
        Collection<Subject> population = subjectExtService.getFilteredData(datasets, therapyFilters.getSubjectExtFilters(), populationFilters)
                .stream().map(SubjectAwareWrapper::getSubject).collect(toList());
        if (population.isEmpty()) {
            return TherapyFilters.empty();
        }
        population.forEach(s -> populationFilters.getSubjectId().completeWithValue(s.getSubjectCode()));

        Collection<TumourTherapy> therapies;
        if (isMostRecentTherapy) {
            therapies = getSubjectLastTherapy(datasets, therapyFilters, populationFilters).values().stream()
                    .flatMap(List::stream)
                    .collect(Collectors.toList());
        } else {
            therapies = getPreviousTherapies(datasets, therapyFilters.getChemotherapyFilters(),
                    therapyFilters.getRadiotherapyFilters(), populationFilters);
        }
        Collection<Chemotherapy> chemos = therapies.stream().flatMap(th -> th.getPreviousChemoTherapies().stream()).collect(toList());
        Collection<Radiotherapy> radios = therapies.stream().flatMap(th -> th.getPreviousRadioTherapies().stream()).collect(toList());

        ChemotherapyFilters chemotherapyFilters = (ChemotherapyFilters) chemotherapyFilterService.getAvailableFilters(
                new FilterQuery<>(chemos, therapyFilters.getChemotherapyFilters(), population, populationFilters));
        RadiotherapyFilters radiotherapyFilters = (RadiotherapyFilters) radiotherapyFilterService.getAvailableFilters(
                new FilterQuery<>(radios, therapyFilters.getRadiotherapyFilters(), population, populationFilters));
        SubjectExtFilters subjectExtFilters = (SubjectExtFilters) subjectExtService.getAvailableFilters(datasets,
                therapyFilters.getSubjectExtFilters(), populationFilters);
        TherapyFilters availableTherapyFilters = TherapyFilters.fromFilters(chemotherapyFilters, radiotherapyFilters,
                subjectExtFilters);

        int totalNumberOfEvents = getTotalNumberOfEvents(datasets, populationFilters, population, therapies, isMostRecentTherapy);
        availableTherapyFilters.setMatchedItemsCount(totalNumberOfEvents);

        return availableTherapyFilters;
    }

    public List<String> getSubjects(Datasets datasets, TherapyFilters therapyFilters, PopulationFilters populationFilters) {
        return subjectExtService.getFilteredData(datasets, therapyFilters.getSubjectExtFilters(),
                populationFilters)
                .stream().map(SubjectAwareWrapper::getSubjectId).collect(toList());
    }

    public SelectionDetail getSelectionDetails(Datasets datasets, TherapyFilters therapyFilters,
                                               PopulationFilters populationFilters,
                                               ChartSelection<TumourTherapy, TumourTherapyGroupByOptions,
                                                       ChartSelectionItem<TumourTherapy, TumourTherapyGroupByOptions>> selection) {

        TumourTherapyGroupByOptions seriesByOption = selection.getSettings().getOptions().get(SERIES_BY).getGroupByOption();

        Map<String, List<List<TumourTherapy>>> subjectTherapies = getTherapiesOfSubject(datasets, therapyFilters, populationFilters, seriesByOption);

        Map<String, TumourTherapy> timeOnCompound = getSubjectTimeOnCompound(datasets, populationFilters, therapyFilters);
        SelectionDetail selectionDetails = selectionService.getSelectionDetails(Stream.concat(timeOnCompound.values().stream(),
                subjectTherapies.values().stream()
                        .flatMap(therapiesOfSubject -> therapiesOfSubject.stream()
                                .flatMap(Collection::stream)
                                .filter(t -> !SUMMARY.equals(t.getName()))
                        ))
                .collect(toList()), selection);

        selectionDetails.setTotalSubjects(populationService.getFilteredData(datasets, populationFilters).size());
        return selectionDetails;
    }

    private int getTotalNumberOfEvents(Datasets datasets, PopulationFilters populationFilters, Collection<Subject> population,
                                       Collection<TumourTherapy> therapies, boolean isMostRecent) {
        Map<String, List<DrugDose>> dosesBySubject = getDosesBySubject(datasets, populationFilters);
        int totalDosesEvents = population.stream()
                .mapToInt(s -> dosesBySubject.getOrDefault(s.getSubjectCode(), new ArrayList<>()).size()).sum();
        int totalPreviousTherapyEvents = isMostRecent ? therapies.stream()
                .mapToInt(th -> th.getPreviousChemoTherapies().size() + th.getPreviousRadioTherapies().size()).sum()
                : therapies.size();
        return totalDosesEvents + totalPreviousTherapyEvents;
    }

    private Map<String, List<List<TumourTherapy>>> groupAllTherapies(Map<String, List<TumourTherapy>> therapies, Datasets datasets) {
        return therapies.entrySet().stream()
                .collect(toMap(Map.Entry::getKey, e -> allTherapiesOverlappedMerged(e.getValue(), datasets)));
    }

    private List<List<TumourTherapy>> allTherapiesOverlappedMerged(List<TumourTherapy> therapies, Datasets datasets) {
        List<List<TumourTherapy>> combined = new ArrayList<>();
        therapies.sort(Comparator.comparing(TumourTherapy::getStartDate, Comparator.nullsFirst(Comparator.naturalOrder())));
        List<TumourTherapy> merged = straightforwardMerge(therapies, true);
        combined.add(merged);

        Object colorByOption = ColorbyCategoriesUtil.getDatasetColorByOption(datasets, PREFERRED_MED);
        therapies.stream()
                .peek(t -> t.setColor(coloringService.getColor(t.getPreviousChemoTherapies().stream().findFirst()
                        .map(ch -> ch.getEvent().getPreferredMedOrEmpty()).orElse(RADIOTHERAPY_LABEL), colorByOption)))
                .map(Arrays::asList)
                .forEach(combined::add); // add all source therapies

        return combined;
    }

    @EqualsAndHashCode(callSuper = true)
    public static class TrellisOptionsWithDrug extends TrellisOptions<PopulationGroupByOptions> {
        @Getter
        @Setter
        private String drug;

        public TrellisOptionsWithDrug(String drug, PopulationGroupByOptions trellisedBy, List<?> trellisOptions) {
            super(trellisedBy, trellisOptions);
            this.drug = drug;
        }
    }

    @Override
    public void generateColors(Datasets datasets) {
        coloringService.generateColors(datasets, getTOCColorBy(datasets, PopulationFilters.empty()));
        FilterResult<Chemotherapy> chemotherapy = chemotherapyService.getFilteredData(datasets,
                ChemotherapyFilters.empty(), PopulationFilters.empty());
        // cache colors for time on compound
        List<TrellisOptions<ChemotherapyGroupByOptions>> chemoColorBy = getTrellisOptions(chemotherapy.getFilteredResult(), PREFERRED_MED);
        // cache colors for chemotherapies
        coloringService.generateColors(datasets, chemoColorBy);
        // cache radiotherapy color (it's single)
        Object datasetColorByOption = ColorbyCategoriesUtil.getDatasetColorByOption(datasets, PREFERRED_MED);
        coloringService.getColor(RADIOTHERAPY_LABEL, datasetColorByOption);
    }
}
