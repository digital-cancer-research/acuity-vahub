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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.jndi.JndiTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.naming.NamingException;
import javax.sql.DataSource;

import static org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE;

/**
 *
 * @author ksnd199
 */
@Configuration
//@EnableCaching
@EnableAutoConfiguration
//@EnableScheduling
@EnableTransactionManagement
@EnableConfigurationProperties
@ComponentScan(
        basePackages = {"com.acuity.visualisations", "com.acuity.visualisations.common"},
        excludeFilters = {
            @Filter(type = ASSIGNABLE_TYPE, value = {ApplicationEhCacheConfig.class, ApplicationEnableExecutorConfig.class})
        }
)
@ImportResource({
    "classpath:spring/detect/mybatis/mybatis-model.xml"
})
public class ApplicationModelConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public TomcatDatasourceProperties acuityDatasourceProperties() {
        return new TomcatDatasourceProperties();
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public TomcatDatasourceProperties auditlogDatasourceProperties() {
        return new TomcatDatasourceProperties();
    }

    @Profile("!jndi")
    @Bean
    public DataSource acuityDataSource() {
        return TomcatDatasourceProperties.getDataSourceFromProperties(acuityDatasourceProperties());
    }

    @Profile("!jndi")
    @Bean
    @Primary
    public DataSource auditlogDataSource() {
        return TomcatDatasourceProperties.getDataSourceFromProperties(auditlogDatasourceProperties());
    }
    
    ////////////////////////////////////
    // JNDI config for prod
    ////////////////////////////////////
    @Value("${spring.datasource.normal.jndiLookupName:default}")
    private String normalDataSourceJndiName;
    @Value("${spring.datasource.visualisations.jndiLookupName:default}")
    private String acuityDataSourceJndiName;
    @Value("${spring.datasource.auditlog.jndiLookupName:default}")
    private String auditLoggerDataSourceJndiName;

    @Profile("jndi")
    @Bean(name = "dataSource")
    public DataSource dataSourceJndi() throws NamingException {
        JndiTemplate jndiTemplate = new JndiTemplate();
        return (DataSource) jndiTemplate.lookup(normalDataSourceJndiName);
    }

    @Profile("jndi")
    @Bean(name = "acuityDataSource")
    public DataSource acuityDataSourceJndi() throws NamingException {
        JndiTemplate jndiTemplate = new JndiTemplate();
        return (DataSource) jndiTemplate.lookup(acuityDataSourceJndiName);
    }

    @Profile("jndi")
    @Bean(name = "auditlogDataSource")
    @Primary
    public DataSource auditlogDataSourceJndi() throws NamingException {
        JndiTemplate jndiTemplate = new JndiTemplate();
        return (DataSource) jndiTemplate.lookup(auditLoggerDataSourceJndiName);
    }
}
