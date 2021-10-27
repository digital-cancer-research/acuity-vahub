package com.acuity.visualisations.rawdatamodel.service.event;

import com.acuity.visualisations.rawdatamodel.dataproviders.DrugDoseDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.filters.DrugDoseFilters;
import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.service.BaseEventService;
import com.acuity.visualisations.rawdatamodel.service.ssv.SsvSummaryTableService;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.DrugDoseGroupByOptions;
import com.acuity.visualisations.rawdatamodel.util.Column.DatasetType;
import com.acuity.visualisations.rawdatamodel.vo.DrugDoseRaw;
import com.acuity.visualisations.rawdatamodel.vo.FilterQuery;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.DrugDose;
import com.acuity.va.security.acl.domain.Datasets;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@Service
public class DrugDoseService extends BaseEventService<DrugDoseRaw, DrugDose, DrugDoseGroupByOptions> implements SsvSummaryTableService {



    public FilterResult<DrugDose> getFilteredDataForTumourColumnRangeService(Datasets datasets, Filters<DrugDose> filters,
                                                                             PopulationFilters populationFilters,
                                                                             Predicate<DrugDose> eventPredicate) {

        FilterQuery<DrugDose> filterQuery = getFilterQueryForTumourColumnRangeService(datasets, filters, populationFilters, eventPredicate);
        return eventFilterService.query(filterQuery);
    }

    protected FilterQuery<DrugDose> getFilterQueryForTumourColumnRangeService(Datasets datasets, Filters<DrugDose> filters, PopulationFilters populationFilters,
                                                                              Predicate<DrugDose> eventPredicate) {

        Stream<DrugDose> eventStream = ((DrugDoseDatasetsDataProvider) getEventDataProvider(datasets, filters))
                .loadDosesForTumourColumnRangeService(datasets).stream();

        //if eventPredicate is provided, filtering on this predicate
        eventStream = eventPredicate == null ? eventStream : eventStream.filter(eventPredicate);
        Collection<DrugDose> events = eventStream.collect(toList());
        Collection<Subject> subjects = getPopulationDatasetsDataProvider().loadData(datasets);
        return new FilterQuery<>(events, filters, subjects, populationFilters);
    }

    @Override
    public List<Map<String, String>> getSingleSubjectData(Datasets datasets, String subjectId) {
        return getSingleSubjectData(datasets, subjectId, DrugDoseFilters.empty());
    }

    @Override
    public Map<String, String> getSingleSubjectColumns(DatasetType datasetType) {
        return ssvCommonService.getColumns(datasetType, DrugDose.class, DrugDoseRaw.class);
    }

    @Override
    public String getSsvTableName() {
        return "drugDose";
    }

    @Override
    public String getSsvTableDisplayName() {
        return "STUDY DRUG ADMINISTRATION";
    }

    @Override
    public String getHeaderName() {
        return "STUDY DRUG";
    }

    @Override
    public double getOrder() {
        return 12;
    }
}
