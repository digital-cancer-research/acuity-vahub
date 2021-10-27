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
