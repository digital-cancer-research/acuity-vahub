package com.acuity.visualisations.config.config;

import com.acuity.visualisations.cache.CustomEhCacheCacheManager;
import com.acuity.visualisations.config.ApplicationModelConfig;
import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.cache.CacheManager;
import org.springframework.cache.ehcache.EhCacheFactoryBean;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.support.SimpleThreadScope;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;

import static org.springframework.context.annotation.FilterType.ANNOTATION;

/**
 * @author ksnd199
 */
@Configuration
@EnableTransactionManagement
@ComponentScan(
        lazyInit = true,
        basePackages = {"com.acuity.visualisations", "com.acuity.visualisations.common"},
        excludeFilters = {
                @Filter(type = ANNOTATION,
                        value = {Configuration.class})
        }
)
@Import(ApplicationModelConfig.class)
@ImportResource({
        "classpath:spring/detect/mybatis/mybatis-model.xml"
})
@ActiveProfiles({"NoScheduledJobs", "it"})
public class ApplicationModelConfigITCase {


    @Bean
    public static CustomScopeConfigurer customScopeConfigurer() {
        CustomScopeConfigurer scopeConfigurer = new CustomScopeConfigurer();
        HashMap<String, Object> scopes = new HashMap<String, Object>();
        scopes.put(WebApplicationContext.SCOPE_REQUEST, new SimpleThreadScope());
        scopeConfigurer.setScopes(scopes);
        return scopeConfigurer;
    }


    @Bean
    public CacheManager cacheManager() {
        return new CustomEhCacheCacheManager(ehCacheCacheManager().getObject());
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
        return ehCacheFactory;
    }
}
