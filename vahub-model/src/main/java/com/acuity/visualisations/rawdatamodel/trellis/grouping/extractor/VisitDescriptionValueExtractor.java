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

package com.acuity.visualisations.rawdatamodel.trellis.grouping.extractor;

import com.acuity.visualisations.rawdatamodel.trellis.grouping.VisitDescription;

public class VisitDescriptionValueExtractor implements ValueExtractor<VisitDescription, Integer> {
    /**
     * Returns an {@link Integer} number that's situated on the second position in visitDescription string (e.g. 'visit 2')
     * or {@link Integer#MAX_VALUE} if there is not a number (e.g. 'Baseline').
     *
     * @param object visit description object to extract number values from description string
     * @return a list within two integer to compare {@param object}
     */
    @Override
    public Integer extractFrom(VisitDescription object) {
        String[] descWords = object.getVisitDescription().split(" ");
        String value = descWords.length > 1 ? descWords[1] : null;
        int intValue;
        try {
            intValue = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            intValue = Integer.MAX_VALUE;
        }
        return intValue;
    }
}
