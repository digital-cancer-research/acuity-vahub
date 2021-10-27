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
