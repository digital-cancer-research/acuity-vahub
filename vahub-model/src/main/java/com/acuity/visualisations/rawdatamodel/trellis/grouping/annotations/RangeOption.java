package com.acuity.visualisations.rawdatamodel.trellis.grouping.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RangeOption {
    RangeOptionType value();

    enum RangeOptionType {
        DATE, LONG, DOUBLE, MAP_STRING_DOUBLE
    }

}
