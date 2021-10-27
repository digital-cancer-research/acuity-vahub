/*
 * Copyright 2021 The University of Manchester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.acuity.visualisations.common.study.metadata;

import com.acuity.visualisations.common.cache.DatasetsKey;
import com.acuity.visualisations.common.cache.DatasetsKeyGenerator;
import com.acuity.visualisations.common.cache.RefreshableCacheResolver;
import com.acuity.va.security.acl.domain.Datasets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Combines 2 metadatas from multi metadata objects
 *
 * @author ksnd199
 */
@Service
@Slf4j
public class MultiMetadataService {
    @Autowired
    private InstanceMetadataService instanceMetadataService;
    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private RefreshableCacheResolver refreshableCacheResolver;

    private Cache getCacheForDatasets(Datasets datasets) {
        Collection<? extends Cache> resolveCaches = refreshableCacheResolver.
                resolveCaches(datasets, InstanceMetadataService.class.getSimpleName(), "generateMetadata");
        return resolveCaches.iterator().next();
    }

    /*
     * Needs to return string to be cached
     */
    public String generateMetadata(Datasets datasets) {
        log.debug("Starting gathering metadata for {}", datasets.toString());

        String generatedMetadata = null;

        if (datasets.getDatasets().size() == 1 || datasets.getDatasets().isEmpty()) { // if empty or 1, treat as normal
            generatedMetadata = instanceMetadataService.generateMetadata(datasets);
        } else {
            List<Datasets> datasetsAsSingleDatasets = datasets.getDatasets().stream().map(ds -> new Datasets(ds)).collect(toList());

            Cache generateMetadataCacheForDatasets = getCacheForDatasets(datasets);

            //  first check if datasets is in the cache
            DatasetsKey keyForDatasets = (DatasetsKey) DatasetsKeyGenerator.generateKey(datasets);
            ValueWrapper vwd = generateMetadataCacheForDatasets.get(keyForDatasets);
            log.debug("Checking to see if key is in the cache for {}", datasets.toString());
            if (vwd != null && vwd.get() != null) {
                log.debug("dataset in cache, getting it from cache for {}", datasets.toString());
                generatedMetadata = instanceMetadataService.generateMetadata(datasets);
            } else {
                
                log.debug("dataset not in cache, getting individual ones cache for {}", datasets.toString());
                //  next check if all individual datasets are in the cache
                boolean allDatasetsInCache = datasetsAsSingleDatasets.stream().allMatch(datasetsAsSingleDataset -> {
                    Cache generateMetadataCacheForSingleDatasets = getCacheForDatasets(datasetsAsSingleDataset);
                    DatasetsKey keyForSingleDataset = (DatasetsKey) DatasetsKeyGenerator.generateKey(datasetsAsSingleDataset);
                    ValueWrapper vw = generateMetadataCacheForSingleDatasets.get(keyForSingleDataset);
                    return vw != null && vw.get() != null;
                });

                if (allDatasetsInCache) {

                    // get each metdataitem individually for each data from cache
                    List<String> instanceMetadatas = datasetsAsSingleDatasets.stream().
                            map(datasetsAsSingleDataset -> instanceMetadataService.generateMetadata(datasetsAsSingleDataset)).
                            collect(toList());
                    // get counts
                    String nonMergeableMetadata = instanceMetadataService.generateNonMergeableMetadata(datasets);
                    log.debug("Non-mergeable instance metadata: {}", nonMergeableMetadata);

                    InstanceMetadata mergedInstanceMetadata = InstanceMetadata.merge(false, instanceMetadatas);
                    InstanceMetadata mergedInstanceMetadataWithCounts = InstanceMetadata.merge(true, mergedInstanceMetadata.build(), nonMergeableMetadata);

                    generateMetadataCacheForDatasets.put(DatasetsKeyGenerator.generateKey(datasets), mergedInstanceMetadataWithCounts.build());
                    generateMetadataCacheForDatasets.put(DatasetsKeyGenerator.generateKey(datasets), mergedInstanceMetadataWithCounts.build());

                    generatedMetadata = mergedInstanceMetadataWithCounts.build();
                } else {
                    // otherwise run full generateMetadata service, this could be slow so shouldnt happen if the priming cache has worked
                    generatedMetadata = instanceMetadataService.generateMetadata(datasets);
                }
            }
        }

        log.debug("Finished gathering metadata for {}", datasets.toString());
        return generatedMetadata;
    }
}
