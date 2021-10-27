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

package com.acuity.visualisations.config;

import com.acuity.visualisations.cache.CustomEhCacheCacheManager;
import com.acuity.visualisations.cache.DiskCacheService;
import com.acuity.visualisations.cache.DiskStoreBootstrapAllCacheLoader;
import org.springframework.cache.CacheManager;
import org.springframework.cache.ehcache.EhCacheFactoryBean;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.io.ClassPathResource;

/**
 * Use spring-cache.xml for config
 * 
 * @author ksnd199
 */
@Configuration
@ImportResource({  
    "classpath:spring/detect/spring-cache.xml"
})
public class ApplicationEhCacheConfig {

    static {
        System.setProperty("net.sf.ehcache.enableShutdownHook", "true");
    }

    @Bean
    public CacheManager cacheManager() {
        return new CustomEhCacheCacheManager(ehCacheCacheManager().getObject());
    }

    @Bean
    public DiskCacheService diskCacheService() {
        return new DiskCacheService(cacheManager());
    }

    @Bean
    public EhCacheManagerFactoryBean ehCacheCacheManager() {
        EhCacheManagerFactoryBean cmfb = new EhCacheManagerFactoryBean();
        cmfb.setConfigLocation(new ClassPathResource("visualisations/ehcache.xml"));
        cmfb.setShared(true);
        return cmfb;
    }

    @Bean
    public EhCacheFactoryBean ehCacheFactory() {
        EhCacheFactoryBean ehCacheFactory = new EhCacheFactoryBean();
        ehCacheFactory.setCacheManager(ehCacheCacheManager().getObject());
        ehCacheFactory.setBootstrapCacheLoader(new DiskStoreBootstrapAllCacheLoader(diskCacheService()));
        return ehCacheFactory;
    }
}
