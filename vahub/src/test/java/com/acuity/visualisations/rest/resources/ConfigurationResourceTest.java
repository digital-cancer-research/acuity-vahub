package com.acuity.visualisations.rest.resources;

import com.acuity.visualisations.rest.config.properties.IntegrationProperties;
import com.acuity.visualisations.rest.config.properties.LicensingProperties;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.info.BuildProperties;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

public class ConfigurationResourceTest {

    @Mock
    private IntegrationProperties integrationProperties;

    @SuppressWarnings("unused") // this mock is injected and used in the ConfigurationResource instance, so we need it
    @Mock
    private LicensingProperties licensingProperties;
    @Mock
    private BuildProperties buildProperties;
    
    @InjectMocks
    private ConfigurationResource configurationResource;

    private static final String FULL_ACUITY_VERSION = "10010.10.1-SNAPSHOT-unobtainium";

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        Mockito.when(buildProperties.getVersion()).thenReturn(FULL_ACUITY_VERSION);
        Mockito.when(integrationProperties.getCbioportal()).thenReturn(new HashMap<>());
    }
}
