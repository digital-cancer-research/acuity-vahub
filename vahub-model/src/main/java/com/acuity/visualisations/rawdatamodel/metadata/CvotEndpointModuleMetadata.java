package com.acuity.visualisations.rawdatamodel.metadata;

import com.acuity.visualisations.common.study.metadata.MetadataItem;
import com.acuity.visualisations.rawdatamodel.axes.BinCountType;
import com.acuity.visualisations.rawdatamodel.axes.CountType;
import com.acuity.visualisations.rawdatamodel.vo.CvotEndpointRaw;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.CvotEndpoint;
import com.acuity.va.security.acl.domain.Datasets;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;


import static com.acuity.visualisations.rawdatamodel.util.Constants.AVAILABLE_BAR_LINE_YAXIS_OPTIONS;
import static com.acuity.visualisations.rawdatamodel.util.Constants.AVAILABLE_YAXIS_OPTIONS;

/**
 * Created by knml167 on 5/22/2017.
 */
@Service
public class CvotEndpointModuleMetadata extends AbstractModuleMetadata<CvotEndpointRaw, CvotEndpoint> {

    @Override
    public MetadataItem getNonMergeableMetadataItem(Datasets datasets) {
        return getMetadataItem(datasets);
    }

    @Override
    protected String tab() {
        return "cvotEndpoints";
    }


    @Override
    protected MetadataItem buildMetadataItem(MetadataItem metadataItem, Datasets datasets) {
        metadataItem = super.buildMetadataItem(metadataItem, datasets);
        metadataItem.add(AVAILABLE_YAXIS_OPTIONS,
                Arrays.stream(CountType.values()).map(Enum::toString).collect(Collectors.toList()));
        metadataItem.add(AVAILABLE_BAR_LINE_YAXIS_OPTIONS, Collections.singletonList(BinCountType.COUNT_START_DATES_ONLY));
        return metadataItem;
    }

    @Override
    protected MetadataItem buildErrorMetadataItem(MetadataItem metadataItem) {
        metadataItem = super.buildErrorMetadataItem(metadataItem);
        metadataItem.add(AVAILABLE_YAXIS_OPTIONS, new ArrayList<>());
        metadataItem.add(AVAILABLE_BAR_LINE_YAXIS_OPTIONS, new ArrayList<>());
        return metadataItem;
    }
}
