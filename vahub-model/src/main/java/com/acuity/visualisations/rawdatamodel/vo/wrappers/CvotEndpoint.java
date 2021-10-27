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

import com.acuity.visualisations.rawdatamodel.util.Column;
import com.acuity.visualisations.rawdatamodel.vo.CvotEndpointRaw;
import com.acuity.visualisations.rawdatamodel.vo.EntityAttribute;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.HasAssociatedAe;
import com.acuity.visualisations.rawdatamodel.vo.HasStartDate;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by knml167 on 6/9/2017.
 */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CvotEndpoint extends SubjectAwareWrapper<CvotEndpointRaw> implements HasStartDate, Serializable, HasAssociatedAe {

    public CvotEndpoint(CvotEndpointRaw event, Subject subject) {
        super(event, subject);
    }

    @Column(columnName = "aeNumber", order = 0, displayName = "Ae number")
    @Override
    public String getAeNumber() {
        return getSubjectCode() + "-" + getEvent().getAeNumber();
    }

    @Override
    public Date getStartDate() {
        return getEvent().getStartDate();
    }

    public enum Attributes implements GroupByOption<CvotEndpoint> {

        ID(EntityAttribute.attribute("id", CvotEndpoint::getId)),
        STUDY_ID(EntityAttribute.attribute("studyId", CvotEndpoint::getStudyId)),
        STUDY_PART(EntityAttribute.attribute("studyPart", CvotEndpoint::getStudyPart)),
        SUBJECT_ID(EntityAttribute.attribute("subjectId", CvotEndpoint::getSubjectId)),
        SUBJECT(EntityAttribute.attribute("subjectId", CvotEndpoint::getSubjectCode)),
        ARM(EntityAttribute.attribute("arm",  (CvotEndpoint e) -> e.getSubject().getActualArm())),
        AE_NUMBER(EntityAttribute.attribute("aeNumber", CvotEndpoint::getAeNumber)),
        START_DATE(EntityAttribute.attribute("startDate", CvotEndpoint::getStartDate)),
        TERM(EntityAttribute.attribute("term", (CvotEndpoint e) -> e.getEvent().getTerm())),
        CATEGORY_1(EntityAttribute.attribute("category1", (CvotEndpoint e) -> e.getEvent().getCategory1())),
        CATEGORY_2(EntityAttribute.attribute("category2", (CvotEndpoint e) -> e.getEvent().getCategory2())),
        CATEGORY_3(EntityAttribute.attribute("category3", (CvotEndpoint e) -> e.getEvent().getCategory3())),
        DESCRIPTION_1(EntityAttribute.attribute("description1", (CvotEndpoint e) -> e.getEvent().getDescription1())),
        DESCRIPTION_2(EntityAttribute.attribute("description2", (CvotEndpoint e) -> e.getEvent().getDescription2())),
        DESCRIPTION_3(EntityAttribute.attribute("description3", (CvotEndpoint e) -> e.getEvent().getDescription3()));

        @Getter
        private final EntityAttribute<CvotEndpoint> attribute;

        Attributes(EntityAttribute<CvotEndpoint> attribute) {
            this.attribute = attribute;
        }

    }

}
