package com.acuity.visualisations.rest.resources;

import com.acuity.visualisations.rest.config.branding.BrandingProperties;
import com.acuity.visualisations.rest.config.properties.IntegrationProperties;
import com.acuity.visualisations.rest.config.properties.LicensingProperties;
import com.acuity.visualisations.rest.model.response.ConfigurationResponse;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.regex.Pattern;

import static org.springframework.http.MediaType.ALL_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@Api(value = "/resources/configuration/", description = "rest endpoints for configuration settings")
@RequestMapping(value = "/resources/configuration/", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
public class ConfigurationResource {

    @Autowired
    private BrandingProperties brandingProperties;

    @Autowired
    private IntegrationProperties integrationProperties;

    @Autowired
    private LicensingProperties licensingProperties;

    @Autowired
    private BuildProperties buildProperties;

    private static final Pattern SHORT_VERSION_EXTRACTOR = Pattern.compile("^([\\d.]+).*$");

    @RequestMapping(value = "properties", consumes = {APPLICATION_JSON_VALUE, ALL_VALUE})
    public ConfigurationResponse getProperties() {
        return ConfigurationResponse.builder()
                .branding(brandingProperties)
                .cbioportalUrl(integrationProperties.getCbioportal().get("url"))
                .qnaMaker(integrationProperties.getQnaMaker())
                .agGridKey(licensingProperties.getAgGridKey())
                .build();
    }
}
