package com.acuity.visualisations.rawdatamodel.service.dod;

import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.va.security.acl.domain.Datasets;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by knml167 on 5/23/2017.
 * Interface declaring methods to work with Details On Demand at service layer
 */
public interface DoDService<T> {
    List<Map<String, String>> getDetailsOnDemandData(Datasets datasets, Set ids, List<SortAttrs> sortAttrs, long from, long count);
    List<Map<String, String>> getDetailsOnDemandData(Datasets datasets, String subjectId, Filters<T> eventFilters);
    List<Map<String, String>> getSingleSubjectData(Datasets datasets, String subjectId, Filters<T> eventFilters);
    void writeAllDetailsOnDemandCsv(Datasets datasets, Writer writer, Filters<T> filters, PopulationFilters populationFilters) throws IOException;
    void writeSelectedDetailsOnDemandCsv(Datasets datasets, Set ids, Writer writer) throws IOException;
}
