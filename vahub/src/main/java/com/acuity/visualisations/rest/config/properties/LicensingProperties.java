package com.acuity.visualisations.rest.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "licensing")
public class LicensingProperties {
    private String agGridKey = null;
}
