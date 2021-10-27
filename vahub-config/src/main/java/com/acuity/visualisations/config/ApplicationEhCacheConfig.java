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
