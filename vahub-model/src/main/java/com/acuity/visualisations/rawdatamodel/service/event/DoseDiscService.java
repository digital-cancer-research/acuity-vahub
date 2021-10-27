package com.acuity.visualisations.rawdatamodel.service.event;

import com.acuity.visualisations.rawdatamodel.filters.DoseDiscFilters;
import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.service.BaseEventService;
import com.acuity.visualisations.rawdatamodel.service.ssv.SsvSummaryTableService;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.DoseDiscGroupByOptions;
import com.acuity.visualisations.rawdatamodel.util.Column.DatasetType;
import com.acuity.visualisations.rawdatamodel.vo.DoseDiscRaw;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.DoseDisc;
import com.acuity.va.security.acl.domain.Datasets;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DoseDiscService extends BaseEventService<DoseDiscRaw, DoseDisc, DoseDiscGroupByOptions> implements SsvSummaryTableService {

    @Override
    public List<Map<String, String>> getSingleSubjectData(Datasets datasets, String subjectId) {
        return getSingleSubjectData(datasets, subjectId, DoseDiscFilters.empty());
    }

    @Override
    public Map<String, String> getSingleSubjectColumns(DatasetType datasetType) {
        return ssvCommonService.getColumns(datasetType, DoseDisc.class, DoseDiscRaw.class);
    }

    @Override
    public List<Map<String, String>> getSingleSubjectData(Datasets datasets, String subjectId, Filters<DoseDisc> filters) {
        final FilterResult<DoseDisc> filteredData = getFilteredData(datasets, filters,
                PopulationFilters.empty(), null, s -> s.getSubjectId().equals(subjectId));
        Collection<DoseDisc> sortedEvents = filteredData.stream()
                .sorted(Comparator.comparing(d -> d.getEvent().getDiscDate(), Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
        return ssvCommonService.getColumnData(DatasetType.fromDatasets(datasets), sortedEvents);
    }

    @Override
    public String getSsvTableName() {
        return "doseDisc";
    }

    @Override
    public String getSsvTableDisplayName() {
        return "DISCONTINUATION OF DRUG";
    }

    @Override
    public String getHeaderName() {
        return "STUDY DRUG";
    }

    @Override
    public double getOrder() {
        return 13;
    }
}
