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

package com.acuity.visualisations.rawdatamodel.statistics.filters;

import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.filters.SeriousAeFilters;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.SeriousAe;

public class SeriousAeFilterSummaryStatistics implements FilterSummaryStatistics<SeriousAe> {
    private SeriousAeFilters seriousAeFilters = new SeriousAeFilters();
    private int count = 0;

    @Override
    public void accept(Object value) {
        count++;
        SeriousAe seriousAe = (SeriousAe) value;

        seriousAeFilters.getAeNumber().completeWithValue(seriousAe.getEvent().getNum());
        seriousAeFilters.getAe().completeWithValue(seriousAe.getEvent().getAe());
        seriousAeFilters.getPt().completeWithValue(seriousAe.getEvent().getPt());
        seriousAeFilters.getStartDate().completeWithValue(seriousAe.getEvent().getStartDate());
        seriousAeFilters.getEndDate().completeWithValue(seriousAe.getEvent().getEndDate());
        seriousAeFilters.getDaysFromFirstDoseToCriteria().completeWithValue(seriousAe.getDaysFromFirstDoseToCriteria());
        seriousAeFilters.getPrimaryDeathCause().completeWithValue(seriousAe.getEvent().getPrimaryDeathCause());
        seriousAeFilters.getSecondaryDeathCause().completeWithValue(seriousAe.getEvent().getSecondaryDeathCause());
        seriousAeFilters.getOtherMedication().completeWithValue(seriousAe.getEvent().getOtherMedication());
        seriousAeFilters.getCausedByOtherMedication().completeWithValue(seriousAe.getEvent().getCausedByOtherMedication());
        seriousAeFilters.getStudyProcedure().completeWithValue(seriousAe.getEvent().getStudyProcedure());
        seriousAeFilters.getCausedByStudy().completeWithValue(seriousAe.getEvent().getCausedByStudy());
        seriousAeFilters.getDescription().completeWithValue(seriousAe.getEvent().getDescription());
        seriousAeFilters.getResultInDeath().completeWithValue(seriousAe.getEvent().getResultInDeath());
        seriousAeFilters.getHospitalizationRequired().completeWithValue(seriousAe.getEvent().getHospitalizationRequired());
        seriousAeFilters.getCongenitalAnomaly().completeWithValue(seriousAe.getEvent().getCongenitalAnomaly());
        seriousAeFilters.getLifeThreatening().completeWithValue(seriousAe.getEvent().getLifeThreatening());
        seriousAeFilters.getDisability().completeWithValue(seriousAe.getEvent().getDisability());
        seriousAeFilters.getOtherSeriousEvent().completeWithValue(seriousAe.getEvent().getOtherSeriousEvent());
        seriousAeFilters.getAd().completeWithValue(seriousAe.getEvent().getAd());
        seriousAeFilters.getCausedByAD().completeWithValue(seriousAe.getEvent().getCausedByAD());
        seriousAeFilters.getAd1().completeWithValue(seriousAe.getEvent().getAd1());
        seriousAeFilters.getCausedByAD1().completeWithValue(seriousAe.getEvent().getCausedByAD1());
        seriousAeFilters.getAd2().completeWithValue(seriousAe.getEvent().getAd2());
        seriousAeFilters.getCausedByAD2().completeWithValue(seriousAe.getEvent().getCausedByAD2());
        seriousAeFilters.getBecomeSeriousDate().completeWithValue(seriousAe.getEvent().getBecomeSeriousDate());
        seriousAeFilters.getFindOutDate().completeWithValue(seriousAe.getEvent().getFindOutDate());
        seriousAeFilters.getHospitalizationDate().completeWithValue(seriousAe.getEvent().getHospitalizationDate());
        seriousAeFilters.getDischargeDate().completeWithValue(seriousAe.getEvent().getDischargeDate());

        seriousAeFilters.setMatchedItemsCount(count);
    }

    @Override
    public void combine(FilterSummaryStatistics<SeriousAe> other) {
        SeriousAeFilters otherFilters = (SeriousAeFilters) other.getFilters();

        seriousAeFilters.getAeNumber().complete(otherFilters.getAeNumber());
        seriousAeFilters.getAe().complete(otherFilters.getAe());
        seriousAeFilters.getPt().complete(otherFilters.getPt());
        seriousAeFilters.getStartDate().complete(otherFilters.getStartDate());
        seriousAeFilters.getEndDate().complete(otherFilters.getEndDate());
        seriousAeFilters.getDaysFromFirstDoseToCriteria().complete(otherFilters.getDaysFromFirstDoseToCriteria());
        seriousAeFilters.getPrimaryDeathCause().complete(otherFilters.getPrimaryDeathCause());
        seriousAeFilters.getSecondaryDeathCause().complete(otherFilters.getSecondaryDeathCause());
        seriousAeFilters.getOtherMedication().complete(otherFilters.getOtherMedication());
        seriousAeFilters.getCausedByOtherMedication().complete(otherFilters.getCausedByOtherMedication());
        seriousAeFilters.getStudyProcedure().complete(otherFilters.getStudyProcedure());
        seriousAeFilters.getCausedByStudy().complete(otherFilters.getCausedByStudy());
        seriousAeFilters.getDescription().complete(otherFilters.getDescription());
        seriousAeFilters.getResultInDeath().complete(otherFilters.getResultInDeath());
        seriousAeFilters.getHospitalizationRequired().complete(otherFilters.getHospitalizationRequired());
        seriousAeFilters.getCongenitalAnomaly().complete(otherFilters.getCongenitalAnomaly());
        seriousAeFilters.getLifeThreatening().complete(otherFilters.getLifeThreatening());
        seriousAeFilters.getDisability().complete(otherFilters.getDisability());
        seriousAeFilters.getOtherSeriousEvent().complete(otherFilters.getOtherSeriousEvent());
        seriousAeFilters.getAd().complete(otherFilters.getAd());
        seriousAeFilters.getCausedByAD().complete(otherFilters.getCausedByAD());
        seriousAeFilters.getAd1().complete(otherFilters.getAd1());
        seriousAeFilters.getCausedByAD1().complete(otherFilters.getCausedByAD1());
        seriousAeFilters.getAd2().complete(otherFilters.getAd2());
        seriousAeFilters.getCausedByAD2().complete(otherFilters.getCausedByAD2());
        seriousAeFilters.getBecomeSeriousDate().complete(otherFilters.getBecomeSeriousDate());
        seriousAeFilters.getFindOutDate().complete(otherFilters.getFindOutDate());
        seriousAeFilters.getHospitalizationDate().complete(otherFilters.getHospitalizationDate());
        seriousAeFilters.getDischargeDate().complete(otherFilters.getDischargeDate());

        count += other.count();
        seriousAeFilters.setMatchedItemsCount(count);
    }

    @Override
    public Filters<SeriousAe> getFilters() {
        return seriousAeFilters;
    }

    @Override
    public int count() {
        return count;
    }

    @Override
    public FilterSummaryStatistics<SeriousAe> build() {
        return this;
    }

    @Override
    public FilterSummaryStatistics<SeriousAe> newStatistics() {
        return new SeriousAeFilterSummaryStatistics();
    }

    @Override
    public String toString() {
        return String.format(
                "%s{filters=%s}",
                this.getClass().getSimpleName(),
                this.seriousAeFilters.toString());
    }
}
