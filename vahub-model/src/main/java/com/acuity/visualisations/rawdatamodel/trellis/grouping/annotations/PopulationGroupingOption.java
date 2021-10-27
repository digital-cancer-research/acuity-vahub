package com.acuity.visualisations.rawdatamodel.trellis.grouping.annotations;

import com.acuity.visualisations.rawdatamodel.trellis.grouping.PopulationGroupByOptions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by knml167 on 8/24/2017. <br>
 * When annotating attribute enum with this,
 * option should have corresponding population group by option
 *
 * <br><b>IMPORTANT! Source and target attributes should return equal results for equal input</b>
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PopulationGroupingOption {

    /**
     * @return Corresponding population group by option
     */
    PopulationGroupByOptions value();
}
