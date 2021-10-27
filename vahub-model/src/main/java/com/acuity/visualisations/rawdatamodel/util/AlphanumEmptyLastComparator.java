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

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static com.acuity.visualisations.rawdatamodel.util.Attributes.DEFAULT_EMPTY_VALUE;

/**
 * Created by knml167 on 6/27/2017.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AlphanumEmptyLastComparator extends AlphanumComparator<String> {

    private static final AlphanumEmptyLastComparator INSTANCE = new AlphanumEmptyLastComparator();

    public static AlphanumEmptyLastComparator getInstance() {
        return INSTANCE;
    }

    @Override
    public int compare(String o1, String o2) {
        if (DEFAULT_EMPTY_VALUE.equalsIgnoreCase(o1) && DEFAULT_EMPTY_VALUE.equalsIgnoreCase(o2)) {
            return 0;
        }
        if (DEFAULT_EMPTY_VALUE.equalsIgnoreCase(o1)) {
            return 1;
        }
        if (DEFAULT_EMPTY_VALUE.equalsIgnoreCase(o2)) {
            return -1;
        }
        return super.compare(o1, o2);
    }
}
