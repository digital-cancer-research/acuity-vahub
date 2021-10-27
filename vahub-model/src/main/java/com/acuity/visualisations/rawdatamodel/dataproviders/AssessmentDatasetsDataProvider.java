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
