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
