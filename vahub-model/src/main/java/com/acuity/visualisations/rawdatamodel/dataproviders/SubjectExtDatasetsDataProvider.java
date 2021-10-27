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

package com.acuity.visualisations.rawdatamodel.dataproviders;

import com.acuity.visualisations.rawdatamodel.dataproviders.common.SubjectAwareDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.util.DaysUtil;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.SubjectExtRaw;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.DiseaseExtent;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Pathology;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.SubjectAwareWrapper;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.SubjectExt;
import com.acuity.va.security.acl.domain.Dataset;
import com.acuity.va.security.acl.domain.Datasets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.OptionalInt;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * This data provider is for aggregated entity {@link SubjectExtRaw}. Since it's aggregated, it does NOT
 * have an own repository to communicate with data base.
 */
@Component
public class SubjectExtDatasetsDataProvider extends SubjectAwareDatasetsDataProvider<SubjectExtRaw, SubjectExt> {

    @Autowired
    private DiseaseExtentDatasetsDataProvider diseaseExtentDatasetsDataProvider;
    @Autowired
    private PathologyDatasetsDataProvider pathologyDatasetsDataProvider;

    @Override
    protected SubjectExt getWrapperInstance(SubjectExtRaw event, Subject subject) {
        return new SubjectExt(event, subject);
    }

    @Override
    protected Class<SubjectExtRaw> rawDataClass() {
        return SubjectExtRaw.class;
    }

    @Override
    protected Collection<SubjectExtRaw> getData(Dataset dataset) {
        return dataProvider.getData(SubjectExtRaw.class, dataset, ds -> {

            Datasets dss = new Datasets(ds);
            Collection<Subject> subjects = getPopulationDatasetsDataProvider().loadData(dss);

            Map<String, Date> recentProgressionDateBySubject = getRecentProgressionDateBySubject(dss);
            Map<String, Date> diagnosisDateBySubject = getDiagnosisDateBySubject(dss);

            return subjects.stream()
                    .map(s -> {
                                Date diagnosisDate = diagnosisDateBySubject.get(s.getId());
                                OptionalInt daysFromDiagnosisDate = DaysUtil.daysBetween(diagnosisDate, s.getFirstTreatmentDate());
                                return SubjectExtRaw.builder().subjectId(s.getId())
                                        .diagnosisDate(diagnosisDate)
                                        .daysFromDiagnosisDate(daysFromDiagnosisDate.isPresent() ? daysFromDiagnosisDate.getAsInt() : null)
                                        .recentProgressionDate(recentProgressionDateBySubject.get(s.getId()))
                                        .build();

                            }).collect(toList());
        });
    }

    // the earliest date for each subject
    private Map<String, Date> getDiagnosisDateBySubject(Datasets dss) {
        Collection<Pathology> pathologies = pathologyDatasetsDataProvider.loadData(dss);
        return pathologies.stream()
                .filter(t -> t.getEvent().getDate() != null)
                .filter(t -> t.getEvent().getDate().before(t.getSubject().getFirstTreatmentDate()))
                .collect(Collectors.toMap(SubjectAwareWrapper::getSubjectId,
                        d -> d.getEvent().getDate(),
                        (d1, d2) -> d1.before(d2) ? d1 : d2));
    }

    // the greatest date for each subject, but only dates < the date of first dose should be considered
    private Map<String, Date> getRecentProgressionDateBySubject(Datasets dss) {
        Collection<DiseaseExtent> diseaseExtents = diseaseExtentDatasetsDataProvider.loadData(dss);
        return diseaseExtents.stream()
                .filter(t -> t.getEvent().getRecentProgressionDate() != null)
                .filter(t -> t.getEvent().getRecentProgressionDate().before(t.getSubject().getFirstTreatmentDate()))
                .collect(Collectors.toMap(SubjectAwareWrapper::getSubjectId,
                d -> d.getEvent().getRecentProgressionDate(),
                (d1, d2) -> d1.after(d2) ? d1 : d2));
    }
}
