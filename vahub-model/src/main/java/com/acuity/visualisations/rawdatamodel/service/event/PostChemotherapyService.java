package com.acuity.visualisations.rawdatamodel.service.event;

import com.acuity.visualisations.rawdatamodel.filters.ChemotherapyFilters;
import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.service.ssv.OncologyPermission;
import com.acuity.visualisations.rawdatamodel.service.ssv.SsvSummaryTableService;
import com.acuity.visualisations.rawdatamodel.vo.ChemotherapyRaw;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Chemotherapy;
import com.acuity.va.security.acl.domain.Datasets;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.acuity.visualisations.rawdatamodel.util.Column.DatasetType;

@Service
@OncologyPermission
public class PostChemotherapyService extends ChemotherapyService implements SsvSummaryTableService {

    private static final String POST = "Post";

    @Override
    public List<Map<String, String>> getSingleSubjectData(Datasets datasets, String subjectId) {
        return getSingleSubjectData(datasets, subjectId, ChemotherapyFilters.empty());
    }

    @Override
    public List<Map<String, String>> getSingleSubjectData(Datasets datasets, String subjectId, Filters<Chemotherapy> filters) {
        final FilterResult<Chemotherapy> filteredData = getFilteredData(datasets, filters,
                PopulationFilters.empty(), null, s -> s.getSubjectId().equals(subjectId));
        Collection<Chemotherapy> postChemotherapies = filteredData.stream()
                .filter(ch -> POST.equalsIgnoreCase(ch.getEvent().getTimeStatus()))
                .collect(Collectors.toList());
        return ssvCommonService.getColumnData(DatasetType.fromDatasets(datasets), postChemotherapies);
    }

    @Override
    public Map<String, String> getSingleSubjectColumns(DatasetType datasetType) {
        return ssvCommonService.getColumns(datasetType, Chemotherapy.class, ChemotherapyRaw.class);
    }

    @Override
    public List<Map<String, String>> getSingleSubjectData(Datasets datasets, String subjectId, boolean hasTumourAccess) {
        return hasTumourAccess ? getSingleSubjectData(datasets, subjectId, ChemotherapyFilters.empty())
                : Collections.emptyList();
    }

    @Override
    public String getSsvTableName() {
        return "postChemotherapy";
    }

    @Override
    public String getSsvTableDisplayName() {
        return "POST ANTI-CANCER THERAPY (CAPRX)";
    }

    @Override
    public String getHeaderName() {
        return "FOLLOW UP";
    }

    @Override
    public String getSubheaderName() {
        return "POST STUDY THERAPIES";
    }

    @Override
    public double getOrder() {
        return 19;
    }
}
