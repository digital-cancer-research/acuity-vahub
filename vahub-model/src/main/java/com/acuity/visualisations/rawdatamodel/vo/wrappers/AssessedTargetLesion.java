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

package com.acuity.visualisations.rawdatamodel.vo.wrappers;

import com.acuity.visualisations.rawdatamodel.vo.AssessedTargetLesionRaw;
import com.acuity.visualisations.rawdatamodel.vo.EntityAttribute;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Comparator;

public class AssessedTargetLesion extends SubjectAwareWrapper<AssessedTargetLesionRaw> implements Serializable {

    public AssessedTargetLesion(AssessedTargetLesionRaw event, Subject subject) {
        super(event, subject);
    }

    public enum Attributes implements GroupByOption<AssessedTargetLesion> {
        ID(EntityAttribute.attribute("id", AssessedTargetLesion::getId)),
        STUDY_ID(EntityAttribute.attribute("studyId", AssessedTargetLesion::getStudyId)),
        STUDY_PART(EntityAttribute.attribute("studyPart", AssessedTargetLesion::getStudyPart)),
        SUBJECT_ID(EntityAttribute.attribute("subjectId", AssessedTargetLesion::getSubjectId)),
        SUBJECT(EntityAttribute.attribute("subject", AssessedTargetLesion::getSubjectCode)),
        BASELINE_DATE(EntityAttribute.attribute("baselineDate", (AssessedTargetLesion e) -> e.getEvent().getAssessmentRaw().getBaselineDate())),
        IS_BASELINE(EntityAttribute.attribute("baseline", (AssessedTargetLesion e) -> e.getEvent().isBaseline())),
        PERCENTAGE_CHANGE(EntityAttribute.attribute("percentageChange", (AssessedTargetLesion e) -> e.getEvent().getSumPercentageChangeFromBaseline())),
        LESION_PERCENTAGE_CHANGE(EntityAttribute.attribute("lesionPercentageChange", (AssessedTargetLesion e) -> e.getEvent()
                .getTargetLesionRaw().getLesionPercentageChangeFromBaseline())),
        ABSOLUTE_CHANGE(EntityAttribute.attribute("absoluteChangeFromBaseline",
                (AssessedTargetLesion e) -> e.getEvent().getLesionsDiameterPerAssessment() - e.getEvent()
                .getBaselineLesionsDiameter())),
        IS_BEST_PERCENTAGE_CHANGE(EntityAttribute.attribute("bestPercentageChange", (AssessedTargetLesion e) -> e.getEvent().isBestPercentageChange())),
        BEST_PERCENTAGE_CHANGE(EntityAttribute.attribute("sumBestPercentageChange", (AssessedTargetLesion e) ->
                e.getEvent().getSumBestPercentageChangeFromBaseline())),
        VISIT_NUMBER(EntityAttribute.attribute("visitNumber", (AssessedTargetLesion e) -> e.getEvent().getVisitNumber())),
        VISIT_DATE(EntityAttribute.attribute("visitDate", (AssessedTargetLesion e) -> e.getEvent().getTargetLesionRaw().getVisitDate())),
        LESIONS_COUNT(EntityAttribute.attribute("lesionsCount", (AssessedTargetLesion e) -> e.getEvent().getLesionsCountPerAssessment())),
        LESIONS_COUNT_AT_BASELINE(EntityAttribute.attribute("lesionsCountAtBaseline", (AssessedTargetLesion e) -> e.getEvent().getLesionsCountAtBaseline())),
        LESION_NUMBER(EntityAttribute.attribute("lesionNumber", (AssessedTargetLesion e) -> e.getEvent().getLesionNumber())),
        LESION_DATE(EntityAttribute.attribute("lesionDate", (AssessedTargetLesion e) -> e.getEvent().getLesionDate())),
        LESION_DIAMETER(EntityAttribute.attribute("lesionDiameter", (AssessedTargetLesion e) -> e.getEvent().getLesionDiameter())),
        LESIONS_DIAMETER_PER_ASSESSMENT(EntityAttribute.attribute("lesionsDiametersPerAssessment",
                (AssessedTargetLesion e) -> e.getEvent().getLesionsDiameterPerAssessment())),
        LESIONS_DIAMETER_AT_BASELINE(EntityAttribute.attribute("lesionsDiametersAtBaseline",
                (AssessedTargetLesion e) -> e.getEvent().getBaselineLesionsDiameter())),
        ASSESSMENT_METHOD(EntityAttribute.attribute("assessmentMethod", (AssessedTargetLesion e) -> e.getEvent().getAssessmentMethod())),
        RESPONSE(EntityAttribute.attribute("response", (AssessedTargetLesion e) -> e.getEvent().getResponse())),
        BEST_RESPONSE(EntityAttribute.attribute("bestResponse", (AssessedTargetLesion e) -> e.getEvent().getBestResponse())),
        NON_TARGET_LESIONS_PRESENT(EntityAttribute.attribute("nonTargetLesionsPresent",
                (AssessedTargetLesion e) -> e.getEvent().getNonTargetLesionsPresent())),
        SUBJECT_LESION(EntityAttribute.attribute("subjectLesion",
                (AssessedTargetLesion e) -> new SubjectLesion(e.getSubjectCode(), e.getEvent().getTargetLesionRaw().getLesionNumber())));

        @Getter
        private final EntityAttribute<AssessedTargetLesion> attribute;

        Attributes(EntityAttribute<AssessedTargetLesion> attribute) {
            this.attribute = attribute;
        }
    }

    public boolean isNotBeforeBaseline() {
        return this.getEvent().getLesionDate() != null && this.getSubject().getBaselineDate() != null
                && !this.getEvent().getLesionDate().before(this.getSubject().getBaselineDate());
    }

    @EqualsAndHashCode
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class SubjectLesion implements Serializable, Comparable<SubjectLesion> {
        private String subjectCode;
        private String lesionNumber;

        @Getter(lazy = true)
        private final String asString = String.format("%s lesion %s", subjectCode, lesionNumber);

        @Override
        public String toString() {
            return getAsString();
        }

        @Override
        public int compareTo(SubjectLesion o) {
            return Comparator.comparing(SubjectLesion::getSubjectCode)
                    .thenComparing(SubjectLesion::getLesionNumber).compare(this, o);
        }
    }
}
