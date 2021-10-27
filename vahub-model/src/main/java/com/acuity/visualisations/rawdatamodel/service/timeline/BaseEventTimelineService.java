package com.acuity.visualisations.rawdatamodel.service.timeline;

import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.service.timeline.data.TimelineTrack;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.SubjectAwareWrapper;
import com.acuity.va.security.acl.domain.Datasets;

import java.util.List;

public interface BaseEventTimelineService<T extends SubjectAwareWrapper> {

    List<T> getTimelineFilteredData(Datasets datasets, Filters<T> filters, PopulationFilters populationFilters);

    List<Subject> getTimelineFilteredSubjects(Datasets datasets, Filters<T> filters, PopulationFilters populationFilters);

    TimelineTrack getTimelineTrack();
}
