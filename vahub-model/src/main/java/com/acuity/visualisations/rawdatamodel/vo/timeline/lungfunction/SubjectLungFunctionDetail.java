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

package com.acuity.visualisations.rawdatamodel.vo.timeline.lungfunction;

import com.acuity.visualisations.rawdatamodel.vo.timeline.SubjectSummary;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * Status detail of spirometry/lung function (visits) per measurement type for a subject, which consists of a list of LungFunctionDetailEvent.
 */
@Getter
@ToString(callSuper = true)
public final class SubjectLungFunctionDetail extends SubjectSummary implements Serializable {
    private String sex;

    private List<LungFunctionCodes> lungFunctionCodes;

    @Builder
    private SubjectLungFunctionDetail(String subjectId, String subject,
                                      List<LungFunctionCodes> codes,
                                      String sex) {
        super(subjectId, subject);
        this.lungFunctionCodes = codes;
        this.sex = sex;
    }
}
