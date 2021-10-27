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
import com.acuity.va.security.acl.domain.Datasets;
import com.acuity.va.security.acl.domain.DetectDataset;
import com.acuity.va.security.acl.domain.AcuityDataset;
import com.acuity.va.security.acl.domain.AcuityObjectIdentity;
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

import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
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
public class RefreshCacheResourceTest {

    @Mock
    private RefreshCacheService mockRefreshCacheService;
    @Mock
    private InstanceMetadataService mockInstanceMetadataService;
    @Mock
    private InfoService mockInfoService;
    @InjectMocks
    private RefreshCacheResource refreshCacheResource;

    private MockMvc mvc;
    private Gson gson = new Gson();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mvc = MockMvcBuilders.standaloneSetup(refreshCacheResource).build();
    }

    @Test
    public void shouldClearCacheForDataset() throws Exception {

        ClearCacheStatus clearCacheStatus = new ClearCacheStatus();
        clearCacheStatus.getClearedCacheNames().add("Cleared cache 1");
        clearCacheStatus.getRetainedCacheNames().add("Retained cache 1");

        when(mockRefreshCacheService.tryLock(any())).thenReturn(new ResponseEntity(clearCacheStatus, HttpStatus.OK));

        MockHttpServletRequestBuilder get = MockMvcRequestBuilders.get("/resources/security/clear/detect/100").contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(get).andExpect(status().isOk()).andDo(print()).andReturn();

        String json = result.getResponse().getContentAsString();
        List<String> clearedCacheNames = JsonPath.read(json, "$.clearedCacheNames");
        List<String> retainedCacheNames = JsonPath.read(json, "$.retainedCacheNames");

        assertThat(clearedCacheNames.get(0)).isEqualTo("Cleared cache 1");
        assertThat(retainedCacheNames.get(0)).isEqualTo("Retained cache 1");

        verify(mockRefreshCacheService, times(1)).tryLock(any());
        verifyNoMoreInteractions(mockRefreshCacheService);
    }

    @Test
    public void shouldClearCacheForDatasetAcuity() throws Exception {

        ClearCacheStatus clearCacheStatus = new ClearCacheStatus();
        clearCacheStatus.getClearedCacheNames().add("Cleared cache 1");
        clearCacheStatus.getRetainedCacheNames().add("Retained cache 1");

        when(mockRefreshCacheService.tryLock(any())).thenReturn(new ResponseEntity(clearCacheStatus, HttpStatus.OK));

        MockHttpServletRequestBuilder get = MockMvcRequestBuilders.get("/resources/security/clear/acuity/100").contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(get).andExpect(status().isOk()).andDo(print()).andReturn();

        String json = result.getResponse().getContentAsString();
        List<String> clearedCacheNames = JsonPath.read(json, "$.clearedCacheNames");
        List<String> retainedCacheNames = JsonPath.read(json, "$.retainedCacheNames");

        assertThat(clearedCacheNames.get(0)).isEqualTo("Cleared cache 1");
        assertThat(retainedCacheNames.get(0)).isEqualTo("Retained cache 1");

        verify(mockRefreshCacheService, times(1)).tryLock(any());
        verifyNoMoreInteractions(mockRefreshCacheService);
    }

    @Test
    public void shouldRefreshAllCache() throws Exception {
        ClearCacheStatus clearCacheStatus = new ClearCacheStatus();
        clearCacheStatus.getClearedCacheNames().add("Cleared cache 1");
        clearCacheStatus.getRetainedCacheNames().add("Retained cache 1");

        when(mockRefreshCacheService.tryLock(any())).thenReturn(new ResponseEntity(clearCacheStatus, HttpStatus.OK));

        MockHttpServletRequestBuilder get = MockMvcRequestBuilders.get("/resources/security/refresh/all").contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(get).andExpect(status().isOk()).andDo(print()).andReturn();

        String json = result.getResponse().getContentAsString();
        List<String> clearedCacheNames = JsonPath.read(json, "$.clearedCacheNames");
        List<String> retainedCacheNames = JsonPath.read(json, "$.retainedCacheNames");

        assertThat(clearedCacheNames.get(0)).isEqualTo("Cleared cache 1");
        assertThat(retainedCacheNames.get(0)).isEqualTo("Retained cache 1");

        verify(mockRefreshCacheService, times(1)).tryLock(any());
        verifyNoMoreInteractions(mockRefreshCacheService);
    }

    @Test
    public void shouldRefreshDetect() throws Exception {
        ClearCacheStatus clearCacheStatus = new ClearCacheStatus();
        clearCacheStatus.getClearedCacheNames().add("Cleared cache 1");
        clearCacheStatus.getRetainedCacheNames().add("Retained cache 1");

        when(mockRefreshCacheService.tryLock(any())).thenReturn(new ResponseEntity(clearCacheStatus, HttpStatus.OK));

        MockHttpServletRequestBuilder get = MockMvcRequestBuilders.get("/resources/security/refresh/detect").contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(get).andExpect(status().isOk()).andDo(print()).andReturn();

        String json = result.getResponse().getContentAsString();
        List<String> clearedCacheNames = JsonPath.read(json, "$.clearedCacheNames");
        List<String> retainedCacheNames = JsonPath.read(json, "$.retainedCacheNames");

        assertThat(clearedCacheNames.get(0)).isEqualTo("Cleared cache 1");
        assertThat(retainedCacheNames.get(0)).isEqualTo("Retained cache 1");

        verify(mockRefreshCacheService, times(1)).tryLock(any());
        verifyNoMoreInteractions(mockRefreshCacheService);
    }

    @Test
    public void shouldRefreshAcuity() throws Exception {
        ClearCacheStatus clearCacheStatus = new ClearCacheStatus();
        clearCacheStatus.getClearedCacheNames().add("Cleared cache 1");
        clearCacheStatus.getRetainedCacheNames().add("Retained cache 1");

        when(mockRefreshCacheService.tryLock(any())).thenReturn(new ResponseEntity(clearCacheStatus, HttpStatus.OK));

        MockHttpServletRequestBuilder get = MockMvcRequestBuilders.get("/resources/security/refresh/acuity").contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(get).andExpect(status().isOk()).andDo(print()).andReturn();

        String json = result.getResponse().getContentAsString();
        List<String> clearedCacheNames = JsonPath.read(json, "$.clearedCacheNames");
        List<String> retainedCacheNames = JsonPath.read(json, "$.retainedCacheNames");

        assertThat(clearedCacheNames.get(0)).isEqualTo("Cleared cache 1");
        assertThat(retainedCacheNames.get(0)).isEqualTo("Retained cache 1");

        verify(mockRefreshCacheService, times(1)).tryLock(any());
        verifyNoMoreInteractions(mockRefreshCacheService);
    }

    @Test
    public void shouldReloadAcuityDataset() throws Exception {
        ClearCacheStatus clearCacheStatus = new ClearCacheStatus();
        clearCacheStatus.getClearedCacheNames().add("Cleared cache 1");
        clearCacheStatus.getRetainedCacheNames().add("Retained cache 1");

        when(mockRefreshCacheService.run(any())).thenReturn(new ResponseEntity(clearCacheStatus, HttpStatus.OK));

        MockHttpServletRequestBuilder get = MockMvcRequestBuilders.get("/resources/security/reload/acuity/100").contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(get).andExpect(status().isOk()).andDo(print()).andReturn();

        String json = result.getResponse().getContentAsString();
        List<String> clearedCacheNames = JsonPath.read(json, "$.clearedCacheNames");
        List<String> retainedCacheNames = JsonPath.read(json, "$.retainedCacheNames");

        assertThat(clearedCacheNames.get(0)).isEqualTo("Cleared cache 1");
        assertThat(retainedCacheNames.get(0)).isEqualTo("Retained cache 1");

        verify(mockRefreshCacheService, times(1)).run(any());
        verifyNoMoreInteractions(mockRefreshCacheService);
    }

    @Test
    public void shouldReloadDetectDataset() throws Exception {
        ClearCacheStatus clearCacheStatus = new ClearCacheStatus();
        clearCacheStatus.getClearedCacheNames().add("Cleared cache 1");
        clearCacheStatus.getRetainedCacheNames().add("Retained cache 1");

        when(mockRefreshCacheService.run(any())).thenReturn(new ResponseEntity(clearCacheStatus, HttpStatus.OK));

        MockHttpServletRequestBuilder get = MockMvcRequestBuilders.get("/resources/security/reload/detect/100").contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(get).andExpect(status().isOk()).andDo(print()).andReturn();

        String json = result.getResponse().getContentAsString();
        List<String> clearedCacheNames = JsonPath.read(json, "$.clearedCacheNames");
        List<String> retainedCacheNames = JsonPath.read(json, "$.retainedCacheNames");

        assertThat(clearedCacheNames.get(0)).isEqualTo("Cleared cache 1");
        assertThat(retainedCacheNames.get(0)).isEqualTo("Retained cache 1");

        verify(mockRefreshCacheService, times(1)).run(any());
        verifyNoMoreInteractions(mockRefreshCacheService);
    }

    @Test
    public void shouldListPrimedCaches() throws Exception {

        ArrayList<Datasets> datasets = newArrayList(Datasets.toDetectDataset(1), Datasets.toDetectDataset(2));

        when(mockRefreshCacheService.listPrimedCachedDatasets()).thenReturn(datasets);

        MockHttpServletRequestBuilder get = MockMvcRequestBuilders.get("/resources/security/primedcaches").contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(get).andExpect(status().isOk()).andDo(print()).andReturn();

        String json = result.getResponse().getContentAsString();

        Integer id1 = JsonPath.read(json, "$.[0].id");
        Integer id2 = JsonPath.read(json, "$.[1].id");

        assertThat(id1).isEqualTo(1);
        assertThat(id2).isEqualTo(2);

        verify(mockRefreshCacheService, times(1)).listPrimedCachedDatasets();
        verifyNoMoreInteractions(mockRefreshCacheService);
    }

    @Test
    public void shouldListMissedPrimedCaches() throws Exception {

        ArrayList<Datasets> datasetsInCache = newArrayList(Datasets.toDetectDataset(1L), Datasets.toAcuityDataset(3L),
                Datasets.toDetectDataset(1L), Datasets.toDetectDataset(4L));
        ArrayList<AcuityObjectIdentity> allDatasets = newArrayList(new DetectDataset(1L), new AcuityDataset(2L), new AcuityDataset(3L));

        when(mockRefreshCacheService.listPrimedCachedDatasets()).thenReturn(datasetsInCache);
        when(mockInfoService.generateObjectIdentities()).thenReturn(allDatasets);

        MockHttpServletRequestBuilder get = MockMvcRequestBuilders.get("/resources/security/missedprimedcaches").contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(get).andExpect(status().isOk()).andDo(print()).andReturn();

        String json = result.getResponse().getContentAsString();

        Integer id1 = JsonPath.read(json, "$.[0].id");
        List<Object> objects = JsonPath.read(json, "$.[*]");

        assertThat(id1).isEqualTo(2);
        assertThat(objects).hasSize(1);

        verify(mockRefreshCacheService, times(1)).listPrimedCachedDatasets();
        verify(mockInfoService, times(1)).generateObjectIdentities();
        verifyNoMoreInteractions(mockRefreshCacheService);
    }
}
