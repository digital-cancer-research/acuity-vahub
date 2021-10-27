package com.acuity.visualisations.rawdatamodel.service.event;

import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.filters.SecondTimeOfProgressionFilters;
import com.acuity.visualisations.rawdatamodel.service.BaseEventService;
import com.acuity.visualisations.rawdatamodel.service.ssv.OncologyPermission;
import com.acuity.visualisations.rawdatamodel.service.ssv.SsvSummaryTableService;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.SecondTimeOfProgressionGroupByOptions;
import com.acuity.visualisations.rawdatamodel.util.Column;
import com.acuity.visualisations.rawdatamodel.vo.SecondTimeOfProgressionRaw;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.SecondTimeOfProgression;
import com.acuity.va.security.acl.domain.Datasets;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.acuity.visualisations.rawdatamodel.util.Constants.NOT_IMPLEMENTED;

@Service
@OncologyPermission
public class SecondTimeOfProgressionService extends BaseEventService<SecondTimeOfProgressionRaw, SecondTimeOfProgression, SecondTimeOfProgressionGroupByOptions>
        implements SsvSummaryTableService {

    @Override
    public List<Map<String, String>> getSingleSubjectData(Datasets datasets, String subjectId) {
        return getSingleSubjectData(datasets, subjectId, SecondTimeOfProgressionFilters.empty());
    }

    @Override
    public List<Map<String, String>> getSingleSubjectData(Datasets datasets, String subjectId, Filters<SecondTimeOfProgression> eventFilters) {
        return Collections.singletonList(getSingleSubjectColumns(Column.DatasetType.fromDatasets(datasets))
                .keySet().stream().collect(Collectors.toMap(k -> k, k -> NOT_IMPLEMENTED)));
    }

    @Override
    public Map<String, String> getSingleSubjectColumns(Column.DatasetType datasetType) {
        return ssvCommonService.getColumns(datasetType, SecondTimeOfProgression.class, SecondTimeOfProgressionRaw.class);
    }

    @Override
    public List<Map<String, String>> getSingleSubjectData(Datasets datasets, String subjectId, boolean hasTumourAccess) {
        return hasTumourAccess ? getSingleSubjectData(datasets, subjectId)
                : Collections.emptyList();
    }

    @Override
    public String getSsvTableName() {
        return "secondTimeOfProgression";
    }

    @Override
    public String getSsvTableDisplayName() {
        return "SECOND TIME OF PROGRESSION";
    }

    @Override
    public String getHeaderName() {
        return "FOLLOW UP";
    }

    @Override
    public String getSubheaderName() {
        return "POST IP FOLLOW UP (PFS2 & SURVIVAL)";
    }

    @Override
    public double getOrder() {
        return 20;
    }
}