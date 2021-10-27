package com.acuity.visualisations.rest.resources.util;

import lombok.experimental.UtilityClass;

import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
@UtilityClass
public class DetailsOnDemandCsvDownloadingUtils {

    public static void setDownloadHeaders(HttpServletResponse response, String filename) {
        response.addHeader("Content-disposition", "attachment;filename=" + filename);
        response.setContentType("txt/csv");
    }

    public static void setDownloadHeaders(HttpServletResponse response) {
        setDownloadHeaders(response, "details_on_demand.csv");
    }
}
