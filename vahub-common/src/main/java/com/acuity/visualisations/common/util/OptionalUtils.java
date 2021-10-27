package com.acuity.visualisations.common.util;

import lombok.experimental.UtilityClass;

import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;

@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
@UtilityClass
public class OptionalUtils {
    public Optional<Integer> intToOptional(OptionalInt optionalInt) {
        return optionalInt.isPresent()
                ? Optional.of(optionalInt.getAsInt())
                : Optional.empty();
    }

    public Optional<Double> doubleToOptional(OptionalDouble optionalDouble) {
        return optionalDouble.isPresent()
                ? Optional.of(optionalDouble.getAsDouble())
                : Optional.empty();
    }
}
