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

package com.acuity.visualisations.common.cache;

import com.acuity.visualisations.common.lookup.CacheableDataProvider;
import com.acuity.visualisations.common.study.metadata.InstanceMetadataService;
import com.acuity.va.security.acl.domain.Datasets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import static com.acuity.visualisations.common.config.Constants.DETECT_PERSISTENT_CACHE;
import static com.acuity.visualisations.common.config.Constants.ACUITY_DAILY_REFRESHABLE_CACHE;
import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * @author Glen
 */
@Component
@Slf4j
public class RefreshCacheService {
    private ReentrantLock allCacheRefreshLock = new ReentrantLock(); // only allow 1 at a time, refuse others

    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private CacheableDataProvider dataProvider;

    /**
     * Refreshed filters and caches
     */
    public ClearCacheStatus refreshAll() {
        log.info("Ready to refresh");
        return clearAllCaches();
    }

    public ClearCacheStatus refreshAllForDatasets(Datasets datasets) {
        log.info("Ready to refresh");
        return clearCachesForDatasets(datasets);
    }

    /**
     * Refreshed filters and caches for acuity
     */
    public ClearCacheStatus refreshAllForAcuity() {
        log.info("Ready to refresh for acuity");
        return clearAcuityCaches();
    }

    /**
     * Refreshed filters and caches for detect
     */
    public ClearCacheStatus refreshAllForDetect() {
        log.info("Ready to refresh for detect");
        return clearDetectCaches();
    }

    /**
     * Clears all acuity caches
     *
     * @return ClearCacheStatus which contains the names of cleared
     */
    public ClearCacheStatus clearAcuityCaches() {
        log.info("Ready to clear acuity caches");
        dataProvider.clearCacheForAcuity();
        return clearMemoryCaches(ACUITY_DAILY_REFRESHABLE_CACHE);
    }

    /**
     * Clears all caches for a datasets object
     *
     * @return ClearCacheStatus which contains the names of cleared
     */
    public ClearCacheStatus clearCachesForDatasets(Datasets datasets) {
        datasets.getDatasets().forEach(dataProvider::clearCacheForDataset);
        log.info("Ready to remove keys caches for {}", datasets);

        return clearMemoryCaches(datasets);
    }

    /**
     * Clears all caches
     *
     * @return ClearCacheStatus which contains the names of cleared
     */
    public ClearCacheStatus clearAllCaches() {
        log.info("Ready to clear alls caches");

        dataProvider.clearAllCacheFiles();

        return clearMemoryCaches(ACUITY_DAILY_REFRESHABLE_CACHE);
    }

    /**
     * Clears all detect caches
     *
     * @return ClearCacheStatus which contains the names of cleared
     */
    public ClearCacheStatus clearDetectCaches() {
        log.info("Ready to clear detect caches");

        dataProvider.clearCacheForDetect();

        return clearMemoryCaches(DETECT_PERSISTENT_CACHE);
    }

    /**
     * Method for removing keys from memory caches using cache manager for datasets
     *
     * @return ClearCacheStatus which contains the names of cleared
     */
    private ClearCacheStatus clearMemoryCaches(Datasets datasets) {
        String startsWith = RefreshableCacheResolver.resolveStartOfCacheName(datasets);

        return removeKeysFromMemoryCaches(datasets, startsWith);
    }

    /**
     * Method for clearing caches using cache manager.
     *
     * Because the bootstraploader put all the disk caches back into memory (data still on disk) then clearing the memory ones clears the disk ones too.
     *
     * @return ClearCacheStatus which contains the names of cleared
     */
    private ClearCacheStatus clearMemoryCaches(String startsWith) {
        log.info("Ready to clear caches, startsWith: {}", startsWith);

        Collection<String> cacheNames = cacheManager.getCacheNames();
        ClearCacheStatus status = new ClearCacheStatus();

        List<String> toClearNames = newArrayList();
        List<String> toRetainNames = newArrayList();

        if (isEmpty(startsWith)) {
            toClearNames = newArrayList(cacheNames);
        } else {
            toClearNames = cacheNames.stream().filter(n -> n.startsWith(startsWith)).collect(toList());
            toRetainNames = cacheNames.stream().filter(n -> !n.startsWith(startsWith)).collect(toList());
        }

        for (String toClearName : toClearNames) {

            cacheManager.getCache(toClearName).clear();
            log.debug("Cleared cache: " + toClearName);
            status.getClearedCacheNames().add(toClearName);
        }
        for (String toRetainName : toRetainNames) {

            log.debug("Retained cache: " + toRetainName);
            status.getRetainedCacheNames().add(toRetainName);
        }

        log.info("caches cleared");

        return status;
    }

