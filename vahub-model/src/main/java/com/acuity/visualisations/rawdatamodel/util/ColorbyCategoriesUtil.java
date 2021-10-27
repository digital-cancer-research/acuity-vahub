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

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class ColorbyCategoriesUtil {
    private ColorbyCategoriesUtil() { }
    public static String getDatasetColorByOption(Datasets datasets, Object colorByOption) {
        String datasetColorByOption = Stream.of(Optional.ofNullable(datasets).map(Datasets::getIdsAsString),
                Optional.ofNullable(colorByOption.toString()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.joining("_"));
        return datasetColorByOption;
    }
}
