package com.acuity.visualisations.rawdatamodel.service.event;

import com.acuity.visualisations.rawdatamodel.dataproviders.PopulationDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.common.SubjectAwareDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.filters.MedicalHistoryFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.service.dod.AmlCommonService;
import com.acuity.visualisations.rawdatamodel.service.dod.CBioCommonService;
import com.acuity.visualisations.rawdatamodel.service.dod.DoDCommonService;
import com.acuity.visualisations.rawdatamodel.service.dod.SsvCommonService;
import com.acuity.visualisations.rawdatamodel.service.filters.AbstractEventFilterService;
import com.acuity.visualisations.rawdatamodel.service.filters.PopulationRawDataFilterService;
import com.acuity.visualisations.rawdatamodel.service.ssv.SsvSummaryTableService;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.MedicalHistoryRaw;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.MedicalHistory;
import com.acuity.va.security.acl.domain.Datasets;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.acuity.visualisations.rawdatamodel.util.Column.DatasetType;

@Service
public class PastMedicalHistoryService extends MedicalHistoryService implements SsvSummaryTableService {

    public PastMedicalHistoryService(DoDCommonService doDCommonService,
                                     SsvCommonService ssvCommonService,
                                     AmlCommonService amlCommonService,
                                     CBioCommonService cBioCommonService,
                                     List<SubjectAwareDatasetsDataProvider<MedicalHistoryRaw, MedicalHistory>> eventDataProviders,
                                     PopulationDatasetsDataProvider populationDatasetsDataProvider,
                                     AbstractEventFilterService<MedicalHistory, Filters<MedicalHistory>> eventFilterService,
                                     PopulationRawDataFilterService populationFilterService) {
        super(
                doDCommonService,
                ssvCommonService,
                amlCommonService,
                cBioCommonService,
                eventDataProviders,
                populationDatasetsDataProvider,
                eventFilterService,
                populationFilterService);
    }

    @Override
    public List<Map<String, String>> getSingleSubjectData(Datasets datasets, String subjectId) {
        return getSingleSubjectData(datasets, subjectId, MedicalHistoryFilters.empty());
    }

    @Override
    public List<Map<String, String>> getSingleSubjectData(Datasets datasets, String subjectId, Filters<MedicalHistory> filters) {
        final FilterResult<MedicalHistory> filteredData = getFilteredData(datasets, filters,
                PopulationFilters.empty(), null, s -> s.getSubjectId().equals(subjectId));
        Collection<MedicalHistory> pastMedicalHistories = filteredData.stream()
                .filter(mh -> {
                    String conditionalStatus = StringUtils.lowerCase(mh.getEvent().getConditionStatus());
                    return PAST.contains(conditionalStatus)
                            || (!CURRENT.contains(conditionalStatus) && mh.endsBeforeFirstTreatmentDate());
                })
                .collect(Collectors.toList());
        return ssvCommonService.getColumnData(DatasetType.fromDatasets(datasets), pastMedicalHistories);
    }

    @Override
    public Map<String, String> getSingleSubjectColumns(DatasetType datasetType) {
        return ssvCommonService.getColumns(datasetType, MedicalHistory.class, MedicalHistoryRaw.class);
    }

    @Override
    public String getSsvTableName() {
        return "pastMedicalHistory";
    }

    @Override
    public String getSsvTableDisplayName() {
        return "PAST MEDICAL HISTORY";
    }

    @Override
    public String getHeaderName() {
        return "PATIENT HISTORY";
    }

    @Override
    public double getOrder() {
        return 3;
    }
}
