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

package com.acuity.visualisations.common.lookup;

import com.acuity.va.security.acl.domain.Dataset;
import com.acuity.va.security.acl.domain.Datasets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Lookup bean to lookup a interface dependent on the Datasets.
 *
 * Ie it looks up the acuity or detect version depending on the type of datasets in Datasets
 *
 * @deprecated since DETECT mode is removed, there is no need to dynamically look up for beans depending on their
 * ACUITY/DETECT type, so this mechanism can be removed and just regular dependency injection may be used instead
 *
 * @author ksnd199
 */
@Service
@Slf4j
@Deprecated
public class BeanLookupService implements ApplicationContextAware {
    static final String DETECT = "detect";
    static final String ACUITY = "acuity";

    private ApplicationContext ac;

    @Override
    public void setApplicationContext(ApplicationContext ac) throws BeansException {
        this.ac = ac;
    }

    public <T> T get(Datasets datasets, Class<T> requiredType) {

        if (datasets.isAcuityType()) {
            return getBeanByName(requiredType, ACUITY);
        } else if (datasets.isDetectType()) {
            return getBeanByName(requiredType, DETECT);
        } else {
            throw new IllegalStateException("Unknown Datasets type " + datasets);
        }
    }

    public <T> T get(Dataset dataset, Class<T> requiredType) {
        if (dataset.thisAcuityType()) {
            return getBeanByName(requiredType, ACUITY);
        } else if (dataset.thisDetectType()) {
            return getBeanByName(requiredType, DETECT);
        } else {
            throw new IllegalStateException("Unknown Dataset type " + dataset);
        }
    }

    public <T> T get(Datasets datasets, ResolvableType requiredType) {
        if (datasets.isAcuityType()) {
            return getBeanByName(requiredType, ACUITY);
        } else if (datasets.isDetectType()) {
            return getBeanByName(requiredType, DETECT);
        } else {
            throw new IllegalStateException("Unknown Datasets type " + datasets);
        }
    }

    public <T> T get(Dataset dataset, ResolvableType requiredType) {
        if (dataset.thisAcuityType()) {
            return getBeanByName(requiredType, ACUITY);
        } else if (dataset.thisDetectType()) {
            return getBeanByName(requiredType, DETECT);
        } else {
            throw new IllegalStateException("Unknown Dataset type " + dataset);
        }
    }

    public <T> T getRepository(Datasets datasets, Class<T> requiredType) {
        return get(datasets, requiredType);
    }

    public <T> T getService(Datasets datasets, Class<T> requiredType) {
        return get(datasets, requiredType);
    }

    public <T> T getBeanByName(Class<T> requiredType, String startsWith) {
        Map<String, T> beansOfType = ac.getBeansOfType(requiredType);

        String beanName = getBeanName(startsWith, beansOfType.keySet())
                .orElseThrow(() -> new IllegalStateException(String.format("Cannot find bean of type %s for %s",
                requiredType.getName(), startsWith)));

        return ac.getBean(beanName, requiredType);
    }

    @SuppressWarnings("unchecked")
    private <T> T getBeanByName(ResolvableType requiredType, String startsWith) {
        List<String> beanNamesForType = Arrays.asList(ac.getBeanNamesForType(requiredType));

        String beanName = getBeanName(startsWith, beanNamesForType)
                .orElseThrow(() -> new IllegalStateException(String.format("Cannot find bean of type %s for %s",
                requiredType.getType().getTypeName(), startsWith)));

        return (T) ac.getBean(beanName);
    }

    @SuppressWarnings("unchecked")
    public <T> T getBeanByType(Class<T> requiredType) {

        try {
            return ac.getBean(requiredType);
        } catch (Exception ex) {
            log.warn("No bean available for " + requiredType, ex);
            return null;
        }
    }

    private Optional<String> getBeanName(String startsWith, Collection<String> beansOfType) {
        return beansOfType.stream()
                .filter(bean -> bean.toLowerCase().startsWith(startsWith))
                .findFirst();
    }
}
