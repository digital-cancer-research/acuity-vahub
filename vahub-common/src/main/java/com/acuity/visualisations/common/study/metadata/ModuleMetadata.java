package com.acuity.visualisations.common.study.metadata;

import com.acuity.va.security.acl.domain.Datasets;

/**
 * Interface for modules to implement to gather metadata about their module
 *
 * @author ksnd199
 */
public interface ModuleMetadata {

    MetadataItem getMetadataItem(Datasets datasets);
    
    MetadataItem getNonMergeableMetadataItem(Datasets datasets);
}
