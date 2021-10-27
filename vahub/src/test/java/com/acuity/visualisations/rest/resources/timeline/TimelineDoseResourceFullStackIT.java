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

package com.acuity.visualisations.rest.resources.timeline;

import com.acuity.visualisations.common.vo.DayZeroType;
import com.acuity.visualisations.rawdatamodel.axes.TAxes;
import com.acuity.visualisations.rawdatamodel.filters.DrugDoseFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.filters.SetFilter;
import com.acuity.visualisations.rawdatamodel.vo.timeline.dose.MaxDoseType;
import com.acuity.visualisations.rawdatamodel.vo.timeline.dose.SubjectDrugDosingSummary;
import com.acuity.visualisations.rest.model.request.dose.TimelineDosingRequest;
import com.acuity.visualisations.rest.test.annotation.FullStackITSpringApplicationConfiguration;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.restassured.RestAssured;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collections;
import java.util.List;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_DATASETS;
import static com.jayway.restassured.RestAssured.given;

@RunWith(SpringJUnit4ClassRunner.class)
@FullStackITSpringApplicationConfiguration
@ActiveProfiles(profiles = {"NoScheduledJobs", "it", "local-no-security", "fullStack"})
public class TimelineDoseResourceFullStackIT {

    @Value("${local.server.port}")
    private int port;

    private static ObjectMapper mapper;

    @Before
    public void setUp() throws Exception {
        RestAssured.port = port;

        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Rule
    public JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test
    public void shouldGetTrellisingOptionAsActualValue() throws Exception {
        PopulationFilters populationFilters = new PopulationFilters();
        populationFilters.setSubjectId(new SetFilter<>(Collections.singletonList("SUBJECT0004")));

        TimelineDosingRequest timelineDosingRequest = new TimelineDosingRequest();
        timelineDosingRequest.setDayZero(new TAxes<>(DayZeroType.DAYS_SINCE_FIRST_DOSE));
        timelineDosingRequest.setPopulationFilters(populationFilters);
        timelineDosingRequest.setDoseFilters(new DrugDoseFilters());
        timelineDosingRequest.setMaxDoseType(MaxDoseType.PER_SUBJECT);
        timelineDosingRequest.setDatasets(DUMMY_ACUITY_DATASETS.getDatasetsList());

        String url = "/resources/timeline/dosing/dose-summaries-by-drug";

        String jsonResponse = given()
                .contentType("application/json")
                .body(mapper.writeValueAsString(timelineDosingRequest))
                .when()
                .post(url)
                .asString();
        System.out.println(jsonResponse);

        List<SubjectDrugDosingSummary> returnedResult = mapper.readValue(jsonResponse, new TypeReference<List<SubjectDrugDosingSummary>>() {
        });

        softly.assertThat(returnedResult).hasSize(1);
        softly.assertThat(returnedResult.get(0).getDrugs()).hasSize(1);
    }
}
