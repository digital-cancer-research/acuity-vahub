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
