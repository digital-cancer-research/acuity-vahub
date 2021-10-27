package com.acuity.visualisations.rest.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource({
    "classpath:spring/mybatis/mybatis-auditlogger.xml",
    "classpath:spring/spring-auditlogger-acuity.xml"
})
public class ApplicationAuditLoggerConfig {
}
