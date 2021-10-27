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

import javax.servlet.http.HttpServletResponse;

/**
 * @deprecated please don't inherit this class. use com.acuity.visualisations.rest.resources.util.DetailsOnDemandCsvDownloadingUtils instead
 */
@Deprecated
public abstract class DetailsOnDemandCsvDownloader {

    protected void setDownloadHeaders(HttpServletResponse response, String filename) {
        response.addHeader("Content-disposition", "attachment;filename=" + filename);
        response.setContentType("txt/csv");
    }

    protected void setDownloadHeaders(HttpServletResponse response) {
        setDownloadHeaders(response, "details_on_demand.csv");
    }
}
