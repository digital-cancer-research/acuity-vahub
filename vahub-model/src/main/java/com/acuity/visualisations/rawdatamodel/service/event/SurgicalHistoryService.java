package com.acuity.visualisations.rawdatamodel.service.event;

import com.acuity.visualisations.rawdatamodel.filters.SurgicalHistoryFilters;
import com.acuity.visualisations.rawdatamodel.service.BaseEventService;
import com.acuity.visualisations.rawdatamodel.service.ssv.SsvSummaryTableService;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.SurgicalHistoryGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.SurgicalHistoryRaw;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.SurgicalHistory;
import com.acuity.va.security.acl.domain.Datasets;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.acuity.visualisations.rawdatamodel.util.Column.DatasetType;

@Service
public class SurgicalHistoryService extends BaseEventService<SurgicalHistoryRaw, SurgicalHistory, SurgicalHistoryGroupByOptions>
        implements SsvSummaryTableService {

    @Override
    public List<Map<String, String>> getSingleSubjectData(Datasets datasets, String subjectId) {
        return getSingleSubjectData(datasets, subjectId, SurgicalHistoryFilters.empty());
    }

    @Override
    public Map<String, String> getSingleSubjectColumns(DatasetType datasetType) {
        return ssvCommonService.getColumns(datasetType, SurgicalHistory.class, SurgicalHistoryRaw.class);
    }

    @Override
    public String getSsvTableName() {
        return "surgicalHistory";
    }

    @Override
    public String getSsvTableDisplayName() {
        return "SURGICAL HISTORY";
    }

    @Override
    public String getHeaderName() {
        return "PATIENT HISTORY";
    }

    @Override
    public double getOrder() {
        return 4;
    }
}
