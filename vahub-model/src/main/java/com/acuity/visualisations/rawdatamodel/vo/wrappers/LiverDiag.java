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
import com.acuity.visualisations.rawdatamodel.vo.LiverDiagRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import lombok.Getter;

import java.io.Serializable;
import java.util.OptionalInt;

public class LiverDiag extends SubjectAwareWrapper<LiverDiagRaw> implements Serializable {
    public LiverDiag(LiverDiagRaw event, Subject subject) {
        super(event, subject);
    }

    @Column(columnName = "studyDayLiverDiagInv", order = 4, displayName = "Study day at liver diagnostic investigation")
    public Integer getStudyDayLiverDiagInv() {
        final OptionalInt res = DaysUtil.daysBetween(getSubject().getDateOfFirstDose(), getEvent().getLiverDiagInvDate());
        return res.isPresent() ? res.getAsInt() : null;
    }

    public enum Attributes implements GroupByOption<LiverDiag> {
        ID(EntityAttribute.attribute("id", (LiverDiag e) -> e.getEvent().getId())),
        STUDY_ID(EntityAttribute.attribute("studyId", (LiverDiag e) -> e.getSubject().getClinicalStudyCode())),
        STUDY_PART(EntityAttribute.attribute("studyPart", (LiverDiag e) -> e.getSubject().getStudyPart())),
        SUBJECT_ID(EntityAttribute.attribute("subjectId", (LiverDiag e) -> e.getSubject().getSubjectId())),
        SUBJECT(EntityAttribute.attribute("subject", (LiverDiag e) -> e.getSubject().getSubjectCode())),
        LIVER_DIAG_INVESTIGATION(EntityAttribute.attribute("liverDiagInv", (LiverDiag e) -> e.getEvent().getLiverDiagInv())),
        LIVER_DIAG_INVESTIGATION_SPEC(EntityAttribute.attribute("liverDiagInvSpec", (LiverDiag e) -> e.getEvent().getLiverDiagInvSpec())),
        LIVER_DIAG_INVESTIGATION_DATE(EntityAttribute.attribute("liverDiagInvDate", (LiverDiag e) -> e.getEvent().getLiverDiagInvDate())),
        LIVER_DIAG_INVESTIGATION_RESULT(EntityAttribute.attribute("liverDiagInvResult", (LiverDiag e) -> e.getEvent().getLiverDiagInvResult())),
        POTENTIAL_HYS_LAW_CASE_NUM(EntityAttribute.attribute("potentialHysLawCaseNum", (LiverDiag e) -> e.getEvent().getPotentialHysLawCaseNum())),
        STUDY_DAY_LIVER_DIAG_INVESTIGATION(EntityAttribute.attribute("studyDayLiverDiagInv", LiverDiag::getStudyDayLiverDiagInv));

        @Getter
        private final EntityAttribute<LiverDiag> attribute;

        Attributes(EntityAttribute<LiverDiag> attribute) {
            this.attribute = attribute;
        }
    }
}
