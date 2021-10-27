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

import com.acuity.va.security.acl.domain.Datasets;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.acuity.visualisations.rawdatamodel.util.DaysUtil.YMD_T_HMS;

/**
 * Created by knml167 on 5/23/2017.
 * Use this annotation on methods or columns of VO classes
 * or {@link com.acuity.visualisations.rawdatamodel.vo.wrappers.EventWrapper} subclasses to determine DoD/SSV column model
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(value = Columns.class)
public @interface Column {

    /**
     * Determines column key name for column (i.e. aeNumber)
     * */
    String columnName() default "";
    /**
     * Determines the order rank for column
     * */
    double order() default Integer.MAX_VALUE;
    /**
     * Determines the display name for column
     * */
    String displayName();
    /**
     * Determines column type:
     * DOD - details on demand table only,
     * SSV - single subject view only
     * */
    Type type() default Type.DOD;

    DatasetType datasetType() default DatasetType.ACUITY_DETECT;

    String dateFormat() default YMD_T_HMS;

    /**
     * Determines if by default sorting supposed to be by the column
     */
    boolean defaultSortBy() default false;

    /**
     * Considered in case of defaultSortBy = true
     * Determines if by default sorting should be reversed
     */
    boolean defaultSortReversed() default false;

    /**
     * Considered in case of defaultSortBy = true
     * Makes sense if table by default sorted by several columns.
     * Determines the order of sorting
     */
    double defaultSortOrder() default 0;

    enum Type {
        DOD,
        SSV,
        DEMOGRAPHY,
        STUDY_INFO,
        AML, // Azure Machine Learning
        CBIO
    }

    enum DatasetType {
        ACUITY,
        DETECT,
        ACUITY_DETECT;

        public static DatasetType fromDatasets(Datasets datasets) {
            if (datasets.isDetectType()) {
                return DETECT;
            } else if (datasets.isAcuityType()) {
                return ACUITY;
            } else {
                throw new IllegalArgumentException("Datasets can only be of Acuity or Detect type");
            }
        }
    }
}
