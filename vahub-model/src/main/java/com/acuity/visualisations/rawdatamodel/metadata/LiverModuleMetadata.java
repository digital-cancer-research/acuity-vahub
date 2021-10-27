package com.acuity.visualisations.rawdatamodel.metadata;

import com.acuity.visualisations.common.study.metadata.MetadataItem;
import com.acuity.visualisations.rawdatamodel.service.event.AeService;
import com.acuity.visualisations.rawdatamodel.service.event.LabService;
import com.acuity.visualisations.rawdatamodel.vo.LiverRaw;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Liver;
import com.acuity.va.security.acl.domain.Datasets;
import com.google.common.collect.ImmutableSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

@Service
public class LiverModuleMetadata extends AbstractModuleMetadata<LiverRaw, Liver> {

    private static final Set<String> JUMP_TO_AE_SOCS = ImmutableSet.of("hepatobiliary disorders", "hepat");

    private static final Set<String> JUMP_TO_ACUITY_LABS = ImmutableSet.of(
            "alanine aminotransferase", "alkaline phosphatase", "aspartate aminotransferase",
            "total bilirubin", "bilirubin");

    @Autowired
    private AeService aeService;

    @Autowired
    private LabService labService;

    @Override
    protected String tab() {
        return "liver";
    }

    @Override
    protected MetadataItem buildMetadataItem(MetadataItem metadataItem, Datasets datasets) {
        Collection<String> labNames;
        if (datasets.isAcuityType()) {
            labNames = labService.getJumpToLabs(datasets, JUMP_TO_ACUITY_LABS);
        } else {
            labNames = Collections.emptyList();
        }
        Set<String> socs = aeService.getJumpToAesSocs(datasets, JUMP_TO_AE_SOCS);

        return super.buildMetadataItem(metadataItem, datasets)
                .add("labNames", labNames)
                .add("socs", socs);
    }

    @Override
    protected MetadataItem buildErrorMetadataItem(MetadataItem metadataItem) {
        return super.buildErrorMetadataItem(metadataItem)
                .add("labNames", Collections.emptyList())
                .add("socs", Collections.emptyList());
    }
}
