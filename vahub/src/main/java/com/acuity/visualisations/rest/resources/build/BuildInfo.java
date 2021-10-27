package com.acuity.visualisations.rest.resources.build;

import com.acuity.visualisations.rest.config.properties.BuildInfoProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequiredArgsConstructor
public class BuildInfo {

    private final BuildInfoProperties buildInfoProperties;

    @RequestMapping(value = "/build-info", produces = APPLICATION_JSON_VALUE)
    @PreAuthorize("@environment.getProperty('spring.profiles.active').contains('local-no-security') or hasRole('DEVELOPMENT_TEAM')")
    public BuildInfoProperties getBuildInfo() {
        return buildInfoProperties;
    }

}
