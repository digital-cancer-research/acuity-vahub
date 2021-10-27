package com.acuity.visualisations.rawdatamodel.service.event;

import com.acuity.visualisations.rawdatamodel.filters.PathologyFilters;
import com.acuity.visualisations.rawdatamodel.service.BaseEventService;
import com.acuity.visualisations.rawdatamodel.service.ssv.OncologyPermission;
import com.acuity.visualisations.rawdatamodel.service.ssv.SsvSummaryTableService;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.PathologyGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.PathologyRaw;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Pathology;
import com.acuity.va.security.acl.domain.Datasets;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.acuity.visualisations.rawdatamodel.util.Column.DatasetType;

@Service
@OncologyPermission
public class PathologyService extends BaseEventService<PathologyRaw, Pathology, PathologyGroupByOptions> implements SsvSummaryTableService {
    @Override
    public List<Map<String, String>> getSingleSubjectData(Datasets datasets, String subjectId) {
        return getSingleSubjectData(datasets, subjectId, PathologyFilters.empty());
    }

    @Override
    public Map<String, String> getSingleSubjectColumns(DatasetType datasetType) {
        return ssvCommonService.getColumns(datasetType, Pathology.class, PathologyRaw.class);
    }

    @Override
    public List<Map<String, String>> getSingleSubjectData(Datasets datasets, String subjectId, boolean hasTumourAccess) {
        return hasTumourAccess ? getSingleSubjectData(datasets, subjectId, PathologyFilters.empty())
                : Collections.emptyList();
    }

    @Override
    public String getSsvTableName() {
        return "pathgen";
    }

    @Override
    public String getSsvTableDisplayName() {
        return "PATHGEN";
    }

    @Override
    public String getHeaderName() {
        return "PATIENT HISTORY";
    }

    @Override
    public double getOrder() {
        return 7;
    }
}
