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

package com.acuity.visualisations.cohorteditor.config;

import com.acuity.visualisations.config.TomcatDatasourceProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.jndi.JndiTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

/**
 *
 * @author ksnd199
 */
@Configuration
@EnableConfigurationProperties
public class PersistenceJPAConfig {

    @Value("${spring.datasource.cohorteditor.jndiLookupName:default}")
    private String cohortEditorDataSourceJndiName;

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public TomcatDatasourceProperties cohorteditorDatasourceProperties() {
        return new TomcatDatasourceProperties();
    }

    @Profile("!jndi")
    @Bean
    public DataSource cohortEditorDataSource() {
        return TomcatDatasourceProperties.getDataSourceFromProperties(cohorteditorDatasourceProperties());
    }

    @Profile("jndi")
    @Bean(name = "cohortEditorDataSource")
    public DataSource cohortEditorDataSourceJndi() throws NamingException {
        JndiTemplate jndiTemplate = new JndiTemplate();
        return (DataSource) jndiTemplate.lookup(cohortEditorDataSourceJndiName);
    }

    @Profile("!jndi")
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(cohortEditorDataSource());

        return setConfig(em);
    }

    @Profile("jndi")
    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryJndi() throws NamingException {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(cohortEditorDataSourceJndi());

        return setConfig(em);
    }

    private LocalContainerEntityManagerFactoryBean setConfig(LocalContainerEntityManagerFactoryBean em) {
        em.setPackagesToScan("com.acuity.visualisations.cohorteditor", "com.acuity.visualisations.cohorteditor.entity");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(true);
        vendorAdapter.setShowSql(true);
        em.setJpaVendorAdapter(vendorAdapter);
        em.setJpaProperties(additionalProperties());

        return em;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);

        return transactionManager;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    Properties additionalProperties() {
        Properties properties = new Properties();
        properties.setProperty("hibernate.hbm2ddl.auto", "validate");
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQL94Dialect");
        properties.setProperty("hibernate.enable_lazy_load_no_trans", "true");

        properties.setProperty("javax.persistence.schema-generation.create-source", "metadata");
        properties.setProperty("javax.persistence.schema-generation.scripts.action", "create");
        properties.setProperty("javax.persistence.schema-generation.scripts.create-target", "target/create.sql");

        return properties;
    }
}
