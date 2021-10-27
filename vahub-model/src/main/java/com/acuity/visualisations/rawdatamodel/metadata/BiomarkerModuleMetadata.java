package com.acuity.visualisations.rawdatamodel.metadata;

import com.acuity.visualisations.common.study.metadata.MetadataItem;
import com.acuity.visualisations.rawdatamodel.service.event.BiomarkerService;
import com.acuity.visualisations.rawdatamodel.vo.biomarker.BiomarkerRaw;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Biomarker;
import com.acuity.va.security.acl.domain.Datasets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

@Service
public class BiomarkerModuleMetadata extends AbstractModuleMetadata<BiomarkerRaw, Biomarker> {

    private static final String ENABLE_CBIO = "enableCBioLink";

    @Autowired
    private BiomarkerService biomarkerService;

    @Override
    protected String tab() {
        return "biomarker";
    }

    @Override
    protected MetadataItem buildMetadataItem(MetadataItem metadataItem, Datasets datasets) {
        Collection<Biomarker> data = getData(datasets);
        metadataItem = super.buildMetadataItem(metadataItem, datasets, data);
        final boolean hasCBioProfiles = biomarkerService.getCBioProfiles(data, Collections.emptyMap()).size() > 0;
        metadataItem.addProperty(ENABLE_CBIO, hasCBioProfiles);
        return metadataItem;
    }

    @Override
    protected MetadataItem buildErrorMetadataItem(MetadataItem metadataItem) {
        metadataItem = super.buildErrorMetadataItem(metadataItem);
        metadataItem.addProperty(ENABLE_CBIO, false);
        return metadataItem;
    }

}
