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
import com.acuity.visualisations.rawdatamodel.statistics.overtime.EventConsumer;

public interface FilterSummaryStatistics<T> extends EventConsumer {
    @Override
    void accept(Object value);

    void combine(FilterSummaryStatistics<T> other);

    Filters<T> getFilters();

    int count();

    FilterSummaryStatistics<T> build();

    FilterSummaryStatistics<T> newStatistics();
}
