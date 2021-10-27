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

package com.acuity.visualisations.rest.config.branding;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "branding")
public class BrandingProperties {

    private Map<String, String> about = new LinkedHashMap<>();
    private Map<String, String> support = new LinkedHashMap<>();
    private Map<String, String> integration = new LinkedHashMap<>();

    private List<SuperUser> superUsers = new ArrayList<>();

    private Map<String, String> staticContent = new LinkedHashMap<>();
    private Map<String, String> brandingColors = new LinkedHashMap<>();
    private ExtendedOptions extendedOptions;
    private String pathToImages;

    @Data
    public static class ExtendedOptions {
        private String aiAndMlServicesUrl;
        private String omicsServicesUrl;
        private String patientServicesUrl;
    }
}
