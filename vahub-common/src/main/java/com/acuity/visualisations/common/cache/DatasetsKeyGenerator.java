package com.acuity.visualisations.common.cache;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleKey;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * To be used instead of the default spring KeyGenerator SimpleKeyGenerator.
 *
 * @author ksnd199
 */
@Service
public class DatasetsKeyGenerator implements KeyGenerator {

    @Override
    public Object generate(Object target, Method method, Object... params) {
        return generateKey(params);
    }

    /*
     * Generate a DatasetsKey based on the specified parameters.
     */
    public static Object generateKey(Object... params) {
        // if params contains a Datasets object,  return a DatasetsKey, else SimpleKey
        if (DatasetsFinder.hasDatasets(params)) {
            return generateDatasetsKey(params);
        } else {
            return generateSimpleKey(params);
        }
    }

    /*
     * Generate a DatasetsKey based on the specified parameters.
     */
    public static DatasetsKey generateDatasetsKey(Object... params) {
        if (DatasetsFinder.hasDatasets(params)) {            
            return new DatasetsKey(DatasetsFinder.findDatasetsObject(params), params);
        }

        throw new IllegalArgumentException("No datasets object in " + Arrays.toString(params));
    }

    /**
     * Generate a key based on the specified parameters.
     */
    public static Object generateSimpleKey(Object... params) {
        if (params.length == 0) {
            return SimpleKey.EMPTY;
        }
        if (params.length == 1) {
            Object param = params[0];
            if (param != null && !param.getClass().isArray()) {
                return param;
            }
        }
        return new SimpleKey(params);
    }
}
