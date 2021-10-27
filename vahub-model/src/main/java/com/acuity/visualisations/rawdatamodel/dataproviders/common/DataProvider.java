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

package com.acuity.visualisations.rawdatamodel.dataproviders.common;

import com.acuity.visualisations.common.lookup.CacheableDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.common.kryo.KryoContext;
import com.acuity.visualisations.rawdatamodel.vo.AcuityEntity;
import com.acuity.va.security.acl.domain.Dataset;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.acuity.visualisations.rawdatamodel.dataproviders.common.DataProviderKeyGenerator.Params.CLASS;
import static com.acuity.visualisations.rawdatamodel.dataproviders.common.DataProviderKeyGenerator.Params.DATASET;

@Service
@NoArgsConstructor
@Slf4j
public class DataProvider implements CacheableDataProvider {

    public static final int CACHE_WRITE_LOCK_ACQUIRE_TIMEOUT_SEC = 5;
    private static final String DETECT = "detect";
    private static final String ACUITY = "visualisations";
    private final ConcurrentMap<Long, ReadWriteLock> datasetKryoLocks = new ConcurrentHashMap<>();
    private final ConcurrentMap<Long, ReadWriteLock> datasetCacheLocks = new ConcurrentHashMap<>();
    private final ReadWriteLock globalLock = new ReentrantReadWriteLock();

    private ConcurrentMap<ImmutablePair<Dataset, Class>, SoftReference<?>> cache = new ConcurrentHashMap<>();

    private String kryoStorage;

    private KryoContext kryoContext;

    @Autowired
    public DataProvider(@Value("${kryo.storage.location}") String kryoStorage, KryoContext context) {
        this.kryoStorage = kryoStorage;
        this.kryoContext = context;
    }

    private static void loadToFile(Kryo kryo, Supplier dataSupplier, final Path fileName) throws IOException {
        log.info("Getting events from origin");
        Object entities = dataSupplier.get();
        try (FileOutputStream fileOutputStream = getFileOutputStream(fileName); Output output = new Output(fileOutputStream)) {
            log.info("Creating kryo file {}", fileName);
            kryo.writeClassAndObject(output, entities);
            output.flush();
        }
    }

    private static FileOutputStream getFileOutputStream(Path fileName) throws FileNotFoundException {
        File storageFile = fileName.toFile();
        Path parent = fileName.getParent();
        if (parent == null) {
            throw new IllegalStateException("Could not create folder for kryo cache, because parent path is null");
        }
        File directory = parent.toFile();
        if (!(directory.mkdirs() || directory.exists())) {
            throw new IllegalStateException("Could not create folder for kryo cache " + directory.getAbsolutePath());
        }
        return new FileOutputStream(storageFile, false);
    }

    protected static int getEntityClassVersion(Class<?> clazz) {
        int version;
        final AcuityEntity acuityEntityAnnotation = clazz.getAnnotation(AcuityEntity.class);
        if (acuityEntityAnnotation != null) {
            version = acuityEntityAnnotation.version();
        } else {
            version = 0;
        }
        return version;
    }

