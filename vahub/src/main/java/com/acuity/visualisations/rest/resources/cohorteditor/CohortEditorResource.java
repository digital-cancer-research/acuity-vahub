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

import com.acuity.visualisations.cohorteditor.repository.SavedFilterRepository;
import com.acuity.visualisations.cohorteditor.service.CohortUsersService;
import com.acuity.visualisations.cohorteditor.service.SavedFilterService;
import com.acuity.visualisations.cohorteditor.util.SavedFilterVOConverter;
import com.acuity.visualisations.cohorteditor.vo.SavedFilterVO;
import com.acuity.visualisations.rest.model.request.cohort.CohortDistinctSubjectsRequest;
import com.acuity.visualisations.rest.model.request.cohort.CohortEditorDeleteRequest;
import com.acuity.visualisations.rest.model.request.cohort.CohortEditorSaveFiltersRequest;
import com.acuity.va.security.acl.domain.DatasetsRequest;
import com.acuity.va.security.acl.domain.AcuitySidDetails;
import com.acuity.visualisations.rest.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping(value = "/resources/cohorteditor/", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
@PreAuthorize(Constants.PRE_AUTHORISE_VISUALISATION)
public class CohortEditorResource {

    @Autowired
    private SavedFilterService savedFilterService;
    @Autowired
    private SavedFilterRepository savedFilterRepository;
    @Autowired
    private SavedFilterVOConverter savedFilterVOConverter;
    @Autowired
    private CohortUsersService cohortUsersService;

    @RequestMapping(value = "/getsubjects", method = POST)
    // Need to add @PreAuthorize( to check permission to savedFilterId
    public List<String> getDistinctSubjects(@RequestBody @Valid CohortDistinctSubjectsRequest requestBody) {

        return savedFilterService.getDistinctSubjects(requestBody.getDatasetsObject(), requestBody.getSavedFilterId());
    }

    @RequestMapping(value = "/savefilters", method = POST)
    public List<SavedFilterVO> saveSavedFilters(@RequestBody @Valid CohortEditorSaveFiltersRequest requestBody) {
        return savedFilterService.saveAndListSavedFilters(requestBody.getDatasetsObject(), requestBody.getSavedFilterVO());
    }

    @RequestMapping(value = "/savefilters/list", method = POST)
    public List<SavedFilterVO> listByUser(@RequestBody @Valid DatasetsRequest requestBody) {
        return savedFilterService.listByUserAndDatasets(requestBody.getDatasets());
    }

    @RequestMapping(value = "/savefilters/delete", method = POST)
    public List<SavedFilterVO> deleteSavedFilters(@RequestBody @Valid CohortEditorDeleteRequest requestBody) {
        savedFilterService.deleteSavedFilters(requestBody.getSavedFilterId());
        return listByUser(requestBody);
    }

    @RequestMapping(value = "/dataset-users", method = POST)
    public List<AcuitySidDetails> listDatasetUsers(@RequestBody DatasetsRequest requestBody) throws IOException, IllegalAccessException {
        return cohortUsersService.getDatasetUsersExcludingCurrentUser(requestBody.getDatasets());
    }
}
