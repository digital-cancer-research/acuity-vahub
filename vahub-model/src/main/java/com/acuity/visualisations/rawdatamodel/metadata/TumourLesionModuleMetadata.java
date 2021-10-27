package com.acuity.visualisations.rawdatamodel.metadata;

import com.acuity.visualisations.common.study.metadata.MetadataItem;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ATLGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.AssessedTargetLesionRaw;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.AssessedTargetLesion;
import com.acuity.va.security.acl.domain.Datasets;
import org.springframework.stereotype.Service;

import java.util.ArrayList;


import static com.acuity.visualisations.rawdatamodel.util.Constants.AVAILABLE_YAXIS_OPTIONS;
import static com.google.common.collect.Lists.newArrayList;

@Service
public class TumourLesionModuleMetadata extends AbstractModuleMetadata<AssessedTargetLesionRaw, AssessedTargetLesion> {
    @Override
    protected String tab() {
        return "tumour-lesion";
    }

    @Override
    protected MetadataItem buildMetadataItem(MetadataItem metadataItem, Datasets datasets) {
        metadataItem = super.buildMetadataItem(metadataItem, datasets);
        // linechart-by-lesion Y axis
        metadataItem.add(AVAILABLE_YAXIS_OPTIONS, newArrayList(ATLGroupByOptions.LESION_PERCENTAGE_CHANGE.toString()));
        return metadataItem;
    }

    @Override
    protected MetadataItem buildErrorMetadataItem(MetadataItem metadataItem) {
        metadataItem = super.buildErrorMetadataItem(metadataItem);
        metadataItem.add(AVAILABLE_YAXIS_OPTIONS, new ArrayList<>());
        return metadataItem;
    }
}
