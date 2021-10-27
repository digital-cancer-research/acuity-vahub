package com.acuity.visualisations.rawdatamodel.aspect;

import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author knml167
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ValidateChartOptions {
    ChartGroupByOptions.ChartGroupBySetting[] required() default {};
    ChartGroupByOptions.ChartGroupBySetting[] optional() default {};
}
