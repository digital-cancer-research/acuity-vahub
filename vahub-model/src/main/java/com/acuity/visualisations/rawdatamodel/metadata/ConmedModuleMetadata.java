package com.acuity.visualisations.rawdatamodel.metadata;

import com.acuity.visualisations.common.study.metadata.MetadataItem;
import com.acuity.visualisations.rawdatamodel.axes.CountType;
import com.acuity.visualisations.rawdatamodel.vo.ConmedRaw;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Conmed;
import com.acuity.va.security.acl.domain.Datasets;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;

@Service
public class ConmedModuleMetadata extends AbstractModuleMetadata<ConmedRaw, Conmed> {
    @Override
    protected String tab() {
        return "conmeds";
    }

    @Override
    protected MetadataItem buildMetadataItem(MetadataItem metadataItem, Datasets datasets) {
        metadataItem = super.buildMetadataItem(metadataItem, datasets);
        metadataItem.add("availableYAxisOptions", Arrays.asList(
                CountType.COUNT_OF_SUBJECTS,
                CountType.PERCENTAGE_OF_ALL_SUBJECTS,
                CountType.PERCENTAGE_OF_SUBJECTS_WITHIN_PLOT));
        return metadataItem;
    }

    @Override
    protected MetadataItem buildErrorMetadataItem(MetadataItem metadataItem) {
        metadataItem = super.buildErrorMetadataItem(metadataItem);
        metadataItem.add("availableYAxisOptions", Collections.emptyList());
        return metadataItem;
    }
}
