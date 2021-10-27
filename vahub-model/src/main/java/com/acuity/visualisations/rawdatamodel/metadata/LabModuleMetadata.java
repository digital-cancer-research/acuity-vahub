package com.acuity.visualisations.rawdatamodel.metadata;

import com.acuity.visualisations.common.study.metadata.MetadataItem;
import com.acuity.visualisations.rawdatamodel.axes.ResultType;
import com.acuity.visualisations.rawdatamodel.service.dod.DoDCommonService;
import com.acuity.visualisations.rawdatamodel.vo.LabRaw;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Lab;
import com.acuity.va.security.acl.domain.Datasets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.stream.Collectors;


import static com.acuity.visualisations.rawdatamodel.util.Constants.AVAILABLE_YAXIS_OPTIONS;
import static com.google.common.collect.Lists.newArrayList;

@Service
public class LabModuleMetadata extends AbstractModuleMetadata<LabRaw, Lab> {

    @Autowired
    private DoDCommonService doDCommonService;

    @Override
    public MetadataItem getNonMergeableMetadataItem(Datasets datasets) {
        return getMetadataItem(datasets);
    }

    @Override
    protected String tab() {
        return "labs";
    }

    @Override
    protected MetadataItem buildMetadataItem(MetadataItem metadataItem, Datasets datasets) {
        return super.buildMetadataItem(metadataItem, datasets)
                .add(AVAILABLE_YAXIS_OPTIONS, Arrays.stream(ResultType.values()).map(Enum::toString).collect(Collectors.toList()));
    }

    @Override
    protected MetadataItem buildErrorMetadataItem(MetadataItem metadataItem) {
        return super.buildErrorMetadataItem(metadataItem)
                .add(AVAILABLE_YAXIS_OPTIONS, newArrayList());
    }
}
