package com.acuity.visualisations.rawdatamodel.vo;

import java.util.Map;

/**
 * Used to define group of dynamic filters
 * Currently used only for detect db
 * Created by khnp879 on 8/9/2018.
 */
public interface HasStudySpecificFilters {

    Map<String, String> getStudySpecificFilters();
}
