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

package com.acuity.visualisations.rest.util;

import com.acuity.visualisations.rawdatamodel.util.DaysUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.filter.HiddenHttpMethodFilter;

import javax.servlet.Filter;
import java.text.SimpleDateFormat;

/**
 *
 * @author ksnd199
 */
@Configuration
public class SpringMVCConfig {

    @Bean
    public Jackson2ObjectMapperBuilder jacksonBuilder() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DaysUtil.JSON_TIMESTAMP_FORMAT);

        Jackson2ObjectMapperBuilder b = new Jackson2ObjectMapperBuilder();
        b.failOnUnknownProperties(false).indentOutput(true).dateFormat(dateFormat);
        return b;
    }

    @Bean
    public Filter hiddenHttpMethodFilter() {
        return new HiddenHttpMethodFilter();
    }
}