    /**
     * Method for removing keys from memory caches using cache manager for datasets
     *
     * @return ClearCacheStatus which contains the names of cleared
     */
    private ClearCacheStatus removeKeysFromMemoryCaches(Datasets datasets, String startsWith) {
        log.info("Ready to remove keys from caches, startsWith: {}, datasets: {}", startsWith, datasets);

        Collection<String> cacheNames = cacheManager.getCacheNames();
        ClearCacheStatus status = new ClearCacheStatus();

        List<String> toRemoveNames = newArrayList();
        List<String> noRemoveNames = newArrayList();

        if (isEmpty(startsWith)) {
            toRemoveNames = newArrayList(cacheNames);
        } else {
            toRemoveNames = cacheNames.stream().filter(n -> n.startsWith(startsWith)).collect(toList());
            noRemoveNames = cacheNames.stream().filter(n -> !n.startsWith(startsWith)).collect(toList());
        }

        for (String toRemoveName : toRemoveNames) {

            AtomicInteger removedKeys = new AtomicInteger(0);
            // NB:  Could improve this by using ehcache search API
            net.sf.ehcache.Cache ehcache = ((net.sf.ehcache.Cache) cacheManager.getCache(toRemoveName).getNativeCache());
            ehcache.getKeys().forEach(k -> {
                if (k instanceof DatasetsKey) {
                    DatasetsKey dsk = ((DatasetsKey) k);

                    // if any of the ids in dataseys key is in the datasets to remove then remove
                    // ie if we have key ds = 1,2 and we are removing ds = 1, then disjoint([1,2], [1]) = true
                    //  double check its removing the same type aswell
                    if (!Collections.disjoint(dsk.getDatasets().getIds(), datasets.getIds())
                            && dsk.getDatasets().isAcuityType() == datasets.isAcuityType()) {
                        ehcache.remove(dsk);
                        removedKeys.incrementAndGet();
                    }
                }
            });

            if (removedKeys.get() != 0) {
                log.debug("Removed #{} keys from: {}", removedKeys.get(), toRemoveName);
                status.getClearedCacheNames().add(toRemoveName);
            }
        }

        for (String noRemoveName : noRemoveNames) {

            log.debug("No keys removed from cache: " + noRemoveName);
            status.getRetainedCacheNames().add(noRemoveName);
        }

        log.info("caches removed keys");

        return status;
    }

    public ResponseEntity tryLock(Callable fn) {
        if (allCacheRefreshLock.tryLock()) {
            try {
                return run(fn);
            } finally {
                allCacheRefreshLock.unlock();
            }
        } else {
            return new ResponseEntity("Refresh already started", HttpStatus.OK);
        }
    }

    public ResponseEntity run(Callable fn) {
        try {
            Object returned = fn.call();
            if (returned != null) {
                return new ResponseEntity(returned, HttpStatus.OK);
            } else {
                return new ResponseEntity(HttpStatus.OK);
            }
        } catch (Exception e) {
            return new ResponseEntity(e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Lists all the cached datasets in the acuity-refreshable-InstanceMetadataService.generateMetadata and
     * detect-nonerefreshable-InstanceMetadataService.generateMetadata
     */
    public List<Datasets> listPrimedCachedDatasets() {

        String acuityCacheName = RefreshableCacheResolver.resolveCacheName(Datasets.toAcuityDataset(1L),
                InstanceMetadataService.class.getSimpleName(), "generateMetadata");
        String detectCacheName = RefreshableCacheResolver.resolveCacheName(Datasets.toDetectDataset(1L),
                InstanceMetadataService.class.getSimpleName(), "generateMetadata");

        List<Datasets> acuityDatasets = listCachedDatasets(acuityCacheName);
        List<Datasets> detectDatasets = listCachedDatasets(detectCacheName);

        return newArrayList(concat(acuityDatasets, detectDatasets));
    }

    /**
     * Lists all the cached datasets in the cache
     */
    private List<Datasets> listCachedDatasets(String cacheName) {
        log.info("Listing dataset caches for {}", cacheName);

        net.sf.ehcache.Cache ehcache = ((net.sf.ehcache.Cache) cacheManager.getCache(cacheName).getNativeCache());
        List<Object> keys = ehcache.getKeys();

        return keys.stream().map(k -> {
            if (k instanceof DatasetsKey) {
                DatasetsKey dsk = ((DatasetsKey) k);
                return dsk.getDatasets();
            }

            return null;
        }).filter(Objects::nonNull).collect(toList());
    }
}
