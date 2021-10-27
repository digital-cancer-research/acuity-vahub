package com.acuity.visualisations.rest.resources.sae;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_DETECT_DATASETS;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasValue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.filters.SeriousAeFilters;
import com.acuity.visualisations.rawdatamodel.service.event.SeriousAeService;
import com.acuity.visualisations.rest.model.request.sae.SeriousAeRequest;
import com.acuity.visualisations.rest.model.response.DetailsOnDemandResponse;
import com.acuity.visualisations.rest.model.request.SingleSubjectRequest;
import com.acuity.va.security.acl.domain.Datasets;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = SeriousAeResource.class, secure = false)
public class SeriousAeResourceTest {
    private static final String BASE_URL = "/resources/sae";

    @Autowired
    private MockMvc mvc;

    @MockBean
    private SeriousAeService seriousAeService;

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void shouldGetFilters() throws Exception {
        SeriousAeRequest request = new SeriousAeRequest();
        request.setSeriousAeFilters(SeriousAeFilters.empty());
        request.setPopulationFilters(PopulationFilters.empty());
        request.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());


        when(seriousAeService.getAvailableFilters(any(Datasets.class),
                any(SeriousAeFilters.class),
                any(PopulationFilters.class)
        )).thenReturn(SeriousAeFilters.empty());

        mvc.perform(post(BASE_URL + "/filters")
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldGetSingleSubjectData() throws Exception {
        SingleSubjectRequest<SeriousAeFilters> requestBody = new SingleSubjectRequest();
        requestBody.setEventFilters(SeriousAeFilters.empty());
        requestBody.setSubjectId("subject1");
        requestBody.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());

        Map<String, String> dodMap = new HashMap<>();
        dodMap.put("column_1", "value_1");
        dodMap.put("column_2", "value_2");

        DetailsOnDemandResponse responseToReturn = new DetailsOnDemandResponse(Arrays.asList(dodMap));

        when(seriousAeService.getDetailsOnDemandData(any(Datasets.class), anyString(), any(SeriousAeFilters.class)))
                .thenReturn(responseToReturn.getDodData());

        mvc.perform(post(BASE_URL + "/single-subject").
                content(mapper.writeValueAsString(requestBody)).
                contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dodData[0]", hasKey("column_2")))
                .andExpect(jsonPath("$.dodData[0]", hasValue("value_2")))
                .andExpect(jsonPath("$.dodData[0]", hasKey("column_1")))
                .andExpect(jsonPath("$.dodData[0]", hasValue("value_1")))
                .andReturn();
    }
}
