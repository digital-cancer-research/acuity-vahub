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
