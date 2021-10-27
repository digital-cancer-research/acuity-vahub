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

package com.acuity.visualisations.rest.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "integration")
public class IntegrationProperties {

    private Map<String, String> cbioportal = new LinkedHashMap<>();
    private Map<String, String> aiAndMlServices = new LinkedHashMap<>();
    private Map<String, String> omicsServices = new LinkedHashMap<>();
    private Map<String, String> patientServices = new LinkedHashMap<>();
    private QnaMakerProperties qnaMaker = null;

    @Data
    public static class QnaMakerProperties {
        private String url;
        private Map<String, String> headers;
    }
}
