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

package com.acuity.visualisations.rawdatamodel.vo.timeline.statussummary;

import com.acuity.visualisations.rawdatamodel.vo.HasStartEndDate;
import com.acuity.visualisations.rawdatamodel.vo.timeline.EventInterval;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

/**
 * Status phase event, start and end of phase event and its phase type
 *
 * @author ksnd199
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class StudyPhase extends EventInterval implements HasStartEndDate {

    @Override
    public Date getEndDate() {
        return getEnd().getDate();
    }

    @Override
    public Date getStartDate() {
        return getStart().getDate();
    }

    public enum PhaseType {
        RANDOMISED_DRUG,
        RUN_IN,
        ON_STUDY_DRUG; // no randomisation
    }
    
    private PhaseType phaseType;
}
