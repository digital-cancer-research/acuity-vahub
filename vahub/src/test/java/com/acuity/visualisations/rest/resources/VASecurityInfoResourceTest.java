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

import com.acuity.visualisations.rawdatamodel.dataset.info.InfoService;
import com.acuity.visualisations.rest.test.config.DisableAutowireRequiredInitializer;
import com.acuity.va.security.acl.domain.DetectDataset;
import com.acuity.va.security.acl.domain.DrugProgramme;
import com.acuity.va.security.acl.domain.AcuityObjectIdentity;
import com.google.gson.Gson;
import com.jayway.jsonpath.JsonPath;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        initializers = DisableAutowireRequiredInitializer.class,
        classes = {MockServletContext.class}
)
@WebAppConfiguration
public class VASecurityInfoResourceTest {

    @Mock
    private InfoService mockVASecurityInfoService;

    @InjectMocks
    private VASecurityInfoResource vASecurityInfoResource;

    private MockMvc mvc;
    private Gson gson = new Gson();

    private Long INSTANCE = 401234524L;
    private String SUBJECT_ID1 = INSTANCE + "-7916598662";
    private String SUBJECT_ID2 = INSTANCE + "-5379462502";

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mvc = MockMvcBuilders.standaloneSetup(vASecurityInfoResource).build();
    }

    @Test
    public void shouldListAllRois() throws Exception {

        ArrayList<AcuityObjectIdentity> resultList = newArrayList(new DrugProgramme(1L, "test"), new DetectDataset(2L, "Vis test"));

        when(mockVASecurityInfoService.generateObjectIdentities()).thenReturn(resultList);

        MockHttpServletRequestBuilder get = MockMvcRequestBuilders.
            get("/resources/security/info/rois").
            contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(get)
            .andExpect(status().isOk())
            .andDo(print()).andReturn();

        verify(mockVASecurityInfoService, times(1)).generateObjectIdentities();

        String json = result.getResponse().getContentAsString();

        System.out.println("JSON IS: " + json);
        Integer id1 = JsonPath.read(json, "$.[0].id");
        Integer id2 = JsonPath.read(json, "$.[1].id");
        String name1 = JsonPath.read(json, "$.[0].name");
        String name2 = JsonPath.read(json, "$.[1].name");

        assertThat(id1).isEqualTo(1);
        assertThat(id2).isEqualTo(2);
        assertThat(name1).isEqualTo("test");
        assertThat(name2).isEqualTo("Vis test");

        verifyNoMoreInteractions(mockVASecurityInfoService);
    }
}
