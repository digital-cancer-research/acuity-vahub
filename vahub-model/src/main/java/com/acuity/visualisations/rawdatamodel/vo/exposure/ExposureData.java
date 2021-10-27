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

package com.acuity.visualisations.rawdatamodel.vo.exposure;

import com.acuity.visualisations.rawdatamodel.vo.wrappers.Exposure;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

import static com.acuity.visualisations.rawdatamodel.util.Attributes.defaultNullableValue;

@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Getter
public class ExposureData implements Serializable {

    private String subject;
    private String treatmentCycle;
    private String analyte;
    private String visit;
    private String dose;
    private String day;

    public ExposureData(SubjectCycle subjectCycle, Exposure event) {
        this.subject = subjectCycle.getSubject();
        this.treatmentCycle = defaultNullableValue(subjectCycle.getCycle().getTreatmentCycle()).toString();
        this.analyte = defaultNullableValue(subjectCycle.getCycle().getAnalyte()).toString();
        this.visit = defaultNullableValue(subjectCycle.getCycle().getVisit()).toString();
        this.dose = defaultNullableValue(event.getEvent().getTreatment()).toString();
        this.day = defaultNullableValue(event.getEvent().getProtocolScheduleDay()).toString();
    }
}
