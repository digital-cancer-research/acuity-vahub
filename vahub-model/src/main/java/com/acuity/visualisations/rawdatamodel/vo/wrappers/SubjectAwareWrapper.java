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
import com.acuity.visualisations.rawdatamodel.vo.HasStringId;
import com.acuity.visualisations.rawdatamodel.vo.HasSubject;
import com.acuity.visualisations.rawdatamodel.vo.HasSubjectId;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.Validate;

import static com.acuity.visualisations.rawdatamodel.util.DodUtil.STUDY_ID;
import static com.acuity.visualisations.rawdatamodel.util.DodUtil.STUDY_PART;
import static com.acuity.visualisations.rawdatamodel.util.DodUtil.SUBJECT_ID;

/**
 * Created by knml167 on 6/9/2017.
 */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SubjectAwareWrapper<T extends HasStringId & HasSubjectId> extends EventWrapper<T> implements HasSubject {
    @Getter
    protected Subject subject;

    public SubjectAwareWrapper(T event, Subject subject) {
        super(event);
        Validate.notNull(subject);
        this.subject = subject;
    }

    @Column(columnName = STUDY_ID, order = -3, displayName = "Study id")
    @Column(columnName = STUDY_ID, order = -3, displayName = "Study id", type = Column.Type.AML)
    @Column(columnName = STUDY_ID, order = -3, displayName = "Study id", type = Column.Type.CBIO)
    public String getStudyId() {
        return getSubject().getClinicalStudyCode();
    }

    @Override
    @Column(columnName = STUDY_PART, order = -2, displayName = "Study part")
    @Column(columnName = STUDY_PART, order = -2, displayName = "Study part", type = Column.Type.AML)
    public String getStudyPart() {
        return getSubject().getStudyPart();
    }

    @Column(columnName = SUBJECT_ID, order = -1, displayName = "Subject id", defaultSortBy = true)
    @Column(columnName = SUBJECT_ID, order = -1, displayName = "Subject id", type = Column.Type.AML)
    public String getRawSubjectCode() {
        return subject.getRawSubject();
    }

    @Override
    public String getSubjectCode() {
        return subject.getSubjectCode();
    }

    @Override
    public String getSubjectId() {
        return getSubject().getId();
    }
}
