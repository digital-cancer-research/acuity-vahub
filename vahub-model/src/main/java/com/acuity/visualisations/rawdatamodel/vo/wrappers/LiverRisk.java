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
import com.acuity.visualisations.rawdatamodel.util.DaysUtil;
import com.acuity.visualisations.rawdatamodel.vo.EntityAttribute;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.HasStartDate;
import com.acuity.visualisations.rawdatamodel.vo.HasSubjectId;
import com.acuity.visualisations.rawdatamodel.vo.LiverRiskRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import java.util.Date;
import java.util.OptionalInt;
import lombok.Getter;

public class LiverRisk extends SubjectAwareWrapper<LiverRiskRaw> implements HasSubjectId, HasStartDate {

    public LiverRisk(LiverRiskRaw event, Subject subject) {
        super(event, subject);
    }

    @Override
    public Date getStartDate() {
        return getEvent().getStartDate();
    }

    public enum Attributes implements GroupByOption<LiverRisk> {

        ID(EntityAttribute.attribute("id", (LiverRisk e) -> e.getEvent().getId())),
        STUDY_ID(EntityAttribute.attribute("studyId", (LiverRisk e) -> e.getSubject().getClinicalStudyCode())),
        STUDY_PART(EntityAttribute.attribute("studyPart", (LiverRisk e) -> e.getSubject().getStudyPart())),
        SUBJECT_ID(EntityAttribute.attribute("subjectId", (LiverRisk e) -> e.getSubject().getSubjectId())),
        SUBJECT(EntityAttribute.attribute("subject", (LiverRisk e) -> e.getSubject().getSubjectCode())),
        POTENTIAL_HYS_LAW_CASE_NUM(EntityAttribute.attribute("potentialHysLawCaseNum",
                (LiverRisk e) -> e.getEvent().getPotentialHysLawCaseNum())),
        VALUE(EntityAttribute.attribute("value", (LiverRisk e) -> e.getEvent().getValue())),
        OCCURRENCE(EntityAttribute.attribute("occurrence", (LiverRisk e) -> e.getEvent().getOccurrence())),
        REFERENCE_PERIOD(EntityAttribute.attribute("referencePeriod", (LiverRisk e) -> e.getEvent().getReferencePeriod())),
        DETAILS(EntityAttribute.attribute("details", (LiverRisk e) -> e.getEvent().getDetails())),
        START_DATE(EntityAttribute.attribute("startDate", LiverRisk::getStartDate)),
        STOP_DATE(EntityAttribute.attribute("stopDate", (LiverRisk e) -> e.getEvent().getStopDate())),
        COMMENT(EntityAttribute.attribute("comment", (LiverRisk e) -> e.getEvent().getComment())),
        STUDY_DAY_AT_START(EntityAttribute.attribute("studyDayAtStart", LiverRisk::getStudyDayAtStart)),
        STUDY_DAY_AT_STOP(EntityAttribute.attribute("studyDayAtStop", LiverRisk::getStudyDayAtStop));

        @Getter
        private final EntityAttribute<LiverRisk> attribute;

        Attributes(EntityAttribute<LiverRisk> attribute) {
            this.attribute = attribute;
        }
    }

    @Column(order = 8, displayName = "Study day at liver risk factor start", columnName = "studyDayAtStart",
            type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    public Integer getStudyDayAtStart() {
        OptionalInt res = DaysUtil.daysBetween(getSubject().getFirstTreatmentDate(), getStartDate());
        return res.isPresent() ? res.getAsInt() : null;
    }

    @Column(order = 9, displayName = "Study day at liver risk factor stop", columnName = "studyDayAtStop",
            type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    public Integer getStudyDayAtStop() {
        OptionalInt res = DaysUtil.daysBetween(getSubject().getFirstTreatmentDate(), getEvent().getStopDate());
        return res.isPresent() ? res.getAsInt() : null;
    }
}
