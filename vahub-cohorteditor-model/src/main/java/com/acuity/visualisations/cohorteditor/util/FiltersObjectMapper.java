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

package com.acuity.visualisations.cohorteditor.util;

import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.fasterxml.jackson.databind.DeserializationFeature;
import java.io.StringWriter;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author ksnd199
 */
@Slf4j
public final class FiltersObjectMapper {

    private static ObjectMapper mapper;

    private FiltersObjectMapper() {
    }

    static {
        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static String toString(Filters filters) {
        try {
            StringWriter writer = new StringWriter();
            mapper.writeValue(writer, filters);

            return writer.toString();
        } catch (Exception ex) {
            log.error("Unable to convert filters to string for {}", filters, ex);
            return null;
        }
    }

    public static Filters fromString(String json, Class filterClazz) {
        try {
            return (Filters) mapper.readValue(json, filterClazz);
        } catch (Exception ex) {
            log.error("Unable to convert json to filters for class {} from {}", filterClazz, json, ex);
            return null;
        }
    }
}
