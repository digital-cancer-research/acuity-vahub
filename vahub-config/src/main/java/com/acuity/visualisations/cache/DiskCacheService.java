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

package com.acuity.visualisations.cache;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.ehcache.EhCacheCacheManager;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;

/**
 * Class to interact with the disk store of ehcache
 * 
 * @author ksnd199
 */
@Slf4j
public class DiskCacheService {
    public static final String LOCKFILE_NAME = "ehcache-diskstore.lock";

    private CacheManager cacheManager;

    public DiskCacheService(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public enum CACHETYPE {
        ALL(allfilter), ACUITY(acuityfilter), DETECT(detectfilter);

        private Predicate<? super Path> filter;

        CACHETYPE(Predicate<? super Path> predicate) {
            this.filter = predicate;
        }
    }

    private static Predicate<? super Path> isCacheDataFileAndNotLock
            = (p) -> p.toFile().isFile() && !p.getFileName().toString().equals(LOCKFILE_NAME) && p.getFileName().toString().endsWith(".data");
    private static Predicate<? super Path> allfilter = (p) -> true;
    private static Predicate<? super Path> acuityfilter = (p) -> p.getFileName().toString().startsWith("visualisations");
    private static Predicate<? super Path> detectfilter = (p) -> p.getFileName().toString().startsWith("detect");

    public String getDiskLocation() {
        return ((EhCacheCacheManager) cacheManager).getCacheManager().getConfiguration().getDiskStoreConfiguration().getPath();
    }

    public Path getDiskLocationPath() {
        return Paths.get(getDiskLocation());
    }

    private List<Path> listDiskCachesImpl(Predicate<? super Path> filterPredicate) throws IOException {
        return Files.list(getDiskLocationPath()).filter(isCacheDataFileAndNotLock).filter(filterPredicate).collect(toList());
    }

    private List<String> iterateOverDiskCaches(Predicate<? super Path> filterPredicate, Consumer<String> consumer) throws IOException {
        List<String> cacheNames = transformPathsToCacheNames(listDiskCachesImpl(filterPredicate));

        cacheNames.forEach(consumer);

        return cacheNames;
    }

    private List<String> transformPathsToCacheNames(List<Path> paths) {
        return paths.stream().map(this::decodePath).distinct().collect(toList());
    }

    private void clearDiskCache(String cacheName) {
        log.debug("Cleared disk cache {}", cacheName);
        Cache cache = cacheManager.getCache(cacheName);
        cache.get(""); // make alive if its not been loaded into memory
        ((net.sf.ehcache.Cache) cache.getNativeCache()).removeAll();
    }

    private void enableDiskCache(String cacheName) {

        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            log.debug("Enable disk cache {}", cacheName);
            ((net.sf.ehcache.Cache) cache.getNativeCache()).getKeys(); // make alive if its not been loaded into memory        
        } else {
            log.debug("disk cache {} is null", cacheName);
        }
    }

    private String decodePath(Path p) {
        String cacheNameEncoded = p.getFileName().toString().replaceAll("00", ""); // extra 00 in enocding, not sure where its from
        try {
            String cacheNameDecoded = URLDecoder.decode(cacheNameEncoded, "UTF-8");
            while (cacheNameDecoded.endsWith(".data")) {
                cacheNameDecoded = FilenameUtils.removeExtension(cacheNameDecoded);
            }

            return cacheNameDecoded;
        } catch (UnsupportedEncodingException ex) {
            log.error("Unable to decode {}", p, ex);
            return null;
        }
    }

    /**
     * @deprecated
     */
    @Deprecated
    public List<Path> listDiskCaches(CACHETYPE type) throws IOException {
        return listDiskCachesImpl(type.filter);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public List<String> deleteDiskCaches(CACHETYPE type) throws IOException {
        return iterateOverDiskCaches(type.filter, this::clearDiskCache);
    }

    public List<String> enableDiskCaches(CACHETYPE type) throws IOException {
        return iterateOverDiskCaches(type.filter, this::enableDiskCache);
    }
}
