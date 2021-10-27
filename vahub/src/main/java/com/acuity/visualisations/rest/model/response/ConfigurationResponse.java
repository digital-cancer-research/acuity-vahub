package com.acuity.visualisations.rest.model.response;

import com.acuity.visualisations.rest.config.branding.BrandingProperties;
import com.acuity.visualisations.rest.config.properties.IntegrationProperties;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConfigurationResponse {
    private BrandingProperties branding;
    private String cbioportalUrl;
    private IntegrationProperties.QnaMakerProperties qnaMaker;
    private String agGridKey;
}
