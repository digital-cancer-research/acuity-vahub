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

package com.acuity.visualisations.rest.config;

import com.acuity.visualisations.config.ApplicationEhCacheConfig;
import com.acuity.visualisations.config.ApplicationEnableExecutorConfig;
import com.acuity.visualisations.config.ApplicationModelConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.event.EventListener;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static springfox.documentation.builders.PathSelectors.regex;

@SpringBootApplication(scanBasePackages = {
        "com.acuity.va.security.auth",
        "com.acuity.visualisations.rest.resources",
        "com.acuity.visualisations.rest.util"
})
@EnableSwagger2
@EnableAspectJAutoProxy(proxyTargetClass = true)
@Slf4j
public class ApplicationWeb extends SpringBootServletInitializer {

    private static Object[] springResources = new Object[]{ApplicationAuditLoggerConfig.class, ApplicationModelConfig.class,
            ApplicationEhCacheConfig.class, ApplicationEnableExecutorConfig.class, ApplicationWeb.class/*, ApplicationCohortEditorConfig.class*/};

    @Bean
    public Docket documentation() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(regex("/resources.*"))
                .build()
                .pathMapping("/")
                .apiInfo(metadata());
    }

    @Bean
    public UiConfiguration uiConfig() {
        return UiConfiguration.DEFAULT;
    }

    private ApiInfo metadata() {
        return new ApiInfoBuilder()
                .title("DETECT REST API")
                .description("This is the documentation for the REST services for the DETECT web application.")
                .version("1.0")
                .contact("DETECT")
                .build();
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(springResources);
    }

    @Bean
    public ServletListenerRegistrationBean<HttpSessionEventPublisher> httpSessionEventPublisher() {
        return new ServletListenerRegistrationBean<>(new HttpSessionEventPublisher());
    }

    @SuppressWarnings("checkstyle:linelength")
    @EventListener(ApplicationReadyEvent.class)
    public void onStartUp() {
        // The printed message that states "vahub has started" on SpringBoot startup. ASCII symbols are used to make it readable in both IDE console log and file log.
        log.info("\n888     888     d8888 888    888 888     888 888888b.        888    888        d8888  .d8888b.        .d8888b. 88888888888     d8888 8888888b. 88888888888 8888888888 8888888b.  \n"
               + "888     888    d88888 888    888 888     888 888  \"88b       888    888       d88888 d88P  Y88b      d88P  Y88b    888        d88888 888   Y88b    888     888        888  \"Y88b \n"
               + "888     888   d88P888 888    888 888     888 888  .88P       888    888      d88P888 Y88b.           Y88b.         888       d88P888 888    888    888     888        888    888 \n"
               + "Y88b   d88P  d88P 888 8888888888 888     888 8888888K.       8888888888     d88P 888  \"Y888b.         \"Y888b.      888      d88P 888 888   d88P    888     8888888    888    888 \n"
               + " Y88b d88P  d88P  888 888    888 888     888 888  \"Y88b      888    888    d88P  888     \"Y88b.          \"Y88b.    888     d88P  888 8888888P\"     888     888        888    888 \n"
               + "  Y88o88P  d88P   888 888    888 888     888 888    888      888    888   d88P   888       \"888            \"888    888    d88P   888 888 T88b      888     888        888    888 \n"
               + "   Y888P  d8888888888 888    888 Y88b. .d88P 888   d88P      888    888  d8888888888 Y88b  d88P      Y88b  d88P    888   d8888888888 888  T88b     888     888        888  .d88P \n"
               + "    Y8P  d88P     888 888    888  \"Y88888P\"  8888888P\"       888    888 d88P     888  \"Y8888P\"        \"Y8888P\"     888  d88P     888 888   T88b    888     8888888888 8888888P\"  ");
    }

    public static void main(String[] args) {
        SpringApplication.run(springResources, args);
    }
}
