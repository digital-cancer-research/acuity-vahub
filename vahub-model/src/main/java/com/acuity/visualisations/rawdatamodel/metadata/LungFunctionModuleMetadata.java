package com.acuity.visualisations.rawdatamodel.metadata;

import com.acuity.visualisations.common.study.metadata.MetadataItem;
import com.acuity.visualisations.rawdatamodel.axes.ResultType;
import com.acuity.visualisations.rawdatamodel.vo.LungFunctionRaw;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.LungFunction;
import com.acuity.va.security.acl.domain.Datasets;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;

@Service
public class LungFunctionModuleMetadata extends AbstractModuleMetadata<LungFunctionRaw, LungFunction> {

    @Override
    protected String tab() {
        return "lungFunction-java";
    }

    @Override
    protected MetadataItem buildMetadataItem(MetadataItem metadataItem, Datasets datasets) {
        metadataItem = super.buildMetadataItem(metadataItem, datasets);
        metadataItem.add("availableYAxisOptions", Arrays.asList(
                ResultType.ACTUAL_VALUE,
                ResultType.ABSOLUTE_CHANGE_FROM_BASELINE,
                ResultType.PERCENTAGE_CHANGE_FROM_BASELINE
        ));

        return metadataItem;
    }

    @Override
    protected MetadataItem buildErrorMetadataItem(MetadataItem metadataItem) {
        metadataItem = super.buildErrorMetadataItem(metadataItem);
        metadataItem.add("availableYAxisOptions", Collections.emptyList());
        return metadataItem;
    }
}
