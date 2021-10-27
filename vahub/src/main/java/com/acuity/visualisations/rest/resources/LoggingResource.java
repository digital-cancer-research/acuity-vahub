package com.acuity.visualisations.rest.resources;

import com.acuity.visualisations.rest.model.request.logging.LoggingRequest;
import com.acuity.va.auditlogger.annotation.LogArg;
import com.acuity.va.auditlogger.annotation.LogOperation;
import com.acuity.visualisations.rest.util.Constants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping(value = "/resources/logging")
@PreAuthorize(Constants.PRE_AUTHORISE_VISUALISATION)
@Api(value = "/resources/logging", description = "rest endpoints for for logging")
@Slf4j
public class LoggingResource {

    @ApiOperation(
            value = "Log View Change",
            nickname = "logViewChange",
            httpMethod = "POST"
    )
    @RequestMapping(value = "/viewchange", method = POST)
    @LogOperation(name = "VIEWCHANGE", value = {
            @LogArg(arg = 0, name = "DATASETS", expression = "getDatasetsObject().getDatasetsLoggingObject()", isDatasetsLoggingObject = true),
            @LogArg(arg = 0, name = "ANALYTICS_SESSION_ID", expression = "getAnalyticsSessionId()"),
            @LogArg(arg = 0, name = "ANALYTICS_SESSION_DATE", expression = "getAnalyticsSessionDate()"),
            @LogArg(arg = 0, name = "VIEW_NAME", expression = "getViewName()"),
            @LogArg(arg = 0, name = "VISUALISATION_NAME", expression = "getVisualisationName()")})
    public void logViewChange(@ApiParam(value = "LoggingRequest}", required = true)
                              @RequestBody @Valid LoggingRequest requestBody) {

        log.debug("logViewChange: {}", requestBody);
    }
}
