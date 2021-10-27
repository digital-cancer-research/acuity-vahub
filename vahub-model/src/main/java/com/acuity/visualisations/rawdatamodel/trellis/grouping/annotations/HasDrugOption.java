package com.acuity.visualisations.rawdatamodel.trellis.grouping.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This option is used if you need to group values by some param using Drug
 * Frontend needs to provide drug selection mechanism in case of correct behaviour of this option
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface HasDrugOption {
}
