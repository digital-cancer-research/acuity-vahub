package com.acuity.visualisations.rawdatamodel.service.ssv;

import com.acuity.va.security.acl.domain.Datasets;

import java.util.List;
import java.util.Map;

import static com.acuity.visualisations.rawdatamodel.util.Column.DatasetType;

public interface SsvSummaryTableService {

    List<Map<String, String>> getSingleSubjectData(Datasets datasets, String subjectId);

    Map<String, String> getSingleSubjectColumns(DatasetType datasetType);

    String getSsvTableName();

    String getSsvTableDisplayName();

    String getHeaderName();

    default String getSubheaderName() {
        return getSsvTableDisplayName();
    }

    double getOrder();

    default List<Map<String, String>> getSingleSubjectData(Datasets datasets, String subjectId, boolean hasTumourAccess) {
        return getSingleSubjectData(datasets, subjectId);
    }
}
