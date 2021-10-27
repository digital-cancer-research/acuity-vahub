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

package com.acuity.visualisations.rest.util;

import javax.servlet.http.HttpServletResponse;

/**
 * @author ksnd199
 */
public final class Constants {

    private Constants() {
    }

    /**
     * Pre authorise the visualisation for each web service end point for visualisations with Have they got permission for this visualisation or is the
     * visualisation training data
     */
    //REQUEST ENDPOINT'S PARAMETER SHOULD BE NAMED AS "requestBody"!
    public static final String PRE_AUTHORISE_VISUALISATION
            = "@permissionEvaluator.hasViewDatasetPermission(authentication, #requestBody.datasets, 3)"; // 3 = AUTHORISED_USER

    /**
     * Pre authorise the visualisation for each web service end point for visualisations with Have they got permission for this visualisation or is the
     * visualisation training data
     */
    //REQUEST ENDPOINT'S PARAMETER SHOULD BE NAMED AS "requestBody"!
    public static final String PRE_AUTHORISE_VISUALISATION_AND_ONCOLOGY
            = "@permissionEvaluator.hasViewDatasetWithExtraPermission(authentication, #requestBody.datasets, 'VIEW_ONCOLOGY_PACKAGE')";

    /**
     * the #requestBody is supposed to be of the {@code EventFilterRequestPopulationAware} type
     */
    //REQUEST ENDPOINT'S PARAMETER SHOULD BE NAMED AS "requestBody"!
    public static final String EMPTY_EVENT_AND_POPULATION_FILTER
            = "#requestBody.getEventFilters().isEmpty() && #requestBody.getPopulationFilters().isEmpty()";

    public static final String EMPTY_POPULATION_FILTER
            = "#requestBody.getPopulationFilters().isEmpty()";

    public static void setDownloadHeader(HttpServletResponse response) {
        response.addHeader("Content-disposition", "attachment;filename=details_on_demand.csv");
        response.setContentType("txt/csv");
    }
}
