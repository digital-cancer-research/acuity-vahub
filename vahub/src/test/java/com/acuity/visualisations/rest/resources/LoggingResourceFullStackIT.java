/*
 * Copyright 2021 The University of Manchester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.acuity.visualisations.rest.resources;

import com.acuity.visualisations.rest.model.request.logging.LoggingRequest;
import com.acuity.visualisations.rest.test.annotation.FullStackITSpringApplicationConfiguration;
import com.acuity.va.auditlogger.dao.AuditLoggerRepository;
import com.acuity.va.auditlogger.domain.LogArgEntity;
import com.acuity.va.auditlogger.domain.LogOperationEntity;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.restassured.RestAssured;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_DATASETS;
import static com.acuity.visualisations.config.util.TestConstants.MULTI_DUMMY_ACUITY_DATASETS;
import static com.jayway.restassured.RestAssured.given;
import static java.util.stream.Collectors.toList;

@RunWith(SpringJUnit4ClassRunner.class)
@FullStackITSpringApplicationConfiguration
public class LoggingResourceFullStackIT {

    private static ObjectMapper mapper;

    @Value("${local.server.port}")
    private int port;

    @Autowired
    private AuditLoggerRepository auditLoggerRepository;

    @Rule
    public JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Before
    public void setUp() throws Exception {
        RestAssured.port = port;

        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Test
    public void shouldLogViewChange() throws Exception {

        int beforeLogOps = auditLoggerRepository.countAllLogOperations();
        int beforeLogArgs = auditLoggerRepository.countAllLogArgs();

        LoggingRequest loggingRequest = new LoggingRequest();

        loggingRequest.setDatasets(DUMMY_ACUITY_DATASETS.getDatasetsList());
        loggingRequest.setAnalyticsSessionId(UUID.randomUUID().toString());
        loggingRequest.setAnalyticsSessionDate(new Date());
        loggingRequest.setViewName("test_view");
        loggingRequest.setVisualisationName("test_vis");

        // Call the service
        given().
                log().all().
                when().
                header("Content-Type", MediaType.APPLICATION_JSON_VALUE).
                body(mapper.writeValueAsString(loggingRequest)).
                post("/resources/logging/viewchange").
                then().
                log().all().
                statusCode(200);

        // Then
        int afterLogOps = auditLoggerRepository.countAllLogOperations();
        int afterLogArgs = auditLoggerRepository.countAllLogArgs();

        softly.assertThat(afterLogOps - beforeLogOps).isEqualTo(1);
        softly.assertThat(afterLogArgs - beforeLogArgs).isEqualTo(6);

        Optional<LogOperationEntity> lastLogOpOpt = auditLoggerRepository
                .getAllLogOperations()
                .stream()
                .max((o1, o2) -> o1.getDateCreated().compareTo(o2.getDateCreated()));

        softly.assertThat(lastLogOpOpt.isPresent()).isEqualTo(true);

        LogOperationEntity lastLogOp = lastLogOpOpt.orElse(null);

        softly.assertThat(lastLogOp.getName()).isEqualTo("VIEWCHANGE");
        softly.assertThat(lastLogOp.getOwner()).isNotNull();

        List<LogArgEntity> last6 = auditLoggerRepository
                .getAllLogArgs()
                .stream()
                .sorted(Comparator.comparingLong(LogArgEntity::getId).reversed())
                .limit(6)
                .collect(toList());

        softly.assertThat(last6).extracting("name").containsExactlyInAnyOrder(
                "MERGED_IDS",
                "IS_MERGED",
                "VISUALISATION_NAME",
                "VIEW_NAME",
                "ANALYTICS_SESSION_DATE",
                "ANALYTICS_SESSION_ID");
        softly.assertThat(last6).extracting("value").containsExactlyInAnyOrder(
                loggingRequest.getDatasetsObject().getIdsList().stream().findFirst().orElse(0L).toString(),
                1L,
                "test_vis",
                "test_view",
                loggingRequest.getAnalyticsSessionDate(),
                loggingRequest.getAnalyticsSessionId());
    }
    @Test
    public void shouldLogViewChangeMerged() throws Exception {

        int beforeLogOps = auditLoggerRepository.countAllLogOperations();
        int beforeLogArgs = auditLoggerRepository.countAllLogArgs();

        LoggingRequest loggingRequest = new LoggingRequest();

        loggingRequest.setDatasets(MULTI_DUMMY_ACUITY_DATASETS.getDatasetsList());
        loggingRequest.setAnalyticsSessionId(UUID.randomUUID().toString());
        loggingRequest.setAnalyticsSessionDate(new Date());
        loggingRequest.setViewName("test_view");
        loggingRequest.setVisualisationName("test_vis");

        // Call the service
        given().
                log().all().
                when().
                header("Content-Type", MediaType.APPLICATION_JSON_VALUE).
                body(mapper.writeValueAsString(loggingRequest)).
                post("/resources/logging/viewchange").
                then().
                log().all().
                statusCode(200);

        // Then
        int afterLogOps = auditLoggerRepository.countAllLogOperations();
        int afterLogArgs = auditLoggerRepository.countAllLogArgs();

        softly.assertThat(afterLogOps - beforeLogOps).isEqualTo(2);
        softly.assertThat(afterLogArgs - beforeLogArgs).isEqualTo(12);
    }

}
