package com.acuity.visualisations.rest.resources;

import com.acuity.visualisations.common.util.Security;
import com.acuity.va.auditlogger.annotation.LogArg;
import com.acuity.va.auditlogger.annotation.LogOperation;
import com.acuity.va.security.acl.domain.AcuitySidDetails;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import static org.springframework.http.MediaType.ALL_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Created by ksnd199.
 */
@RestController
@RequestMapping(
        value = "/resources/session",
        consumes = {APPLICATION_JSON_VALUE, ALL_VALUE},
        produces = APPLICATION_JSON_VALUE
)
@Slf4j
@RequiredArgsConstructor
public class SessionResource {

    private final Security security;

    @ApiOperation("Gets the current user information")
    @GetMapping("/whoami")
    @LogOperation(name = "DETECT_LOGON", value = {
            @LogArg(arg = -1, name = "PRID", expression = "getUserId()"),
            @LogArg(arg = -1, name = "NAME", expression = "getFullName()"),
            @LogArg(arg = -1, name = "AUTHORITIES", expression = "getAuthoritiesAsStringToString()")})
    public AcuitySidDetails whoami() {
        try {
            AcuitySidDetails acuityUserDetails = security.getAcuityUserDetails();
            log.info("GET: Show security info. Principal = " + acuityUserDetails.getUsername());

            return acuityUserDetails;
        } catch (IllegalStateException ex) {
            throw new WebApplicationException("Unable to establish current user", ex, Response.Status.UNAUTHORIZED);
        }
    }

    @ApiOperation("Pings the server to keep session alive")
    @GetMapping("/ping")
    public String ping() {
        return "ping";
    }
}
