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
import com.acuity.visualisations.rawdatamodel.util.Column.DatasetType;
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

@Service
public class CurrentMedicalHistoryService extends MedicalHistoryService implements SsvSummaryTableService {

    public CurrentMedicalHistoryService(DoDCommonService doDCommonService,
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
        Collection<MedicalHistory> currentMedicalHistories = filteredData.stream()
                .filter(mh -> {
                    final String conditionalStatus = StringUtils.lowerCase(mh.getEvent().getConditionStatus());
                    return CURRENT.contains(conditionalStatus)
                            || (!PAST.contains(conditionalStatus) && !mh.endsBeforeFirstTreatmentDate());
                })
                .collect(Collectors.toList());
        return ssvCommonService.getColumnData(DatasetType.fromDatasets(datasets), currentMedicalHistories);
    }

    @Override
    public Map<String, String> getSingleSubjectColumns(DatasetType datasetType) {
        return ssvCommonService.getColumns(datasetType, MedicalHistory.class, MedicalHistoryRaw.class);
    }

    @Override
    public String getSsvTableName() {
        return "currentMedicalHistory";
    }

    @Override
    public String getSsvTableDisplayName() {
        return "CONCURRENT CONDITIONS AT STUDY ENTRY";
    }

    @Override
    public String getHeaderName() {
        return "PATIENT HISTORY";
    }

    @Override
    public double getOrder() {
        return 5;
    }
}
