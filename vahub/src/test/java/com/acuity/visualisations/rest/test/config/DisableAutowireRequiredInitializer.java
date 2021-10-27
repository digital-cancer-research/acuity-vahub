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

package com.acuity.visualisations.rest.test.config;

import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.support.GenericApplicationContext;

/**
 * Disables @Autowired(required=true) is tests for mocking deep spring dependency trees
 * 
 * @author ksnd199
 */
public class DisableAutowireRequiredInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {

        // Register the AutowiredAnnotationBeanPostProcessor while initalizing
        // the context so we get there before any @Autowire resolution happens
        // We set the "requiredParameterValue" so that @Autowire fields are not 
        // required to be resolved. Very useful for a test context
        GenericApplicationContext ctx = (GenericApplicationContext) applicationContext;
        ctx.registerBeanDefinition(AnnotationConfigUtils.AUTOWIRED_ANNOTATION_PROCESSOR_BEAN_NAME,
                BeanDefinitionBuilder
                .rootBeanDefinition(AutowiredAnnotationBeanPostProcessor.class)
                .addPropertyValue("requiredParameterValue", false)
                .getBeanDefinition());
    }
}
