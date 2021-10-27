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

package com.acuity.visualisations.rest.resources.cache;

import com.acuity.visualisations.common.cache.ClearCacheStatus;
import com.acuity.visualisations.common.cache.RefreshCacheService;
import com.acuity.visualisations.common.study.metadata.InstanceMetadataService;
import com.acuity.visualisations.rawdatamodel.dataset.info.InfoService;
import com.acuity.visualisations.rest.config.logging.EnhancedLoggingFilter;
import com.acuity.va.security.acl.domain.Dataset;
import com.acuity.va.security.acl.domain.Datasets;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.concurrent.DelegatingSecurityContextExecutorService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.MediaType.ALL_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created by ksnd199.
 *
 * Clear Cache Operations:
 *
 * <code>
 * /cache/clear/acuity
 * /cache/clear/detect
 * /cache/clear/all
 * /cache/clear/detect/{id}
 * /cache/clear/acuity/{id}
 *
 * Delete filters Operations:
 *
 * /filters/clear/acuity
 * /filters/clear/detect
 * /filters/clear/detect/{id}
 * /filters/clear/acuity/{id}
 *
 * /refresh/acuity
 * /refresh/detect
 *
 * /reload/acuity/{id}
 * /reload/detect/{id}
 * </code>
 */
@RestController
@RequestMapping(value = "/resources/security/", consumes = {APPLICATION_JSON_VALUE, ALL_VALUE}, produces = APPLICATION_JSON_VALUE)
@Slf4j
public class RefreshCacheResource {
    private ExecutorService executorService;

    public RefreshCacheResource() {
        executorService = new DelegatingSecurityContextExecutorService(Executors.newFixedThreadPool(1)); // queue 1 at a time for datasets
    }

    @Autowired
    private RefreshCacheService cacheService;
    @Autowired
    private InstanceMetadataService instanceMetadataService;
    @Autowired
    private InfoService infoService;

    @ApiOperation(
            value = "Clear detect cache for dataset",
            nickname = "clearDetectCacheForDataset",
            response = ResponseEntity.class,
            httpMethod = "GET"
    )
    @RequestMapping(value = "/clear/detect/{id}", method = GET)
    public ResponseEntity clearDetectCacheForDataset(@PathVariable("id") Long id) {

        Datasets detectDatasets = Datasets.toDetectDataset(id);
        Callable fn = () -> cacheService.clearCachesForDatasets(detectDatasets);
        return cacheService.tryLock(fn);
    }

    @ApiOperation(
            value = "Clear acuity cache for dataset",
            nickname = "clearAcuityCacheForDataset",
            response = ResponseEntity.class,
            httpMethod = "GET"
    )
    @RequestMapping(value = "/clear/acuity/{id}", method = GET)
    public ResponseEntity clearAcuityCacheForDataset(@PathVariable("id") Long id) {

        Datasets acuityDatasets = Datasets.toAcuityDataset(id);
        Callable fn = () -> cacheService.clearCachesForDatasets(acuityDatasets);
        return cacheService.tryLock(fn);
    }

    /**
     * @deprecated
     */
    @Deprecated
    @ApiOperation(
            value = "Clear detect cache fpr etl",
            nickname = "clearDetectCacheForETL",
            response = ResponseEntity.class,
            httpMethod = "GET"
    )
    @RequestMapping(value = "/clear", method = GET)
    public ResponseEntity clearDetectCacheForETL() {

        Callable fn = () -> cacheService.clearDetectCaches();
        return cacheService.tryLock(fn);
    }

    @ApiOperation(
            value = "Refresh all filters and caches",
            nickname = "clearAllFiltersAndCaches",
            response = ResponseEntity.class,
            httpMethod = "GET"
    )
    @RequestMapping(value = "/refresh/all", method = GET)
    public ResponseEntity refreshAllFiltersAndCaches() {

        Callable fn = () -> cacheService.refreshAll();
        return cacheService.tryLock(fn);
    }

    @ApiOperation(
            value = "Refresh detect filters and caches",
            nickname = "clearDetectFiltersAndCaches",
            response = ResponseEntity.class,
            httpMethod = "GET"
    )
    @RequestMapping(value = "/refresh/detect", method = GET)
    public ResponseEntity refreshDetectFiltersAndCaches() {

        Callable fn = () -> cacheService.refreshAllForDetect();
        return cacheService.tryLock(fn);
    }

