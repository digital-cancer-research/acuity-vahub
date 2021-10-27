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

package com.acuity.visualisations.rest.resources.cohorteditor;

import com.acuity.visualisations.cohorteditor.entity.SavedFilter;
import com.acuity.visualisations.cohorteditor.entity.SavedFilterInstance;
import com.acuity.visualisations.cohorteditor.repository.SavedFilterRepository;
import com.acuity.visualisations.cohorteditor.service.CohortSubjectService;
import com.acuity.visualisations.cohorteditor.service.CohortUsersService;
import com.acuity.visualisations.cohorteditor.service.SavedFilterService;
import com.acuity.visualisations.cohorteditor.util.FiltersObjectMapper;
import com.acuity.visualisations.cohorteditor.vo.SavedFilterVO;
import com.acuity.visualisations.cohorteditor.vo.UserVO;
import com.acuity.visualisations.rawdatamodel.filters.LabFilters;
import com.acuity.visualisations.rest.model.request.cohort.CohortDistinctSubjectsRequest;
import com.acuity.visualisations.rest.model.request.cohort.CohortEditorSaveFiltersRequest;
import com.acuity.visualisations.rest.test.config.DisableAutowireRequiredInitializer;
import com.acuity.va.security.acl.domain.Datasets;
import com.acuity.va.security.acl.domain.DatasetsRequest;
import com.acuity.va.security.acl.domain.DetectDataset;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.List;

import static com.acuity.visualisations.cohorteditor.entity.SavedFilterInstance.FilterTable.LABS;
import static com.acuity.visualisations.config.util.TestConstants.DUMMY_DETECT_DATASETS;
import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
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
@SuppressWarnings("unchecked")
public class CohortEditorResourceTest {

    @Mock
    private CohortSubjectService mockCohortSubjectService;
    @Mock
    private SavedFilterRepository mockSavedFilterRepository;
    @Mock
    private SavedFilterService mockSavedFilterService;
    @Mock
    private CohortUsersService mockCohortUsersService;

    @InjectMocks
    private CohortEditorResource cohortEditorResource;

    private MockMvc mvc;
    private static ObjectMapper mapper;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mvc = MockMvcBuilders.standaloneSetup(cohortEditorResource).build();

        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Test
    public void shouldGetsubjects() throws Exception {

        SavedFilter mockSavedFilter = mock(SavedFilter.class);
        when(mockSavedFilter.getId()).thenReturn(12L);
        when(mockSavedFilter.getFilters()).thenReturn(newArrayList());
        when(mockSavedFilter.getOperator()).thenReturn(SavedFilter.Operator.AND);

        CohortDistinctSubjectsRequest distinctSubjectsRequest = new CohortDistinctSubjectsRequest();
        distinctSubjectsRequest.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());
        distinctSubjectsRequest.setSavedFilterId(12L);

        List<String> subjects = newArrayList("subject1");

        when(mockSavedFilterService.getDistinctSubjects(any(Datasets.class), any(Long.class))).thenReturn(subjects);

        MockHttpServletRequestBuilder post = MockMvcRequestBuilders.
                post("/resources/cohorteditor/getsubjects").
                content(mapper.writeValueAsString(distinctSubjectsRequest)).
                contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(post)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        List<String> returnedSubjects = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<String>>() {
        });

        assertThat(returnedSubjects).isEqualTo(subjects);
        verify(mockSavedFilterService, times(1)).getDistinctSubjects(any(Datasets.class), any(Long.class));
        verifyNoMoreInteractions(mockSavedFilterService);
    }

    @Test
    public void shouldSaveFilters() throws Exception {

        SavedFilter savedFilter = new SavedFilter();
        savedFilter.setName("name updated");

        SavedFilterVO savedFilterVO = new SavedFilterVO();
        savedFilterVO.setSavedFilter(savedFilter);

        SavedFilterInstance savedFilterInstance = new SavedFilterInstance();
        LabFilters labs = new LabFilters();
        savedFilterInstance.setJson(FiltersObjectMapper.toString(labs));
        savedFilterInstance.setFilterView(LABS);

        savedFilterVO.setCohortFilters(newArrayList(savedFilterInstance));

        CohortEditorSaveFiltersRequest savefiltersRequest = new CohortEditorSaveFiltersRequest();
        savefiltersRequest.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());
        savefiltersRequest.setSavedFilterVO(savedFilterVO);

        when(mockSavedFilterService.saveAndListSavedFilters(any(), any())).thenReturn(newArrayList(new SavedFilterVO()));

        MockHttpServletRequestBuilder post = MockMvcRequestBuilders.
                post("/resources/cohorteditor/savefilters").
                content(mapper.writeValueAsString(savefiltersRequest)).
                contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(post)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        List<SavedFilterVO> savedFiltersVo = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<SavedFilterVO>>() {
        });

        assertThat(savedFiltersVo).isEqualTo(newArrayList(new SavedFilterVO()));
        verify(mockSavedFilterService, times(1)).saveAndListSavedFilters(eq(DUMMY_DETECT_DATASETS), eq(savedFilterVO));
        verifyNoMoreInteractions(mockSavedFilterService);
    }

    @Test
    public void shouldListMyFilters() throws Exception {

        DatasetsRequest requestBody = new DatasetsRequest();
        requestBody.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());

        SavedFilter savedFilter = new SavedFilter();
        savedFilter.setName("name updated");

        SavedFilterVO savedFilterVO = new SavedFilterVO();
        savedFilterVO.setSavedFilter(savedFilter);

        SavedFilterInstance savedFilterInstance = new SavedFilterInstance();
        LabFilters labs = new LabFilters();
        savedFilterInstance.setJson(FiltersObjectMapper.toString(labs));
        savedFilterInstance.setFilterView(LABS);

        savedFilterVO.setCohortFilters(newArrayList(savedFilterInstance));

        when(mockSavedFilterService.listByUserAndDatasets(DUMMY_DETECT_DATASETS.getDatasetsList())).thenReturn(newArrayList(new SavedFilterVO()));

        MockHttpServletRequestBuilder post = MockMvcRequestBuilders.
                post("/resources/cohorteditor/savefilters/list").
                content(mapper.writeValueAsString(requestBody)).
                contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(post)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        List<SavedFilterVO> savedFiltersVo = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<SavedFilterVO>>() {
        });

        assertThat(savedFiltersVo).isEqualTo(newArrayList(new SavedFilterVO()));
        verify(mockSavedFilterService, times(1)).listByUserAndDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());
        verifyNoMoreInteractions(mockSavedFilterService);
    }

    @Test
    public void shouldListDatasetUsers() throws Exception {

        DatasetsRequest requestBody = new DatasetsRequest();
        requestBody.setDatasets(newArrayList(new DetectDataset(1L)));

        when(mockCohortUsersService.getDatasetUsersExcludingCurrentUser(any())).thenReturn(newArrayList());

        MockHttpServletRequestBuilder post = MockMvcRequestBuilders.
                post("/resources/cohorteditor/dataset-users").
                content(mapper.writeValueAsString(requestBody)).
                contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(post)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        List<UserVO> restResponse = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<UserVO>>() {
        });

        assertThat(restResponse).isEqualTo(newArrayList());
        verify(mockCohortUsersService, times(1)).getDatasetUsersExcludingCurrentUser(any());
        verifyNoMoreInteractions(mockCohortUsersService);
    }
}
