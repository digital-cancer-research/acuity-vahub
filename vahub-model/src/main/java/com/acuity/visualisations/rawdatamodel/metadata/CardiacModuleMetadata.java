package com.acuity.visualisations.rawdatamodel.metadata;

import com.acuity.visualisations.common.study.metadata.MetadataItem;
import com.acuity.visualisations.rawdatamodel.service.event.AeService;
import com.acuity.visualisations.rawdatamodel.vo.CardiacRaw;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Cardiac;
import com.acuity.va.security.acl.domain.Datasets;
import com.google.common.collect.ImmutableSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import static com.acuity.visualisations.rawdatamodel.axes.ResultType.ABSOLUTE_CHANGE_FROM_BASELINE;
import static com.acuity.visualisations.rawdatamodel.axes.ResultType.ACTUAL_VALUE;
import static com.acuity.visualisations.rawdatamodel.axes.ResultType.PERCENTAGE_CHANGE_FROM_BASELINE;

@Service
public class CardiacModuleMetadata extends AbstractModuleMetadata<CardiacRaw, Cardiac> {

    @Autowired
    private AeService aeService;

    private static final Set<String> ACUITY_JUMP_TO_AE_SOCS = ImmutableSet.of("cardiac disorders", "card");

    @Override
    protected MetadataItem buildMetadataItem(MetadataItem metadataItem, Datasets datasets) {
        Set<String> socs = aeService.getJumpToAesSocs(datasets, ACUITY_JUMP_TO_AE_SOCS);

        return super.buildMetadataItem(metadataItem, datasets)
                .add("availableYAxisOptions", Arrays.asList(
                        ACTUAL_VALUE,
                        ABSOLUTE_CHANGE_FROM_BASELINE,
                        PERCENTAGE_CHANGE_FROM_BASELINE
                ))
                .add("socs", socs);
    }

    @Override
    protected MetadataItem buildErrorMetadataItem(MetadataItem metadataItem) {
        return super.buildErrorMetadataItem(metadataItem)
                .add("availableYAxisOptions", Collections.emptyList())
                .add("socs", Collections.emptyList());
    }

    @Override
    protected String tab() {
        return "cardiac";
    }
}
