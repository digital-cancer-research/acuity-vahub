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

import com.acuity.visualisations.rawdatamodel.filters.CerebrovascularFilters;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Cerebrovascular;

public class CerebrovascularFilterSummaryStatistics implements FilterSummaryStatistics<Cerebrovascular> {

    private CerebrovascularFilters cerebrovascularFilters = new CerebrovascularFilters();
    private int count = 0;

    @Override
    public void accept(Object value) {
        count++;
        Cerebrovascular cerebrovascular = (Cerebrovascular) value;
        cerebrovascularFilters.getAeNumber().completeWithValue(cerebrovascular.getAeNumber());
        cerebrovascularFilters.getComment().completeWithValue(cerebrovascular.getEvent().getComment());
        cerebrovascularFilters.getTraumatic().completeWithValue(cerebrovascular.getEvent().getTraumatic());
        cerebrovascularFilters.getEventTerm().completeWithValue(cerebrovascular.getEvent().getTerm());
        cerebrovascularFilters.getEventStartDate().completeWithValue(cerebrovascular.getEvent().getStartDate());
        cerebrovascularFilters.getEventType().completeWithValue(cerebrovascular.getEvent().getEventType());
        cerebrovascularFilters.getIntraHemorrhageLoc().completeWithValue(cerebrovascular.getEvent().getIntraHemorrhageLoc());
        cerebrovascularFilters.getIntraHemorrhageOtherLoc().completeWithValue(cerebrovascular.getEvent().getIntraHemorrhageOtherLoc());
        cerebrovascularFilters.getMrsPriorStroke().completeWithValue(cerebrovascular.getEvent().getMrsPriorToStroke());
        cerebrovascularFilters.getMrsCurrVisitOr90DAfterStroke().completeWithValue(cerebrovascular.getEvent().getMrsCurrVisitOr90dAfter());
        cerebrovascularFilters.getMrsDuringStrokeHosp().completeWithValue(cerebrovascular.getEvent().getMrsDuringStrokeHosp());
        cerebrovascularFilters.getPrimaryIschemicStroke().completeWithValue(cerebrovascular.getEvent().getPrimaryIschemicStroke());
        cerebrovascularFilters.getSymptomsDuration().completeWithValue(cerebrovascular.getEvent().getSymptomsDuration());
        cerebrovascularFilters.setMatchedItemsCount(count);
    }

    @Override
    public void combine(FilterSummaryStatistics<Cerebrovascular> other) {
        CerebrovascularFilterSummaryStatistics otherCerebro = (CerebrovascularFilterSummaryStatistics) other;
        cerebrovascularFilters.getSymptomsDuration().complete(otherCerebro.cerebrovascularFilters.getSymptomsDuration());
        cerebrovascularFilters.getPrimaryIschemicStroke().complete(otherCerebro.cerebrovascularFilters.getPrimaryIschemicStroke());
        cerebrovascularFilters.getIntraHemorrhageOtherLoc().complete(otherCerebro.cerebrovascularFilters.getIntraHemorrhageOtherLoc());
        cerebrovascularFilters.getIntraHemorrhageLoc().complete(otherCerebro.cerebrovascularFilters.getIntraHemorrhageLoc());
        cerebrovascularFilters.getAeNumber().complete(otherCerebro.cerebrovascularFilters.getAeNumber());
        cerebrovascularFilters.getEventTerm().complete(otherCerebro.cerebrovascularFilters.getEventTerm());
        cerebrovascularFilters.getEventType().complete(otherCerebro.cerebrovascularFilters.getEventType());
        cerebrovascularFilters.getEventStartDate().complete(otherCerebro.cerebrovascularFilters.getEventStartDate());
        cerebrovascularFilters.getComment().complete(otherCerebro.cerebrovascularFilters.getComment());
        cerebrovascularFilters.getMrsPriorStroke().complete(otherCerebro.cerebrovascularFilters.getMrsPriorStroke());
        cerebrovascularFilters.getMrsCurrVisitOr90DAfterStroke().complete(otherCerebro.cerebrovascularFilters.getMrsCurrVisitOr90DAfterStroke());
        cerebrovascularFilters.getMrsDuringStrokeHosp().complete(otherCerebro.cerebrovascularFilters.getMrsDuringStrokeHosp());
        cerebrovascularFilters.getTraumatic().complete(otherCerebro.cerebrovascularFilters.getTraumatic());

        cerebrovascularFilters.setMatchedItemsCount(count);
    }

    @Override
    public CerebrovascularFilters getFilters() {
        return cerebrovascularFilters;
    }

    @Override
    public int count() {
        return count;
    }

    @Override
    public FilterSummaryStatistics<Cerebrovascular> build() {
        return this;
    }

    @Override
    public FilterSummaryStatistics<Cerebrovascular> newStatistics() {
        return new CerebrovascularFilterSummaryStatistics();
    }

    @Override
    public String toString() {
        return String.format(
                "%s{cerebrovascularFilters=%s}",
                this.getClass().getSimpleName(),
                this.cerebrovascularFilters.toString());
    }
}
