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

package com.acuity.visualisations.rawdatamodel.vo.plots;

import com.acuity.visualisations.rawdatamodel.util.Attributes;
import com.acuity.visualisations.rawdatamodel.util.DaysUtil;
import lombok.Value;

@Value
public class BarChartDateFormattedOption implements Comparable<BarChartDateFormattedOption> {
    private String date;
    private String format;

    public static final BarChartDateFormattedOption EMPTY = new BarChartDateFormattedOption(null, null);


    @Override
    public int compareTo(BarChartDateFormattedOption o) {
        if (this.date == null && o.date == null) {
            return 0;
        }
        if (this.date == null) {
            return 1;
        }
        if (o.date == null) {
            return -1;
        }
        return DaysUtil.toDate(this.date, this.format).compareTo(DaysUtil.toDate(o.date, o.format));
    }

    @Override
    public String toString() {
        if (this.date == null) {
            return Attributes.DEFAULT_EMPTY_VALUE;
        }
        return date;
    }
}
