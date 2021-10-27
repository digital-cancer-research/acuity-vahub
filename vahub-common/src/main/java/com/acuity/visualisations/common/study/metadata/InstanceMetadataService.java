package com.acuity.visualisations.common.study.metadata;

import com.acuity.va.security.acl.domain.Datasets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Collects all the metadata information from all the implementations of ModuleMetadata in the classpath and generates a json representation to be passed back
 * in a request header
 *
 * @author ksnd199
 */
@Service
@CacheConfig(keyGenerator = "datasetsKeyGenerator", cacheResolver = "refreshableCacheResolver")
@Slf4j
public class InstanceMetadataService {
    @Autowired
    private List<ModuleMetadata> moduleMetadataServices;

    /*
     * Needs to return string to be cached
     */
    @Cacheable(sync = true)
    public String generateMetadata(Datasets datasets) {
        log.debug("Starting gathering metadata for {}", datasets.toString());
        InstanceMetadata instanceMetadata = new InstanceMetadata();

        for (ModuleMetadata moduleMetadata : moduleMetadataServices) {

            log.debug("Gathering metadata {} for {}", moduleMetadata.getClass().getName(), datasets.toString());
            MetadataItem moduleMetadataItem = moduleMetadata.getMetadataItem(datasets);
            instanceMetadata.add(moduleMetadataItem);
            log.debug("Finished gathering metadata {} for {}", moduleMetadata.getClass().getName(), datasets.toString());
        }
        log.debug("Finished gathering metadata for {}", datasets.toString());
        return instanceMetadata.build();
    }

    /*
     * Needs to return string to be cached
     */
    @Cacheable(sync = true)
    public String generateNonMergeableMetadata(Datasets datasets) {
        log.debug("Starting gathering non-mergeable metadata for {}", datasets.toString());
        InstanceMetadata instanceMetadata = new InstanceMetadata();

        for (ModuleMetadata moduleMetadata : moduleMetadataServices) {

            log.debug("Gathering non-mergeable metadata {} for {}", moduleMetadata.getClass().getName(), datasets.toString());
            MetadataItem moduleCountsMetadataItem = moduleMetadata.getNonMergeableMetadataItem(datasets);
            if (moduleCountsMetadataItem != null) {
                instanceMetadata.add(moduleCountsMetadataItem);
            }
            log.debug("Finished gathering non-mergeable metadata {} for {}", moduleMetadata.getClass().getName(), datasets.toString());
        }
        log.debug("Finished gathering non-mergeable metadata for {}", datasets.toString());
        return instanceMetadata.build();
    }
}
