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

package com.acuity.visualisations.rawdatamodel.util;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;


@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
@UtilityClass
public final class Constants {

    public static final int ROUNDING_PRECISION = 2;
    public static final String FORMATTING_TWO_DECIMAL_PLACES = "%.2f";
    public static final String FORMATTING_THREE_DECIMAL_PLACES = "%.3f";
    public static final String PERCENT = "%";
    public static final String YES = "Yes";
    public static final String NO = "No";

    public static final String ALL = "All";
    public static final String NONE = "None";
    public static final String ALL_TO_LOWER_CASE = "all";
    public static final String NULL = "NULL";
    public static final String SUMMARY = "Summary";

    public static final String MISSING = "Missing";

    public static final String NOT_IMPLEMENTED = "NOT IMPLEMENTED";

    public static final BigDecimal HUNDRED = new BigDecimal(100);

    public static final String AVAILABLE_YAXIS_OPTIONS = "availableYAxisOptions";
    public static final String AVAILABLE_BAR_LINE_YAXIS_OPTIONS = "availableBarLineYAxisOptions";
    public static final String HAS_TRACKED_MUTATIONS = "hasTrackedMutations";
    public static final String TRACKED_MUTATIONS_STRING = "trackedMutationsString";

    public static final String DEFAULT_GROUP = "Default group";
    public static final String DEFAULT_GROUP_TO_LOWER_CASE = "default group";

    public static final String EMPTY = "Empty";
    public static final String NA = "N/A";

    public static final String BASELINE_FLAG_YES = "Y";
    public static final String BASELINE_FLAG_NO = "N";

    public static final int MINUTES_IN_HOUR = 60;

    public static final String DOUBLE_DASH = "--";
}
