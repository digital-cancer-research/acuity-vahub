package com.acuity.visualisations.rawdatamodel.dataproviders.common;

import com.acuity.visualisations.rawdatamodel.dataproviders.PopulationDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.vo.HasStringId;
import com.acuity.visualisations.rawdatamodel.vo.HasSubjectId;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.SubjectAwareWrapper;
import com.acuity.va.security.acl.domain.Datasets;
import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class SubjectAwareDatasetsDataProvider<T extends HasSubjectId & HasStringId, W extends SubjectAwareWrapper<T>>
        extends DatasetsDataProvider<T, W> {
    @Autowired
    @Getter(AccessLevel.PROTECTED)
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;

    @Override
    protected Collection<W> wrap(Datasets datasets, Collection<T> events) {
        final Map<String, Subject> subjects = populationDatasetsDataProvider.loadData(datasets)
                .stream()
                .collect(Collectors.toMap(Subject::getSubjectId, s -> s));
        return events.stream()
                .filter(e -> subjects.containsKey(e.getSubjectId())) //this filters out events for subjects not in study population
                .map(e -> getWrapperInstance(e, subjects.get(e.getSubjectId())))
                .collect(Collectors.toList());
    }

    protected abstract W getWrapperInstance(T event, Subject subject);
}
