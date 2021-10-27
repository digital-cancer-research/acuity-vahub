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
import com.acuity.visualisations.rawdatamodel.vo.AssessedNonTargetLesionRaw;
import com.acuity.visualisations.rawdatamodel.vo.AssessmentRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.AssessedNonTargetLesion;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Assessment;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.NonTargetLesion;
import com.acuity.va.security.acl.domain.Dataset;
import com.acuity.va.security.acl.domain.Datasets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This data provider is for aggregated entity {@link AssessedNonTargetLesionRaw}. Since it's aggregated, it does NOT
 * have an own repository to communicate with data base.
 */
@Component
public class AssessedNonTargetLesionDatasetsDataProvider extends
        SubjectAwareDatasetsDataProvider<AssessedNonTargetLesionRaw, AssessedNonTargetLesion> {

    @Autowired
    private AssessmentDatasetsDataProvider assessmentDatasetsDataProvider;
    @Autowired
    private NonTargetLesionDatasetsDataProvider nonTargetLesionDatasetsDataProvider;

    @Override
    protected AssessedNonTargetLesion getWrapperInstance(AssessedNonTargetLesionRaw event, Subject subject) {
        return new AssessedNonTargetLesion(event, subject);
    }

    @Override
    protected Class<AssessedNonTargetLesionRaw> rawDataClass() {
        return AssessedNonTargetLesionRaw.class;
    }

    @Override
    protected Collection<AssessedNonTargetLesionRaw> getData(Dataset dataset) {
        return dataProvider.getData(AssessedNonTargetLesionRaw.class, dataset, ds -> {

                    Datasets dss = new Datasets(ds);
                    Collection<Assessment> assessments = assessmentDatasetsDataProvider.loadData(dss);
                    Collection<NonTargetLesion> nonTargetLesions = nonTargetLesionDatasetsDataProvider.loadData(dss);

                    return mergeIntoAssessedNonTargetLesions(assessments, nonTargetLesions);
                }
        );
    }

    /**
     * Derives baseline, lesions diameters, percentage changes and best percentage change from
     * {@link com.acuity.visualisations.rawdatamodel.vo.wrappers.TargetLesion} and
     * {@link Subject}. Derives response and best response from {@link Assessment}
     *
     * @param assessments
     * @param lesions
     * @return
     */
    private List<AssessedNonTargetLesionRaw> mergeIntoAssessedNonTargetLesions(Collection<Assessment> assessments,
                                                                               Collection<NonTargetLesion> lesions) {

        List<AssessedNonTargetLesionRaw> natls = new ArrayList<>();

        Map<String, Map<Integer, List<NonTargetLesion>>> lesionsBySubjectByVisitNumber =
                groupTLBySubjectAndVisitNumber(lesions);
        Map<String, Map<Integer, Assessment>> assessmentsBySubjectByVisNum =
                groupAssessmentsBySubjectAndVisitNumber(assessments);

        lesionsBySubjectByVisitNumber.forEach((subjectId, subjectLesionsByVisNumMap) -> {
            Map<Integer, Assessment> assessmentsByVisNumMap = assessmentsBySubjectByVisNum.get(subjectId);
            List<AssessedNonTargetLesionRaw> assessedNonTargetLesions =
                    mergeByVisitNumberAndDate(subjectId, assessmentsByVisNumMap, subjectLesionsByVisNumMap);
            natls.addAll(assessedNonTargetLesions);
        });

        return natls;
    }

    /**
     * Merges non-target lesions and assessments of response into {@link AssessedNonTargetLesionRaw}
     *
     * @param subjectId
     * @param assessmentByVisNum
     * @param subjectLesionsByVisNum
     * @return
     */
    private List<AssessedNonTargetLesionRaw> mergeByVisitNumberAndDate(String subjectId, Map<Integer, Assessment> assessmentByVisNum,
                                                                       Map<Integer, List<NonTargetLesion>> subjectLesionsByVisNum) {
        List<AssessedNonTargetLesionRaw> assessedTargetLesions = new ArrayList<>();
        subjectLesionsByVisNum.forEach((number, ntls) -> {
            Assessment as = (assessmentByVisNum == null ? null : assessmentByVisNum.get(number));
            AssessmentRaw asRaw = (as == null ? null : as.getEvent());
            String response = (asRaw == null ? AssessmentRaw.Response.NO_ASSESSMENT.getName() : asRaw.getResponse());
            ntls.forEach(ntl ->
                    assessedTargetLesions.add(AssessedNonTargetLesionRaw.builder()
                            .subjectId(subjectId)
                            .response(assessmentMatchByVisDate(asRaw, ntl.getEvent().getVisitDate()) ? response : null)
                            .assessmentRaw(assessmentMatchByVisDate(asRaw, ntl.getEvent().getVisitDate()) ? asRaw : null)
                            .nonTargetLesionRaw(ntl.getEvent())
                            .build())
            );
        });
        return assessedTargetLesions;
    }

    private boolean assessmentMatchByVisDate(AssessmentRaw as, Date visDate) {
        return as != null && as.getVisitDate() != null && as.getVisitDate().equals(visDate);
    }

    /**
     * Method to group list of {@link NonTargetLesion} by {@code subjectId} and {@code visitNumber}.
     * Before grouping the list is filtered to get rid of non-valuable entities.
     *
     * @param lesions
     * @return
     */
    private Map<String, Map<Integer, List<NonTargetLesion>>> groupTLBySubjectAndVisitNumber(Collection<NonTargetLesion> lesions) {
        return lesions.stream()
                .filter(l -> l.getEvent().getLesionDate() != null)
                .filter(l -> l.getEvent().getVisitDate() != null)
                .filter(l -> l.getEvent().getVisitNumber() != null)
                .collect(Collectors.groupingBy(NonTargetLesion::getSubjectId,
                        Collectors.groupingBy(ntl -> ntl.getEvent().getVisitNumber())));
    }

    /**
     * Method to group list of {@link Assessment} by {@code subjectId} and {@code visitNumber}.
     *
     * @param assessments
     * @return
     */
    private Map<String, Map<Integer, Assessment>> groupAssessmentsBySubjectAndVisitNumber(Collection<Assessment> assessments) {
        return assessments.stream()
                .filter(a -> a.getEvent().getVisitNumber() != null)
                .filter(a -> a.getEvent().getVisitDate() != null)
                .collect(Collectors.groupingBy(Assessment::getSubjectId,
                        Collectors.toMap(a -> a.getEvent().getVisitNumber(), Function.identity())));
    }

}
