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
import com.acuity.va.auditlogger.domain.LogArgEntity;
import com.acuity.va.auditlogger.domain.LogOperationEntity;
import com.acuity.va.security.acl.domain.AcuitySidDetails;
import com.jayway.restassured.RestAssured;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.jayway.restassured.RestAssured.given;
import static java.util.stream.Collectors.toList;

@RunWith(SpringJUnit4ClassRunner.class)
@FullStackITSpringApplicationConfiguration
public class SessionResourceAuditLoggingFullStackIT {

    @Value("${local.server.port}")
    private int port;

    @Autowired
    private AuditLoggerRepository auditLoggerRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Rule
    public JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Before
    public void setUp() throws Exception {
        RestAssured.port = port;
    }

    @Test
    public void shouldLogWhoAmiRestCall() {

        int beforeLogOps = auditLoggerRepository.countAllLogOperations();
        int beforeLogArgs = auditLoggerRepository.countAllLogArgs();

        // Call the service
        AcuitySidDetails sidDetails = given().
                log().all().
                when().
                header("Content-Type", MediaType.APPLICATION_JSON_VALUE).
                get("/resources/session/whoami").
                then().
                log().all().
                statusCode(200).
                extract().as(AcuitySidDetails.class);

        // Then
        int afterLogOps = auditLoggerRepository.countAllLogOperations();
        int afterLogArgs = auditLoggerRepository.countAllLogArgs();

        softly.assertThat(afterLogOps - beforeLogOps).isEqualTo(1);
        softly.assertThat(afterLogArgs - beforeLogArgs).isEqualTo(3);

        Optional<LogOperationEntity> lastLogOpOpt = auditLoggerRepository
                .getAllLogOperations()
                .stream()
                .max((o1, o2) -> o1.getDateCreated().compareTo(o2.getDateCreated()));

        softly.assertThat(lastLogOpOpt.isPresent()).isEqualTo(true);

        LogOperationEntity lastLogOp = lastLogOpOpt.orElse(null);

        softly.assertThat(lastLogOp.getName()).isEqualTo("DETECT_LOGON");
        softly.assertThat(lastLogOp.getOwner()).isNotNull();

        List<LogArgEntity> last3 = auditLoggerRepository
                .getAllLogArgs()
                .stream()
                .sorted(Comparator.comparingLong(LogArgEntity::getId).reversed())
                .limit(3)
                .collect(toList());

        softly.assertThat(last3).extracting("name").containsOnly("PRID", "NAME", "AUTHORITIES");
        softly.assertThat(last3).extracting("value").contains(sidDetails.getAuthoritiesAsStringToString());
    }

    @Test
    public void shouldPing() {

        // Call the service
        given().
                log().all().
                when().
                header("Content-Type", MediaType.APPLICATION_JSON_VALUE).
                get("/resources/session/ping").
                then().
                log().all().
                statusCode(200);
    }

    @Test
    public void shouldAddToView() {
        int beforeLogView = countLoggedUsersView();
        // Call the service
        AcuitySidDetails sidDetails = given().
                log().all().
                when().
                header("Content-Type", MediaType.APPLICATION_JSON_VALUE).
                get("/resources/session/whoami").
                then().
                log().all().
                statusCode(200).
                extract().as(AcuitySidDetails.class);

        int afterLogView = countLoggedUsersView();
        softly.assertThat(afterLogView - beforeLogView).isEqualTo(1);
        Map<String, Object> newEntry = selectFromUserLoggingView(sidDetails.getFullName());
        softly.assertThat(newEntry.get("SESSION_ID")).isNotNull();
        softly.assertThat(newEntry.get("USER_ID")).isNotNull();
        softly.assertThat(newEntry.get("AUTHORITIES").toString()).isEqualTo(sidDetails.getAuthoritiesAsStringToString());
        softly.assertThat(newEntry.get("DEVELOPER").toString()).isEqualTo("1");
    }

    private Map<String, Object> selectFromUserLoggingView(String username) {
        return jdbcTemplate.queryForList("SELECT * FROM VIEW_LOGGING_USER_SESSIONS WHERE USERNAME = ?", username).get(0);
    }

    private int countLoggedUsersView() {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) AS count FROM VIEW_LOGGING_USER_SESSIONS", Integer.class);
    }
}
