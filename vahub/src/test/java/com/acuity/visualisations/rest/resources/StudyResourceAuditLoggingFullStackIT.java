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

import com.acuity.visualisations.rest.test.annotation.FullStackITSpringApplicationConfiguration;
import com.acuity.va.auditlogger.dao.AuditLoggerRepository;
import com.acuity.va.auditlogger.domain.LogOperationEntity;
import com.acuity.va.security.acl.domain.DatasetsRequest;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Iterables;
import com.jayway.restassured.RestAssured;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_DETECT_DATASETS;
import static com.jayway.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@FullStackITSpringApplicationConfiguration
public class StudyResourceAuditLoggingFullStackIT {

    @Value("${local.server.port}")
    private int port;

    private static ObjectMapper mapper;

    @Autowired
    private AuditLoggerRepository auditLoggerRepository;

    @Before
    public void setUp() throws Exception {
        RestAssured.port = port;

        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Test
    @Ignore
    public void shouldLogGetMetadataInfoRestCall() throws Exception {

        int beforeLogOps = auditLoggerRepository.countAllLogOperations();
        int beforeLogArgs = auditLoggerRepository.countAllLogArgs();

        DatasetsRequest datasetsRequest = new DatasetsRequest();
        datasetsRequest.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());

        // Call the service
        given().
                log().all().
                contentType("application/json").
                body(mapper.writeValueAsString(datasetsRequest)).
                when().
                post("/resources/study/info").
                then().
                log().all().
                statusCode(200);

        // Then
        int afterLogOps = auditLoggerRepository.countAllLogOperations();
        int afterLogArgs = auditLoggerRepository.countAllLogArgs();

        assertThat(afterLogOps - beforeLogOps).isEqualTo(1);
        assertThat(afterLogArgs - beforeLogArgs).isEqualTo(0);

        LogOperationEntity lastLogOp = Iterables.getLast(auditLoggerRepository.getAllLogOperations());

        assertThat(lastLogOp.getName()).isEqualTo("DETECT_LOGON");
        assertThat(lastLogOp.getOwner()).isNotNull();
    }
}