    private static String getDatasetAndClassString(Dataset dataset, Class<?> clazz) {
        return String.format("%s::%d::%s::%s", dataset.getShortNameByType(), dataset.getId(), dataset.getName(), clazz.getSimpleName());
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    private <T> Collection<T> getFromCache(final Class clazz, final Dataset dataset,
                                           final Supplier<Collection<T>> sourceDataSupplier, ReadWriteLock datasetCacheLock) {

        boolean readLocked = true;
        datasetCacheLock.readLock().lock();
        try {
            final ImmutablePair<Dataset, Class> key = new ImmutablePair<>(dataset, clazz);
            //Concurrent hash map computeIfAbsent provides sync by itself, no need to writeLock here:
            Object o = cache.computeIfAbsent(key, ds -> new SoftReference<>(null)).get();
            if (o == null) {
                datasetCacheLock.readLock().unlock();
                readLocked = false;
                if (datasetCacheLock.writeLock().tryLock(CACHE_WRITE_LOCK_ACQUIRE_TIMEOUT_SEC, TimeUnit.SECONDS)) {
                    log.info("Data for {} was evicted from cache, reading again", getDatasetAndClassString(dataset, clazz));
                    try {
                        final Collection<T> res = sourceDataSupplier.get();
                        cache.put(key, new SoftReference<Object>(res));
                        return res;
                    } finally {
                        datasetCacheLock.writeLock().unlock();
                    }
                } else {
                    log.info("Data for {} was evicted from cache, but failed acquiring write lock in {} seconds, trying again",
                            getDatasetAndClassString(dataset, clazz), CACHE_WRITE_LOCK_ACQUIRE_TIMEOUT_SEC);
                    return getFromCache(clazz, dataset, sourceDataSupplier, datasetCacheLock);
                }
            } else {
                log.debug("Data for {} found in cache", getDatasetAndClassString(dataset, clazz));
                return (Collection<T>) o;
            }
        } finally {
            if (readLocked) {
                datasetCacheLock.readLock().unlock();
            }
        }
    }

    @Override
//    @Cacheable(keyGenerator = "dataProviderKeyGenerator", cacheResolver = "dataProviderCacheResolver")
    public <T> Collection<T> getData(
            @DataProviderKeyGenerator.Param(CLASS) final Class<T> clazz,
            @DataProviderKeyGenerator.Param(DATASET) final Dataset dataset,
            final Function<Dataset, Collection<T>> sourceDataSupplier) {

        final ReadWriteLock datasetKryoLock = getDatasetKryoLock(dataset.getId());
        final ReadWriteLock datasetCacheLock = getDatasetCacheLock(dataset.getId());
        try {
            globalLock.readLock().lock();

            return Collections.unmodifiableCollection(getFromCache(
                    clazz,
                    dataset,
                    () -> readDataset(clazz, dataset, sourceDataSupplier, datasetKryoLock), datasetCacheLock));
        } finally {
            globalLock.readLock().unlock();
        }
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    private <T> List<T> readDataset(
            Class<T> clazz,
            Dataset dataset,
            Function<Dataset, Collection<T>> sourceDataSupplier,
            ReadWriteLock datasetLock) {
        return kryoContext.borrow(kryo -> {
            datasetLock.writeLock().lock();
            datasetLock.readLock().lock();

            try {
                Path path = resolvePath(clazz, dataset);
                try {
                    if (!path.toFile().exists()) {
                        log.info("Data not found, loading {} into kryo path", getDatasetAndClassString(dataset, clazz));
                        loadToFile(kryo, () -> sourceDataSupplier.apply(dataset), path);
                    }

                    log.info("Reading {} from {}", getDatasetAndClassString(dataset, clazz), path);
                    final List<T> ts;
                    try (FileInputStream fileInputStream = new FileInputStream(path.toString()); Input input = new Input(fileInputStream)) {
                        ts = (List<T>) kryo.readClassAndObject(input);
                        log.info("Read {} items for {}", ts.size(), getDatasetAndClassString(dataset, clazz));
                        return ts;
                    }
                } catch (Exception e) {
                    try {
                        log.warn("Error reading kryo file {} : {}, reloading {} into kryo path", e.getClass(), e.getMessage(),
                                getDatasetAndClassString(dataset, clazz));
                        loadToFile(kryo, () -> sourceDataSupplier.apply(dataset), path);
                        log.info("Reading again {} from {}", getDatasetAndClassString(dataset, clazz), path);
                        try (FileInputStream fileInputStream = new FileInputStream(path.toString()); Input input = new Input(fileInputStream)) {
                            return (List<T>) kryo.readClassAndObject(input);
                        }
                    } catch (IOException another) {
                        log.warn("Second reading attempt failed", another);
                        throw new DataProviderException(another);
                    }
                }
            } finally {
                datasetLock.readLock().unlock();
                datasetLock.writeLock().unlock();
            }
        });
    }

    @Override
    public <T> Path resolvePath(Class<T> clazz, Dataset dataset) {
        int version;
        version = getEntityClassVersion(clazz);
        String fileName = String.format("%s_v%d_%s_dataset_%s.kryo", clazz.getSimpleName(), version,
                dataset.getShortNameByType(), dataset.getId().toString());
        return getKryoStoragePath().resolve(Paths.get(dataset.getShortNameByType(), dataset.getId().toString(), fileName));
    }

    @Override
    public void clearAllCacheFiles() {
        globalLock.writeLock().lock();
        try {
            clearKryoCache(null, null);
            clearSoftReferenceCache(null, null);
        } finally {
            globalLock.writeLock().unlock();
        }
    }

    @Override
    public void clearCacheForAcuity() {
        globalLock.writeLock().lock();
        try {
            clearKryoCache(ACUITY, null);
            clearSoftReferenceCache(ACUITY, null);
        } finally {
            globalLock.writeLock().unlock();
        }
    }

    @Override
    public void clearCacheForDetect() {
        globalLock.writeLock().lock();
        try {
            clearKryoCache(DETECT, null);
            clearSoftReferenceCache(DETECT, null);
        } finally {
            globalLock.writeLock().unlock();
        }
    }

    @Override
    public void clearCacheForDataset(final Dataset dataset) {
        final ReadWriteLock datasetKryoLock = getDatasetKryoLock(dataset.getId());
        final ReadWriteLock datasetCacheLock = getDatasetCacheLock(dataset.getId());
        globalLock.readLock().lock();
        try {
            datasetCacheLock.writeLock().lock();
            datasetKryoLock.writeLock().lock();
            try {
                clearKryoCache(dataset.getShortNameByType(), dataset.getId());
                clearSoftReferenceCache(dataset.getShortNameByType(), dataset.getId());
            } finally {
                datasetKryoLock.writeLock().unlock();
                datasetCacheLock.writeLock().unlock();
            }
        } finally {
            globalLock.readLock().unlock();
        }
    }

    private ReadWriteLock getDatasetKryoLock(final Long datasetId) {
        return datasetKryoLocks.computeIfAbsent(datasetId, id -> new ReentrantReadWriteLock());
    }

    private ReadWriteLock getDatasetCacheLock(final Long datasetId) {
        return datasetCacheLocks.computeIfAbsent(datasetId, id -> new ReentrantReadWriteLock());
    }

    private void clearKryoCache(String datasetType, Long datasetId) {

        final StringBuilder msg = new StringBuilder("Clearing kryo storage");
        Path dirPath = getKryoStoragePath();
        if (datasetType != null) {
            dirPath = dirPath.resolve(datasetType);
            msg.append(" for ").append(datasetType);
            if (datasetId != null) {
                dirPath = dirPath.resolve(datasetId.toString());
                msg.append(" dataset ").append(datasetId);
            }
        }
        log.info(msg.toString());
        try {
            if (dirPath.toFile().exists()) {
                FileUtils.cleanDirectory(dirPath.toFile());
                log.info(msg.append(" successful").toString());
            }
        } catch (IOException e) {
            log.warn(msg.append(" failed").toString(), e);
            // sometimes filesystem fails to delete files under high load, give it another chance after timeout
            sleep();
            try {
                FileUtils.cleanDirectory(dirPath.toFile());
                log.warn(msg.append(", but worked on second attempt").toString());
            } catch (IOException e1) {
                throw new DataProviderException(e1);
            }
        }
    }

    private void clearSoftReferenceCache(String datasetType, Long datasetId) {
        StringBuilder msg = new StringBuilder("Clearing soft reference cache");
        if (datasetType != null) {
            msg.append(" for ").append(datasetType);
            if (datasetId != null) {
                msg.append(" dataset ").append(datasetId);
            }
        }
        log.info(msg.toString());
        cache.keySet().stream().filter(k ->
                (datasetId == null || Objects.equals(k.getLeft().getId(), datasetId))
                        && (datasetType == null || datasetType.equalsIgnoreCase(k.getLeft().getShortNameByType()))
        ).forEach(k -> cache.remove(k));
    }

    private Path getKryoStoragePath() {
        return Paths.get(kryoStorage);
    }

    /**
     * Silent sleep
     */
    private static void sleep() {
        try {
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException ignored) {
            // Empty on purpose
        }
    }



}
