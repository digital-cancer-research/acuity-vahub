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

package com.acuity.visualisations.rawdatamodel.service.timeline.data;

import com.acuity.visualisations.rawdatamodel.vo.HasStartEndDate;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * It represents list of events happes in the same period of time.
 */
@Data
public class TimelineBucket<T extends HasStartEndDate> implements HasStartEndDate {
    private Date startDate;
    private Date endDate;
    private List<T> items = new ArrayList<>();
    private boolean ongoing;

    public TimelineBucket(Date startDate) {
        this.startDate = startDate;
    }

    public void addItems(Collection<T> items) {
        this.items.addAll(items);
    }
}
