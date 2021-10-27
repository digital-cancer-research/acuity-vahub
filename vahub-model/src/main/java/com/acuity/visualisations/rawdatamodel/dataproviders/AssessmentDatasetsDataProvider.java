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

import com.acuity.visualisations.rawdatamodel.dataproviders.common.SubjectAwareDatasetsRegularDataProvider;
import com.acuity.visualisations.rawdatamodel.vo.AssessmentRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.TargetLesionRaw;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Assessment;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.TargetLesion;
import com.acuity.va.security.acl.domain.Dataset;
import com.acuity.va.security.acl.domain.Datasets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class AssessmentDatasetsDataProvider extends SubjectAwareDatasetsRegularDataProvider<AssessmentRaw, Assessment> implements CommonBaselineDataProvider {

    @Autowired
    private TargetLesionDatasetsDataProvider targetLesionDatasetsDataProvider;
    @Autowired
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;

    @Override
    protected Assessment getWrapperInstance(AssessmentRaw event, Subject subject) {
        return new Assessment(event, subject);
    }

    @Override
    protected Class<AssessmentRaw> rawDataClass() {
        return AssessmentRaw.class;
    }

    @Override
    protected Collection<AssessmentRaw> getData(Dataset dataset) {
        Datasets dss = new Datasets(dataset);
        Collection<AssessmentRaw> assessments = super.getData(dataset);
        Collection<Subject> subjects = populationDatasetsDataProvider.loadData(dss);
        Collection<TargetLesion> targetLesions = targetLesionDatasetsDataProvider.loadData(dss);
        Map<String, List<TargetLesionRaw>> tlBySubject = groupTLBySubject(targetLesions);
        Map<String, Date> baselineDatePerSubject = defineBaselineDatePerSubject(tlBySubject, subjects);

        return assessments.stream()
                .map(e -> e.toBuilder().baselineDate(baselineDatePerSubject.get(e.getSubjectId())).build())
                .collect(Collectors.toList());
    }
}