    @ApiOperation(
            value = "Refresh acuity filters and caches",
            nickname = "clearAcuityFiltersAndCaches",
            response = ResponseEntity.class,
            httpMethod = "GET"
    )
    @RequestMapping(value = "/refresh/acuity", method = GET)
    public ResponseEntity refreshAcuityFiltersAndCaches() {

        Callable fn = () -> cacheService.refreshAllForAcuity();
        return cacheService.tryLock(fn);
    }

    @ApiOperation(
            value = "Reload acuity filters and caches and reload info cache",
            nickname = "reloadAcuityFiltersAndCachesForDatasetsAndReloadInfoCache",
            response = ResponseEntity.class,
            httpMethod = "GET"
    )
    @RequestMapping(value = "/reload/acuity/{id}", method = GET)
    public ResponseEntity reloadAcuityFiltersAndCachesForDatasetsAndReloadInfoCache(@PathVariable("id") Long id)
            throws ExecutionException, InterruptedException {
        log.info("Called reloading of dataset {}", id);
        Callable fn = () -> {
            registerAuth();
            Datasets acuityDatasets = Datasets.toAcuityDataset(id);
            log.info("Starting reloading of dataset {}", acuityDatasets);
            ClearCacheStatus refreshAllForDatasets = cacheService.refreshAllForDatasets(acuityDatasets);
            instanceMetadataService.generateMetadata(acuityDatasets);

            log.info("Finished reloading of dataset {}", acuityDatasets);
            return refreshAllForDatasets;
        };

        Future<ResponseEntity> futureTask = executorService.submit(() -> cacheService.run(fn));
        log.info("Queued reloading of dataset {}", id);
        return futureTask.get();
    }

    @ApiOperation(
            value = "Reload detect filters and caches and reload info cache",
            nickname = "reloadDetectFiltersAndCachesForDatasetsAndReloadInfoCache",
            response = ResponseEntity.class,
            httpMethod = "GET"
    )
    @RequestMapping(value = "/reload/detect/{id}", method = GET)
    public ResponseEntity reloadDetectFiltersAndCachesForDatasetsAndReloadInfoCache(@PathVariable("id") Long id)
            throws ExecutionException, InterruptedException {
        log.info("Called reloading of dataset {}", id);

        Callable fn = () -> {
            registerAuth();
            Datasets detectDatasets = Datasets.toDetectDataset(id);
            log.info("Starting reloading of dataset {}", detectDatasets);
            ClearCacheStatus refreshAllForDatasets = cacheService.refreshAllForDatasets(detectDatasets);
            instanceMetadataService.generateMetadata(detectDatasets);

            log.info("Finished reloading of dataset {}", detectDatasets);
            return refreshAllForDatasets;
        };

        Future<ResponseEntity> futureTask = executorService.submit(() -> cacheService.run(fn));
        log.info("Queued reloading of dataset {}", id);
        return futureTask.get();
    }

    private void registerAuth() {
        Optional<Authentication> authentication = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication());
        authentication.ifPresent(EnhancedLoggingFilter::register);
    }

    @ApiOperation(
            value = "Lists the datasets in the priming cache",
            nickname = "listPrimedCachedDatasets",
            response = List.class,
            httpMethod = "GET"
    )
    @RequestMapping(value = "/primedcaches", method = GET)
    public List<Dataset> listPrimedCachedDatasets() {

        return cacheService.listPrimedCachedDatasets().stream().
                map(dss -> {
                    if (dss.getDatasetsList().size() == 1) {
                        return dss.getDatasetsList().get(0);
                    }

                    return null;
                }).filter(Objects::nonNull).collect(toList());
    }

    @ApiOperation(
            value = "Lists the missing datasets in the priming cache",
            nickname = "listMissedPrimedCachedDatasets",
            response = List.class,
            httpMethod = "GET"
    )
    @RequestMapping(value = "/missedprimedcaches", method = GET)
    public Collection<Dataset> listMissedPrimedCachedDatasets() {
        List<Dataset> listPrimedCachedDatasets = listPrimedCachedDatasets().stream()
                .distinct()
                .collect(toList());
        List<Dataset> allDatasets = infoService.generateObjectIdentities().stream().
                filter(roi -> roi.thisDatasetType()).
                map(roi -> ((Dataset) roi)).
                collect(toList());

        return CollectionUtils.subtract(allDatasets, listPrimedCachedDatasets);
    }
}
