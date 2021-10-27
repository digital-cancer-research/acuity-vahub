package com.acuity.visualisations.rawdatamodel.metadata;

import com.acuity.visualisations.common.study.metadata.MetadataItem;
import com.acuity.visualisations.rawdatamodel.service.event.PkResultWithResponseService;
import com.acuity.va.security.acl.domain.Datasets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class RecistPkResultWithResponseModuleMetadata extends AbstractModuleMetadata {

    private static final String RECIST_PK_KEY = "recist-pk";

    @Autowired
    private PkResultWithResponseService pkResultWithResponseService;

    protected String tab() {
        return RECIST_PK_KEY;
    }

    @Override
    protected MetadataItem buildAllMetadataItems(MetadataItem metadataItem, Datasets datasets) {

        final Map<String, String> doDColumns = pkResultWithResponseService.getRecistDoDColumns(datasets);
        metadataItem.add("detailsOnDemandColumns", doDColumns.keySet());
        metadataItem.add("detailsOnDemandTitledColumns", doDColumns);
        return metadataItem;
    }

}
