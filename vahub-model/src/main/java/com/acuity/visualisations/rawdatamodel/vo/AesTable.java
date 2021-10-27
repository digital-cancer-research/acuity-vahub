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

package com.acuity.visualisations.rawdatamodel.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class AesTable implements Serializable {

    private String term;
    private String grade;
    private String treatmentArm;
    // subject per grade/severity term and arm
    private Integer subjectCountPerGrade;
    // subjects per term and arm
    private Integer subjectCountPerTerm;
    // subject on the arm
    private Integer subjectCountPerArm;
    private Integer noIncidenceCount;

    public Integer getNoIncidenceCount() {
        if (subjectCountPerArm != null && subjectCountPerTerm != null) {
            return subjectCountPerArm - subjectCountPerTerm;
        } else {
            return 0;
        }
    }
}
