package com.acuity.visualisations.rawdatamodel.service.event;

import com.acuity.visualisations.rawdatamodel.filters.DiseaseExtentFilters;
import com.acuity.visualisations.rawdatamodel.service.BaseEventService;
import com.acuity.visualisations.rawdatamodel.service.ssv.OncologyPermission;
import com.acuity.visualisations.rawdatamodel.service.ssv.SsvSummaryTableService;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.DiseaseExtentGroupByOptions;
import com.acuity.visualisations.rawdatamodel.util.Column.DatasetType;
import com.acuity.visualisations.rawdatamodel.vo.DiseaseExtentRaw;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.DiseaseExtent;
import com.acuity.va.security.acl.domain.Datasets;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@OncologyPermission
public class DiseaseExtentService extends BaseEventService<DiseaseExtentRaw, DiseaseExtent, DiseaseExtentGroupByOptions> implements SsvSummaryTableService {
    @Override
    public List<Map<String, String>> getSingleSubjectData(Datasets datasets, String subjectId) {
        return getSingleSubjectData(datasets, subjectId, DiseaseExtentFilters.empty());
    }

    @Override
    public Map<String, String> getSingleSubjectColumns(DatasetType datasetType) {
        return ssvCommonService.getColumns(datasetType, DiseaseExtent.class, DiseaseExtentRaw.class);
    }

    @Override
    public List<Map<String, String>> getSingleSubjectData(Datasets datasets, String subjectId, boolean hasTumourAccess) {
        return hasTumourAccess ? getSingleSubjectData(datasets, subjectId, DiseaseExtentFilters.empty())
                : Collections.emptyList();
    }

    @Override
    public String getSsvTableName() {
        return "disExt";
    }

    @Override
    public String getSsvTableDisplayName() {
        return "EXTENT OF DISEASE AT STUDY ENTRY (DISEXT)";
    }

    @Override
    public String getHeaderName() {
        return "PATIENT HISTORY";
    }

    @Override
    public double getOrder() {
        return 8;
    }
}
