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
