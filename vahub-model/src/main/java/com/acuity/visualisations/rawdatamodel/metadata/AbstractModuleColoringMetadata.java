package com.acuity.visualisations.rawdatamodel.metadata;


import com.acuity.visualisations.common.study.metadata.MetadataItem;
import com.acuity.visualisations.rawdatamodel.service.ssv.ColorInitializer;
import com.acuity.va.security.acl.domain.Datasets;

public abstract class AbstractModuleColoringMetadata<R, T> extends AbstractModuleMetadata {

    @Override
    public MetadataItem getMetadataItem(Datasets datasets) {
        getColorInitializer().generateColors(datasets);
        return super.getMetadataItem(datasets);
    }

    abstract ColorInitializer getColorInitializer();
}
