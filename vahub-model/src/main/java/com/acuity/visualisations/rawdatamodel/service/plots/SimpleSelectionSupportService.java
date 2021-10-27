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
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.HasStringId;
import com.acuity.visualisations.rawdatamodel.vo.HasSubject;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;

import java.util.List;

import static java.util.stream.Collectors.toSet;

public interface SimpleSelectionSupportService<T extends HasStringId & HasSubject, G extends Enum<G> & GroupByOption<T>>
        extends SimpleSelectionMatchingService<T, G> {

    default SelectionDetail getSelectionDetails(FilterResult<T> filtered, ChartSelection<T, G, ChartSelectionItem<T, G>> selection) {

        final List<T> matchedItems = getMatchedItems(filtered.getFilteredEvents(), selection);

        return SelectionDetail.builder()
                .eventIds(matchedItems.stream().map(T::getId).collect(toSet()))
                //don't use T::getSubjectId here, see https://stackoverflow.com/questions/27031244/lambdaconversionexception-with-generics-jvm-bug
                .subjectIds(matchedItems.stream().map(t -> t.getSubjectId()).distinct().collect(toSet()))
                .totalEvents(filtered.getAllEvents().size())
                .totalSubjects(filtered.getPopulationFilterResult().size())
                .build();
    }
}
