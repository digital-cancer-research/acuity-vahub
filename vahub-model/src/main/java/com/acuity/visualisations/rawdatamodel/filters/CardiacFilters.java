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

package com.acuity.visualisations.rawdatamodel.filters;

import com.acuity.visualisations.rawdatamodel.util.CombinedQueryBuilder;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Cardiac;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.VisitNumber;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.googlecode.cqengine.query.Query;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Collection;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class CardiacFilters extends Filters<Cardiac> {

    @JsonIgnore
    public static CardiacFilters empty() {
        return new CardiacFilters();
    }

    protected SetFilter<String> measurementCategory = new SetFilter<>();
    protected SetFilter<String> measurementName = new SetFilter<>();
    protected RangeFilter<Double> resultValue = new RangeFilter<>();
    protected SetFilter<String> resultUnit = new SetFilter<>();
    protected SetFilter<String> clinicallySignificant = new SetFilter<>();
    protected DateRangeFilter measurementTimePoint = new DateRangeFilter();
    protected RangeFilter<Integer> daysOnStudy = new RangeFilter<>();
    protected RangeFilter<Double> analysisVisit = new RangeFilter<>();
    protected RangeFilter<Double> visitNumber = new RangeFilter<>();
    protected RangeFilter<Double> baselineValue = new RangeFilter<>();
    protected SetFilter<String> baselineFlag = new SetFilter<>();
    protected SetFilter<String> studyPeriods = new SetFilter<>();
    protected SetFilter<String> protocolScheduleTimepoint = new SetFilter<>();
    protected SetFilter<String> comment = new SetFilter<>();
    protected SetFilter<String> wave = new SetFilter<>();
    protected SetFilter<String> method = new SetFilter<>();
    protected SetFilter<String> sttChanges = new SetFilter<>();
    protected SetFilter<String> stSegment = new SetFilter<>();
    protected SetFilter<String> heartRhythm = new SetFilter<>();
    protected SetFilter<String> heartRhythmOther = new SetFilter<>();
    protected SetFilter<String> extraSystoles = new SetFilter<>();
    protected SetFilter<String> specifyExtraSystoles = new SetFilter<>();
    protected SetFilter<String> reasonAbnormalConduction = new SetFilter<>();
    protected SetFilter<String> conduction = new SetFilter<>();
    protected SetFilter<String> typeOfConduction = new SetFilter<>();
    protected SetFilter<String> sinusRhythm = new SetFilter<>();
    protected SetFilter<String> reasonNoSinusRhythm = new SetFilter<>();
    protected SetFilter<String> atrialFibrillation = new SetFilter<>();
    protected RangeFilter<Double> changeFromBaselineValue = new RangeFilter<>();
    protected RangeFilter<Double> percentageChangeFromBaselineValue = new RangeFilter<>();
    protected RangeFilter<Integer> beatGroupNumber = new RangeFilter<>();
    protected RangeFilter<Integer> beatNumberWithinBeatGroup = new RangeFilter<>();
    protected RangeFilter<Integer> numberOfBeatsInAverageBeat = new RangeFilter<>();
    protected RangeFilter<Double> beatGroupLengthInSec = new RangeFilter<>();

    protected DateRangeFilter dateOfLastDose = new DateRangeFilter();
    protected SetFilter<String> lastDoseAmount = new SetFilter<>();

    @Override
    public Query<Cardiac> getQuery(Collection<String> subjectIds) {
        return new CombinedQueryBuilder<Cardiac>(Cardiac.class)
                .add(getFilterQuery(Cardiac.Attributes.SUBJECT_ID, new SetFilter<>(subjectIds)))
                .add(getFilterQuery(Cardiac.Attributes.MEASUREMENT_NAME, measurementName))
                .add(getFilterQuery(Cardiac.Attributes.MEASUREMENT_CATEGORY, measurementCategory))
                .add(getFilterQuery(Cardiac.Attributes.RESULT_VALUE, resultValue))
                .add(getFilterQuery(Cardiac.Attributes.RESULT_UNIT, resultUnit))
                .add(getFilterQuery(Cardiac.Attributes.STUDY_PERIOD, studyPeriods))
                .add(getFilterQuery(Cardiac.Attributes.CLINICALLY_SIGNIFICANT, clinicallySignificant))
                .add(getFilterQuery(Cardiac.Attributes.MEASUREMENT_TIME_POINT, measurementTimePoint))
                .add(getFilterQuery(Cardiac.Attributes.ANALYSIS_VISIT, analysisVisit))
                .add(getFilterQuery(Cardiac.Attributes.DAYS_ON_STUDY, daysOnStudy))
                .add(getFilterQuery(Cardiac.Attributes.VISIT_NUMBER, VisitNumber.wrapVisitNumberFilter(visitNumber)))
                .add(getFilterQuery(Cardiac.Attributes.BASELINE_VALUE, baselineValue))
                .add(getFilterQuery(Cardiac.Attributes.BASELINE_FLAG, baselineFlag))
                .add(getFilterQuery(Cardiac.Attributes.CHANGE_FROM_BASELINE, changeFromBaselineValue))
                .add(getFilterQuery(Cardiac.Attributes.PERCENT_CHANGE_FROM_BASELINE, percentageChangeFromBaselineValue))
                .add(getFilterQuery(Cardiac.Attributes.BEAT_GROUP_NUMBER, beatGroupNumber))
                .add(getFilterQuery(Cardiac.Attributes.BEAT_NUMBER_WITHIN_BEAT_GROUP, beatNumberWithinBeatGroup))
                .add(getFilterQuery(Cardiac.Attributes.BEATS_NUMBER_AVG_BEAT, numberOfBeatsInAverageBeat))
                .add(getFilterQuery(Cardiac.Attributes.BEAT_GROUP_LENGTH_IN_SEC, beatGroupLengthInSec))
                .add(getFilterQuery(Cardiac.Attributes.COMMENT, comment))
                .add(getFilterQuery(Cardiac.Attributes.WAVE, wave))
                .add(getFilterQuery(Cardiac.Attributes.METHOD, method))
                .add(getFilterQuery(Cardiac.Attributes.PROTOCOL_SCHEDULE_TIMEPOINT, protocolScheduleTimepoint))
                .add(getFilterQuery(Cardiac.Attributes.REASON_ABNORMAL_CONDUCTION, reasonAbnormalConduction))
                .add(getFilterQuery(Cardiac.Attributes.STT_CHANGES, sttChanges))
                .add(getFilterQuery(Cardiac.Attributes.ST_SEGMENT, stSegment))
                .add(getFilterQuery(Cardiac.Attributes.HEART_RHYTHM, heartRhythm))
                .add(getFilterQuery(Cardiac.Attributes.HEART_RHYTHM_OTHER, heartRhythmOther))
                .add(getFilterQuery(Cardiac.Attributes.EXTRA_SYSTOLES, extraSystoles))
                .add(getFilterQuery(Cardiac.Attributes.SPECIFY_EXTRA_SYSTOLES, specifyExtraSystoles))
                .add(getFilterQuery(Cardiac.Attributes.CONDUCTION, conduction))
                .add(getFilterQuery(Cardiac.Attributes.TYPE_OF_CONDUCTION, typeOfConduction))
                .add(getFilterQuery(Cardiac.Attributes.SINUS_RHYTHM, sinusRhythm))
                .add(getFilterQuery(Cardiac.Attributes.REASON_NO_SINUS_RHYTHM, reasonNoSinusRhythm))
                .add(getFilterQuery(Cardiac.Attributes.ATRIAL_FIBRILLATION, atrialFibrillation))
                .add(getFilterQuery(Cardiac.Attributes.LAST_DOSE_DATE, dateOfLastDose))
                .add(getFilterQuery(Cardiac.Attributes.LAST_DOSE_AMOUNT, lastDoseAmount))
                .build();
    }
}
