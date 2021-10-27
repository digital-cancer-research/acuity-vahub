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

package com.acuity.visualisations.rawdatamodel.service.plots;

import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItem;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.TumourTherapyGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.TumourTherapy;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.EventWrapper;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

/**
 * Class that enables selection support for TumourTherapy class. TumourTherapy is an aggregating class that has no Id,
 * but can contain several objects of Chemotherapy or Radiotherapy and Dosing.
 */
@Service
public class TumourColumnRangeSelectionSupportService implements SimpleSelectionSupportService<TumourTherapy, TumourTherapyGroupByOptions> {

    /**
     * This method is not supposed to be used, because TumourTherapy normally has no FilterResult.
     * Override the basic method just in case.
     */
    @Override
    public SelectionDetail getSelectionDetails(FilterResult<TumourTherapy> filtered, ChartSelection<TumourTherapy, TumourTherapyGroupByOptions,
            ChartSelectionItem<TumourTherapy, TumourTherapyGroupByOptions>> selection) {

        return getSelectionDetails(filtered.getFilteredResult(), selection);
    }

    public SelectionDetail getSelectionDetails(Collection<TumourTherapy> therapies, ChartSelection<TumourTherapy, TumourTherapyGroupByOptions,
            ChartSelectionItem<TumourTherapy, TumourTherapyGroupByOptions>> selection) {

        final List<TumourTherapy> matchedItems = getMatchedItems(therapies, selection);

        // SelectionDetail's totalEvents is not used for now, so leave it blank to avoid performing unnecessary operations
        return SelectionDetail.builder()
                // Collect events from TumourTherapy's previousChemoTherapies, previousRadioTherapies and doses into one set.
                // Don't use com.google.common.collect.Streams.concat here, it produces weird
                // java.lang.NoSuchMethodError: com.google.common.math.LongMath.saturatedAdd(JJ)J
                .eventIds(matchedItems.stream()
                        .flatMap(t -> Stream.of(t.getPreviousChemoTherapies().stream().map(EventWrapper::getId),
                                t.getPreviousRadioTherapies().stream().map(EventWrapper::getId),
                                t.getDoses().stream().map(EventWrapper::getId))
                                .flatMap(eventsStream -> eventsStream)).collect(toSet()))
                .subjectIds(matchedItems.stream().map(TumourTherapy::getSubjectId).collect(toSet()))
                .totalSubjects((int) therapies.stream().map(TumourTherapy::getSubjectId).distinct().count())
                .build();
    }
}
