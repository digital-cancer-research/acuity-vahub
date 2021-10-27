package com.acuity.visualisations.rawdatamodel.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "classpath:inmemory.properties")
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class InmemoryConfig {
}
