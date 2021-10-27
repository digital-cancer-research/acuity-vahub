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

import com.acuity.visualisations.rawdatamodel.vo.PkResultRaw;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.AssessedTargetLesion;
import com.acuity.va.security.acl.domain.Dataset;
import com.acuity.va.security.acl.domain.Datasets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static java.util.stream.Collectors.toMap;

@Component
public class PkResultWithResponseDatasetsDataProvider extends PkResultDatasetsDataProvider {

    @Autowired
    private AssessedTargetLesionDatasetsDataProvider assessedTargetLesionDatasetsDataProvider;

    @Override
    protected Collection<PkResultRaw> getData(Dataset dataset) {
        Datasets dss = new Datasets(dataset);
        Collection<PkResultRaw> pkResults = super.getData(dataset);
        Collection<AssessedTargetLesion> assessedTargetLesions = assessedTargetLesionDatasetsDataProvider.loadData(dss);
        return mergeIntoPkResultWithResponse(pkResults, assessedTargetLesions);
    }

    private List<PkResultRaw> mergeIntoPkResultWithResponse(Collection<PkResultRaw> pkResults,
                                                            Collection<AssessedTargetLesion>
                                                                    assessedTargetLesions) {
        Map<String, String> groupedBestResponsesBySubject = bestResponseBySubject(assessedTargetLesions);
        List<PkResultRaw> pkResultWithResponse = new ArrayList<>();
        for (PkResultRaw pkResult : pkResults) {
            String bestOverAllResponse = groupedBestResponsesBySubject.get(pkResult.getSubjectId());
            if (bestOverAllResponse != null) {
                pkResultWithResponse.add(pkResult.toBuilder().bestOverallResponse(bestOverAllResponse).build());
            }
        }
        return pkResultWithResponse;
    }

    private Map<String, String> bestResponseBySubject(Collection<AssessedTargetLesion> assessedTargetLesions) {
        return assessedTargetLesions.stream()
                .collect(toMap(AssessedTargetLesion::getSubjectId,
                        atl -> atl.getEvent().getBestResponse(), (oldValue, newValue) -> oldValue));
    }
}
