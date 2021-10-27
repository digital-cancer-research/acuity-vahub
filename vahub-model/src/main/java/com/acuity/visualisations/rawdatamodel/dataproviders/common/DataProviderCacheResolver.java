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

import com.acuity.visualisations.common.cache.RefreshableCacheResolver;
import com.acuity.va.security.acl.domain.Dataset;
import com.acuity.va.security.acl.domain.Datasets;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.CacheOperationInvocationContext;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;

/**
 * Created by knml167 on 10/24/2016.
 */
@Component
public class DataProviderCacheResolver implements CacheResolver {
    @Autowired
    private CacheManager cacheManager;

    @Override
    @SneakyThrows
    public Collection<? extends Cache> resolveCaches(CacheOperationInvocationContext<?> context) {
        Datasets datasets = null;
        Class clazz = null;
        final Annotation[][] parameterAnnotations;
        String prefix = "";
        parameterAnnotations = (DataProvider.class).getDeclaredMethod("getData", Class.class, Dataset.class, Function.class).getParameterAnnotations();
        for (int i = 0; i < parameterAnnotations.length; i++) {
            for (Annotation a : parameterAnnotations[i]) {
                if (a instanceof DataProviderKeyGenerator.Param) {
                    switch (((DataProviderKeyGenerator.Param) a).value()) {
                        case DATASET:
                            datasets = new Datasets((Dataset) context.getArgs()[i]);
                            prefix = RefreshableCacheResolver.resolveStartOfCacheName(datasets);
                            break;
                        case CLASS:
                            clazz = (Class) context.getArgs()[i];
                            break;
                        default:
                            break;
                    }
                }
            }
        }
        //we don't need anything except class for now, though I'll keep these results for just in case
        String cacheName = prefix + "dataProvider-" + (clazz != null ? clazz.getSimpleName() : "null");
        return Collections.singleton(cacheManager.getCache(cacheName));
    }


}
