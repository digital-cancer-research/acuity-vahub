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


import org.apache.commons.math3.util.Precision;

import java.util.Optional;
import java.util.OptionalDouble;

import static com.acuity.visualisations.rawdatamodel.util.Constants.ROUNDING_PRECISION;

public final class ReferenceRangeUtil {

    private ReferenceRangeUtil() {
    }

    public static OptionalDouble timesReferenceRange(Double value, Double refRange) {
        if (value == null || refRange == null || refRange == 0) {
            return OptionalDouble.empty();
        }

        double timesRefRange = value / refRange;
        return OptionalDouble.of(Precision.round(timesRefRange, ROUNDING_PRECISION));
    }

    public static Optional<String> outOfReferenceRange(Double value, Double lowerRefRange, Double upperRefRange) {
        if (value == null || lowerRefRange == null || upperRefRange == null) {
            return Optional.of("false");
        }

        boolean outOfRefRange = value < lowerRefRange || value > upperRefRange;
        return Optional.of(outOfRefRange ? "true" : "false");
    }

    public static OptionalDouble referenceRangeNormalisedValue(Double value, Double lowerRefRange, Double upperRefRange) {
        if (value == null || lowerRefRange == null || upperRefRange == null || lowerRefRange.equals(upperRefRange)) {
            return OptionalDouble.empty();
        }

        double refRangeNorm = (value - lowerRefRange) / (upperRefRange - lowerRefRange);
        return OptionalDouble.of(Precision.round(refRangeNorm, ROUNDING_PRECISION));
    }
}
