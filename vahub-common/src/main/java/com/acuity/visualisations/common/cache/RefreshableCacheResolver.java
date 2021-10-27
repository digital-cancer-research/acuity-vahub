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

import com.acuity.va.security.acl.domain.Datasets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.CacheOperationInvocationContext;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.Collections;

import static com.acuity.visualisations.common.config.Constants.DETECT_PERSISTENT_CACHE;
import static com.acuity.visualisations.common.config.Constants.HAS_PERMISSION_VIEW_ONCOLOGY_PACKAGE;
import static com.acuity.visualisations.common.config.Constants.HAS_VIEW_DATASET_PERMISSION;
import static com.acuity.visualisations.common.config.Constants.ACUITY_DAILY_REFRESHABLE_CACHE;
import static com.acuity.visualisations.common.config.Constants.VIEW_ONCOLOGY_PERMISSION;
import static com.acuity.visualisations.common.config.Constants.VIEW_VISUALISATIONS_PERMISSION;

/**
 * Created by knml167 on 10/24/2016.
 */
@Component
@Slf4j
public class RefreshableCacheResolver implements CacheResolver {

    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private PermissionEvaluator permissionEvaluator;

    private Datasets getDatasets(CacheOperationInvocationContext<?> context) {

        Datasets ds = DatasetsFinder.findDatasetsObject(context.getArgs());

        if (ds != null) {
            return ds;
        } else {
            throw new IllegalStateException("Unable to get Datasets object from context: " + context);
        }
    }

    @Override
    public Collection<Cache> resolveCaches(CacheOperationInvocationContext<?> context) {
        if (hasPermissionToResolveCaches(context)) {
            return resolveCaches(
                    getDatasets(context),
                    context.getTarget().getClass().getSimpleName(),
                    resolveMethodName(context.getMethod(), context.getArgs())
            );
        } else {
            throw new SecurityException("Permission denied");
        }
    }

    public Collection<Cache> resolveCaches(Datasets datasets, String simpleClassName, String methodName) {
        String cacheName = resolveCacheName(datasets, simpleClassName, methodName);
        return Collections.singleton(cacheManager.getCache(cacheName));
    }

    public static String resolveCacheName(Datasets datasets, String simpleClassName, String methodName) {
        return resolveStartOfCacheName(datasets) + simpleClassName + "." + methodName;
    }

    public static String resolveStartOfCacheName(Datasets datasets) {
        return (datasets.isAcuityType() ? ACUITY_DAILY_REFRESHABLE_CACHE : DETECT_PERSISTENT_CACHE);
    }

    /**
     * In case the auto functionality, the additional method resolve is required (or will be getDistinctValues instead)
     */
    private String resolveMethodName(Method method, Object[] args) {
        String methodName = method.getName();
        /**
         * one of the arguments will be presented as cache name by this annotation
         */
        if (method.getAnnotation(CacheableManual.class) != null) {
            final Parameter[] params = method.getParameters();
            for (int i = 0; i < params.length; i++) {
                final ManualCacheName annotation = params[i].getAnnotation(ManualCacheName.class);
                if (annotation == null) {
                    continue;
                }

                methodName = (String) args[i];
                return methodName;
            }
            log.warn("Invalid argument list while manual cache management for " + methodName);
        }
        return methodName;
    }

    private boolean hasPermissionToResolveCaches(CacheOperationInvocationContext<?> context) {
        PreAuthorize preAuthorize = context.getTarget().getClass().getAnnotation(PreAuthorize.class);
        if (preAuthorize == null) {
            return true;
        }
        String preauthorizeCheck = preAuthorize.value();
        boolean hasPermission = false;
        switch (preauthorizeCheck) {
            case HAS_VIEW_DATASET_PERMISSION:
                hasPermission = permissionEvaluator.hasPermission(SecurityContextHolder.getContext().getAuthentication(),
                        getDatasets(context).getDatasetsList(), VIEW_VISUALISATIONS_PERMISSION);
                break;
            case HAS_PERMISSION_VIEW_ONCOLOGY_PACKAGE:
                hasPermission = permissionEvaluator.hasPermission(SecurityContextHolder.getContext().getAuthentication(),
                        getDatasets(context).getDatasetsList(), VIEW_ONCOLOGY_PERMISSION);
                break;
            default:
        }
        return hasPermission;
    }
}
