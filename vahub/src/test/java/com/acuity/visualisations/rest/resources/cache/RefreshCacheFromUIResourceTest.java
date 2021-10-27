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

package com.acuity.visualisations.rest.resources.cache;

import com.acuity.visualisations.common.cache.ClearCacheStatus;
import com.acuity.visualisations.common.cache.RefreshCacheService;
import com.acuity.visualisations.common.study.metadata.InstanceMetadataService;
import com.acuity.visualisations.rawdatamodel.dataset.info.InfoService;
import com.acuity.visualisations.rest.test.config.DisableAutowireRequiredInitializer;
import com.google.gson.Gson;
import com.jayway.jsonpath.JsonPath;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
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
public class RefreshCacheFromUIResourceTest {

    @Mock
    private RefreshCacheService mockRefreshCacheService;
    @Mock
    private InstanceMetadataService mockInstanceMetadataService;
    @Mock
    private InfoService mockInfoService;
    @InjectMocks
    private RefreshCacheFromUIResource refreshCacheFromUIResource;

    private MockMvc mvc;
    private Gson gson = new Gson();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mvc = MockMvcBuilders.standaloneSetup(refreshCacheFromUIResource).build();
    }

    @Test
    public void shouldClearAllCache() throws Exception {
        ClearCacheStatus clearCacheStatus = new ClearCacheStatus();
        clearCacheStatus.getClearedCacheNames().add("Cleared cache 1");
        clearCacheStatus.getRetainedCacheNames().add("Retained cache 1");

        when(mockRefreshCacheService.tryLock(any())).thenReturn(new ResponseEntity(clearCacheStatus, HttpStatus.OK));

        MockHttpServletRequestBuilder get = MockMvcRequestBuilders.get("/resources/cache/clear/all").contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(get).andExpect(status().isOk()).andDo(print()).andReturn();

        String json = result.getResponse().getContentAsString();
        List<String> clearedCacheNames = JsonPath.read(json, "$.clearedCacheNames");
        List<String> retainedCacheNames = JsonPath.read(json, "$.retainedCacheNames");

        assertThat(clearedCacheNames.get(0)).isEqualTo("Cleared cache 1");
        assertThat(retainedCacheNames.get(0)).isEqualTo("Retained cache 1");

        verify(mockRefreshCacheService, times(1)).tryLock(any());
        verifyNoMoreInteractions(mockRefreshCacheService);
    }
}
