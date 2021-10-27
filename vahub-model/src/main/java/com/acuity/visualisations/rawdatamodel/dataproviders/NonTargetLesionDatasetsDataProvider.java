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
import com.acuity.visualisations.rawdatamodel.vo.NonTargetLesionRaw;
import com.acuity.visualisations.rawdatamodel.vo.NonTargetLesionRaw.NonTargetLesionRawBuilder;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.TargetLesionRaw;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.NonTargetLesion;
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
public class NonTargetLesionDatasetsDataProvider extends SubjectAwareDatasetsRegularDataProvider<NonTargetLesionRaw, NonTargetLesion>
        implements CommonBaselineDataProvider {

    @Autowired
    private TargetLesionDatasetsDataProvider targetLesionDatasetsDataProvider;
    @Autowired
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;

    @Override
    protected NonTargetLesion getWrapperInstance(NonTargetLesionRaw event, Subject subject) {
        return new NonTargetLesion(event, subject);
    }

    @Override
    protected Class<NonTargetLesionRaw> rawDataClass() {
        return NonTargetLesionRaw.class;
    }

    @Override
    protected Collection<NonTargetLesionRaw> getData(Dataset dataset) {
        Datasets dss = new Datasets(dataset);
        Collection<NonTargetLesionRaw> ntls = super.getData(dataset);
        Collection<Subject> subjects = populationDatasetsDataProvider.loadData(dss);
        Collection<TargetLesion> targetLesions = targetLesionDatasetsDataProvider.loadData(dss);
        Map<String, List<TargetLesionRaw>> tlBySubject = groupTLBySubject(targetLesions);
        Map<String, Date> baselineDatePerSubject = defineBaselineDatePerSubject(tlBySubject, subjects);
        return ntls.stream()
                .map(e -> {
                    NonTargetLesionRawBuilder ntlBuilder = e.toBuilder()
                            .baselineDate(baselineDatePerSubject.get(e.getSubjectId()));
                    // don't shorten 'Not evaluable' to 'NA' for non-target lesions
                    // to avoid confusion with 'Not evaluated'
                    // remove this case when standard responses for non-target lesions and overall assessment
                    // responses will be stored distinguishable in the database - must be done in future stories
                    if (AssessmentRaw.Response.NOT_EVALUABLE.getName().equals(e.getResponse())) {
                        ntlBuilder.responseShort(e.getResponse());
                    }
                    return ntlBuilder.build();
                })
                .collect(Collectors.toList());
    }

}
