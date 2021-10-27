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

import com.acuity.visualisations.rawdatamodel.vo.biomarker.BiomarkerRaw;
import com.acuity.visualisations.rawdatamodel.vo.EntityAttribute;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class Biomarker extends SubjectAwareWrapper<BiomarkerRaw> implements Serializable {

    public Biomarker(BiomarkerRaw event, Subject subject) {
        super(event, subject);
    }

    public enum Attributes implements GroupByOption<Biomarker> {

        ID(EntityAttribute.attribute("id", Biomarker::getId)),
        STUDY_ID(EntityAttribute.attribute("studyId", Biomarker::getStudyId)),
        STUDY_PART(EntityAttribute.attribute("studyPart", Biomarker::getStudyPart)),
        SUBJECT_ID(EntityAttribute.attribute("subjectId", Biomarker::getSubjectId)),
        SUBJECT(EntityAttribute.attribute("subject", Biomarker::getSubjectCode)),
        GENE(EntityAttribute.attribute("gene", (Biomarker e) -> e.getEvent().getGene())),
        MUTATION(EntityAttribute.attribute("mutation", (Biomarker e) -> e.getEvent().getMutation())),
        SOMATIC_STATUS(EntityAttribute.attribute("somaticStatus", (Biomarker e) -> e.getEvent().getSomaticStatus())),
        AMINO_ACID_CHANGE(EntityAttribute.attribute("aminoAcidChange", (Biomarker e) -> e.getEvent().getAminoAcidChange())),
        COPY_NUMBER_ALTER_COPY_NUMBER(EntityAttribute.attribute("copyNumberAlterationCopyNumber",
                (Biomarker e) -> e.getEvent().getCopyNumberAlterationCopyNumber())),
        ALLELE_FREQUENCY(EntityAttribute.attribute("alleleFrequency", (Biomarker e) -> e.getEvent().getMutantAlleleFrequency()));

        @Getter
        private final EntityAttribute<Biomarker> attribute;

        Attributes(EntityAttribute<Biomarker> attribute) {
            this.attribute = attribute;
        }
    }

}

