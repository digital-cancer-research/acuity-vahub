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

package com.acuity.visualisations.rawdatamodel.service.ae.chord;

import com.acuity.visualisations.rawdatamodel.vo.wrappers.Ae;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.acuity.visualisations.rawdatamodel.util.DaysUtil.getMaxDate;
import static com.acuity.visualisations.rawdatamodel.util.DaysUtil.getMinDate;

@Getter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
class AesMergingWrapper {

    private List<Ae> aes = new ArrayList<>();
    private String subjectCode;
    private Date startDate;
    private Date endDate;

    AesMergingWrapper(Ae adverseEvent) {
        this.startDate = adverseEvent.getStartDate();
        this.endDate = adverseEvent.getEndDate();
        this.subjectCode = adverseEvent.getSubjectCode();
        aes.add(adverseEvent);
    }

    AesMergingWrapper(AesMergingWrapper wrapper1, AesMergingWrapper wrapper2) {
        this.startDate = getMinDate(wrapper1.getStartDate(), wrapper2.getStartDate());
        this.endDate = getMaxDate(wrapper1.getEndDate(), wrapper2.getEndDate());
        this.subjectCode = wrapper1.getSubjectCode();
        aes.addAll(wrapper1.getAes());
        aes.addAll(wrapper2.getAes());
    }
}
