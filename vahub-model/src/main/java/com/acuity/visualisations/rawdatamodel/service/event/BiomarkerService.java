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

import com.acuity.visualisations.rawdatamodel.dao.StudyInfoRepository;
import com.acuity.visualisations.rawdatamodel.filters.BiomarkerFilters;
import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.service.BasePlotEventService;
import com.acuity.visualisations.rawdatamodel.service.compatibility.BiomarkerHeatMapColoringService;
import com.acuity.visualisations.rawdatamodel.service.plots.SimpleSelectionSupportService;
import com.acuity.visualisations.rawdatamodel.trellis.TrellisOption;
import com.acuity.visualisations.rawdatamodel.trellis.TrellisOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.BiomarkerGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptionsFiltered;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItem;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.GroupByKey;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.LimitableBySettings;
import com.acuity.visualisations.rawdatamodel.util.AlphanumComparator;
import com.acuity.visualisations.rawdatamodel.util.Attributes;
import com.acuity.visualisations.rawdatamodel.util.Column;
import com.acuity.visualisations.rawdatamodel.util.TrellisUtil;
import com.acuity.visualisations.rawdatamodel.vo.CBioData;
import com.acuity.visualisations.rawdatamodel.vo.CBioProfile;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.StudyInfoAdministrationDetail;
import com.acuity.visualisations.rawdatamodel.vo.biomarker.BiomarkerData;
import com.acuity.visualisations.rawdatamodel.vo.biomarker.BiomarkerParameters;
import com.acuity.visualisations.rawdatamodel.vo.biomarker.BiomarkerRaw;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.OutputHeatMapData;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.OutputHeatMapEntry;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedHeatMap;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Biomarker;
import com.acuity.va.security.acl.domain.Datasets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.acuity.visualisations.rawdatamodel.trellis.grouping.BiomarkerGroupByOptions.GenePercentage.getGenePercentageMap;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Service
public class BiomarkerService extends BasePlotEventService<BiomarkerRaw, Biomarker, BiomarkerGroupByOptions>
        implements SimpleSelectionSupportService<Biomarker, BiomarkerGroupByOptions> {

    @Autowired
    private BiomarkerHeatMapColoringService coloringService;
    @Autowired
    private StudyInfoRepository studyInfoRepository;

    public List<TrellisedHeatMap<Biomarker, BiomarkerGroupByOptions>> getBiomarkerHeatMap(Datasets datasets,
                                                                                          ChartGroupByOptionsFiltered<Biomarker,
                                                                                                  BiomarkerGroupByOptions> settings,
                                                                                          Filters<Biomarker> filters,
                                                                                          PopulationFilters populationFilters) {

        FilterResult<Biomarker> filtered = getFilteredData(datasets, filters, populationFilters);
        //hardcoding chart options
        final ChartGroupByOptions<Biomarker, BiomarkerGroupByOptions> optionsWithContext = getOptionsWithContext(settings.getSettings(),
                getContextSupplier(filtered));

        /*Events should be unique within settings attributes combination, so simply transform to set*/
        final Map<GroupByKey<Biomarker, BiomarkerGroupByOptions>, Set<GroupByKey<Biomarker, BiomarkerGroupByOptions>>> groupedByTrellis =
                filtered.getFilteredResult()
                        .stream().map(e -> Attributes.get(optionsWithContext, e)).collect(
                        Collectors.groupingBy(LimitableBySettings::limitedByTrellisOptions, Collectors.toSet())
                );

        //all distinct X and Y axis values over all the charts
        final List<String> yCategories = groupedByTrellis.values().stream().flatMap(Collection::stream)
                .map(i -> i.getValue(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS)).distinct().sorted()
                .map(Object::toString).collect(toList());
        final List<String> xCategories = groupedByTrellis.values().stream().flatMap(Collection::stream)
                .map(i -> i.getValue(ChartGroupByOptions.ChartGroupBySetting.X_AXIS)).distinct()
                .map(Object::toString).sorted(new AlphanumComparator<>()).collect(toList());


        return groupedByTrellis.entrySet().stream().map(trellisSet -> {
            final Set<GroupByKey<Biomarker, BiomarkerGroupByOptions>> items = trellisSet.getValue();


            final List<TrellisOption<Biomarker, BiomarkerGroupByOptions>> trellisOptions = trellisSet.getKey().getTrellisByValues().entrySet().stream()
                    .map(option -> TrellisOption.of(option.getKey(), option.getValue())).collect(toList());
            final List<OutputHeatMapEntry> entries = items.stream().map(i -> {
                int x = xCategories.indexOf(i.getValue(ChartGroupByOptions.ChartGroupBySetting.X_AXIS).toString());
                int y = yCategories.indexOf(i.getValue(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS).toString());
                BiomarkerData value = (BiomarkerData) i.getValue(ChartGroupByOptions.ChartGroupBySetting.VALUE);
                String priorityMutation = value.getPriorityMutation();
                return new OutputHeatMapEntry(x, y, priorityMutation, value, coloringService.getColor(priorityMutation));
            }).sorted(Comparator.comparing(OutputHeatMapEntry::getX).thenComparing(OutputHeatMapEntry::getY)).collect(toList());
            return new TrellisedHeatMap<>(trellisOptions, new OutputHeatMapData(xCategories, yCategories, entries));
        }).collect(toList());
    }

    public List<TrellisOptions<BiomarkerGroupByOptions>> getHeatmapColorByOptions(Datasets datasets,
                                                                                  Filters<Biomarker> filters,
                                                                                  PopulationFilters populationFilters) {
        FilterResult<Biomarker> filtered = getFilteredData(datasets, filters, populationFilters);
        return TrellisUtil.getTrellisOptions(filtered.getFilteredResult(),
                BiomarkerGroupByOptions.ALTERATION_TYPE.<Biomarker, BiomarkerGroupByOptions>getGroupByOptionAndParams()
                        .supplyContext(getColorByContextSupplier(filtered)));
    }

    private Supplier<Map<BiomarkerGroupByOptions, Object>> getContextSupplier(FilterResult<Biomarker> filtered) {
        return () -> {
            final Map<BiomarkerGroupByOptions, Object> context = new EnumMap<>(BiomarkerGroupByOptions.class);
            context.put(BiomarkerGroupByOptions.GENE_PERCENTAGE, getGenePercentageMap(filtered));
            context.put(BiomarkerGroupByOptions.BIOMARKER_DATA, getBiomarkerDataByGeneBySubject(filtered));
            return context;
        };
    }

    private Supplier<Map<BiomarkerGroupByOptions, Object>> getColorByContextSupplier(FilterResult<Biomarker> filtered) {
        return () -> {
            final Map<BiomarkerGroupByOptions, Object> context = new EnumMap<>(BiomarkerGroupByOptions.class);
            context.put(BiomarkerGroupByOptions.ALTERATION_TYPE, getBiomarkerDataByGeneBySubject(filtered));
            return context;
        };
    }

    public SelectionDetail getSelectionDetails(Datasets datasets, Filters<Biomarker> filters, PopulationFilters populationFilters,
                                               ChartSelection<Biomarker, BiomarkerGroupByOptions,
                                                       ChartSelectionItem<Biomarker, BiomarkerGroupByOptions>> selection) {
        FilterResult<Biomarker> filtered = getFilteredData(datasets, filters, populationFilters);
        ChartGroupByOptions optionsWithContext = getOptionsWithContext(selection.getSettings(), getContextSupplier(filtered));
        return getSelectionDetails(filtered, ChartSelection.of(optionsWithContext, selection.getSelectionItems()));
    }

    public CBioData getCBioData(Datasets datasets, Set<String> ids, Filters<Biomarker> filters, PopulationFilters populationFilters) {
        FilterResult<Biomarker> filtered = getFilteredData(datasets, filters, populationFilters);
        List<Biomarker> events = new ArrayList<>(filtered.getFilteredEvents());
        Map<String, Integer> genesPercentage = BiomarkerGroupByOptions.GenePercentage.getGenePercentageMap(filtered);
        if (!ids.isEmpty()) {
            // if events are selected, then leave selected only
            events = events.stream()
                    .filter(e -> ids.contains(e.getId()))
                    .collect(toList());
        }
        events.sort(Comparator.comparing(e -> -genesPercentage.get(((Biomarker) e).getEvent().getGene()))
                .thenComparing(e -> ((Biomarker) e).getEvent().getGene()));
        List<StudyInfoAdministrationDetail> studyCodesList = studyInfoRepository.getStudyInfoByDatasetIds(datasets.getIds());
        Map<String, String> studyCodesToCBioPortalCodes = studyCodesList.stream()
                .collect(toMap(StudyInfoAdministrationDetail::getStudyCode, StudyInfoAdministrationDetail::getCBioStudyCode));

        Map<String, List<String>> profiles = getCBioProfiles(events, studyCodesToCBioPortalCodes);
        List<Map<String, String>> data = cBioCommonService.getColumnData(Column.DatasetType.fromDatasets(datasets), events);
        return new CBioData(studyCodesToCBioPortalCodes, profiles, data);
    }

    public Map<String, List<String>> getCBioProfiles(Collection<Biomarker> events,
                                                     Map<String, String> studyCodesToCBioPortalCodes) {
        return events.stream()
                .collect(toMap(Biomarker::getStudyId, p -> p.getEvent().getProfilesMask(), (p, q) -> p)).entrySet().stream()
                .map(e -> getProfilePairs(
                        studyCodesToCBioPortalCodes.getOrDefault(e.getKey(), e.getKey()),
                        e.getValue()).entrySet())
                .flatMap(Collection::stream)
                .collect(toMap(Map.Entry::getKey, e -> {
                    List<String> profileIdes = new LinkedList<>();
                    profileIdes.add(e.getValue());
                    return profileIdes;
                }, (p, q) -> {
                    p.addAll(q);
                    return p;
                }));
    }

    private Map<String, Map<String, List<BiomarkerParameters>>> getBiomarkerDataByGeneBySubject(FilterResult<Biomarker> filtered) {
        return filtered.stream().collect(groupingBy(Biomarker::getSubjectCode,
                groupingBy((Biomarker b) -> b.getEvent().getGene(),
                        mapping((Biomarker b) -> BiomarkerParameters.builder().mutation(b.getEvent().getMutation())
                                .somaticStatus(b.getEvent().getSomaticStatus())
                                .aminoAcidChange(b.getEvent().getAminoAcidChange())
                                .copyNumberAlterationCopyNumber(b.getEvent().getCopyNumberAlterationCopyNumber())
                                .alleleFrequency(b.getEvent().getMutantAlleleFrequency())
                                .build(), toList()))));
    }

    private Map<String, String> getProfilePairs(String studyId, Integer bitmask) {
        return CBioProfile.getProfilesFromBitmask(bitmask).stream()
                .collect(toMap(CBioProfile::getProfileGroupName, p -> p.getStudyProfileId(studyId), (p, q) -> p));
    }

    public SelectionDetail getSelectionBySubjectIds(Datasets datasets, Set<String> subjectIds) {
        FilterResult<Biomarker> filtered = getFilteredData(datasets, BiomarkerFilters.empty(), PopulationFilters.empty());
        return super.getSelectionBySubjectIds(filtered, subjectIds);
    }
}
