package com.acuity.visualisations.rawdatamodel.service.event;

import com.acuity.visualisations.rawdatamodel.filters.TargetLesionFilters;
import com.acuity.visualisations.rawdatamodel.service.BaseEventService;
import com.acuity.visualisations.rawdatamodel.service.ssv.OncologyPermission;
import com.acuity.visualisations.rawdatamodel.service.ssv.SsvSummaryTableService;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.TargetLesionGroupByOptions;
import com.acuity.visualisations.rawdatamodel.util.Column.DatasetType;
import com.acuity.visualisations.rawdatamodel.vo.TargetLesionRaw;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.TargetLesion;
import com.acuity.va.security.acl.domain.Datasets;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@OncologyPermission
public class TargetLesionService
        extends BaseEventService<TargetLesionRaw, TargetLesion, TargetLesionGroupByOptions>
        implements SsvSummaryTableService {
    @Override
    public List<Map<String, String>> getSingleSubjectData(Datasets datasets, String subjectId) {
        return getSingleSubjectData(datasets, subjectId, TargetLesionFilters.empty());
    }

    @Override
    public List<Map<String, String>> getSingleSubjectData(Datasets datasets, String subjectId, boolean hasTumourAccess) {
        return hasTumourAccess ? getSingleSubjectData(datasets, subjectId, TargetLesionFilters.empty())
                : Collections.emptyList();
    }

    @Override
    public Map<String, String> getSingleSubjectColumns(DatasetType datasetType) {
        return ssvCommonService.getColumns(datasetType, TargetLesion.class, TargetLesionRaw.class);
    }

    @Override
    public String getSsvTableName() {
        return "targetlesion";
    }

    @Override
    public String getSsvTableDisplayName() {
        return "TARGET LESION";
    }

    @Override
    public String getSubheaderName() {
        return "TARGET LESION";
    }

    @Override
    public String getHeaderName() {
        return "RECIST RESPONSE";
    }

    @Override
    public double getOrder() {
        return 16;
    }
}
