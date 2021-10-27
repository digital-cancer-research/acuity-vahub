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

import com.acuity.visualisations.rawdatamodel.vo.timeline.day.hour.DateDayHour;
import com.acuity.visualisations.rawdatamodel.vo.timeline.SubjectSummary;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Status summary for a subject
 *
 * @author ksnd199
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class SubjectStatusSummary extends SubjectSummary {

    protected DateDayHour firstVisit;
    protected DateDayHour lastVisit;
    protected DateDayHour firstTreatment;
    protected DateDayHour randomisation;
    protected DateDayHour lastTreatment;
    protected DateDayHour completion; // study end date for detect
    protected DateDayHour death;
    protected DateDayHour ongoing;
    protected DateDayHour cutoff;
    protected DateDayHour endDate; // calculated study participation end date
    protected List<String> drugs;

    private List<StudyPhase> phases = newArrayList();

    public void calculatePhases() {
        //noinspection StatementWithEmptyBody
        if (endDate != null) {
            StudyPhase studyPhase = new StudyPhase(StudyPhase.PhaseType.ON_STUDY_DRUG);
            studyPhase.setStart(firstTreatment);
            studyPhase.setEnd(endDate);
            studyPhase.setOngoing(false);
            phases.add(studyPhase);
        } else {
            // first visit not happened yet, add no phases
        }
    }

    // We tried to use lombok 1.18.4 version which allows use @SuperBuilder to works with fields from superclasses.
    // But in this case typescript-generator-maven-plugin fails with NPE during the project build.
    @Builder
    @SuppressWarnings("checkstyle:parameternumber")
    public SubjectStatusSummary(String subjectId,
                                String subject,
                                DateDayHour firstVisit,
                                DateDayHour lastVisit,
                                DateDayHour firstTreatment,
                                DateDayHour randomisation,
                                DateDayHour lastTreatment,
                                DateDayHour completion,
                                DateDayHour death,
                                DateDayHour ongoing,
                                DateDayHour cutoff,
                                DateDayHour endDate,
                                List<String> drugs,
                                List<StudyPhase> phases) {
        super(subjectId, subject);
        this.firstVisit = firstVisit;
        this.lastVisit = lastVisit;
        this.firstTreatment = firstTreatment;
        this.randomisation = randomisation;
        this.lastTreatment = lastTreatment;
        this.completion = completion;
        this.death = death;
        this.ongoing = ongoing;
        this.cutoff = cutoff;
        this.endDate = endDate;
        this.drugs = drugs;
        this.phases = phases;
    }
}
