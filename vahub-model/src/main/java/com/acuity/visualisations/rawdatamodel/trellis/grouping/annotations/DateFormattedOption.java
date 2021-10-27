package com.acuity.visualisations.rawdatamodel.trellis.grouping.annotations;


import static com.acuity.visualisations.rawdatamodel.util.DaysUtil.DMMMY;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DateFormattedOption {
    String format() default DMMMY;
}
