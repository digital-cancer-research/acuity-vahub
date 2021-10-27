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
