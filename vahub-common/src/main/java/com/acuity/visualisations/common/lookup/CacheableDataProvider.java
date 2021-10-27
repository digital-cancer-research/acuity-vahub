package com.acuity.visualisations.common.lookup;

import com.acuity.va.security.acl.domain.Dataset;

import java.nio.file.Path;
import java.util.Collection;
import java.util.function.Function;

public interface CacheableDataProvider {

    <T> Collection<T> getData(Class<T> clazz, Dataset dataset, Function<Dataset, Collection<T>> sourceDataSupplier);

    <T> Path resolvePath(Class<T> clazz, Dataset dataset);

    void clearAllCacheFiles();

    void clearCacheForAcuity();

    void clearCacheForDetect();

    void clearCacheForDataset(Dataset dataset);
}
