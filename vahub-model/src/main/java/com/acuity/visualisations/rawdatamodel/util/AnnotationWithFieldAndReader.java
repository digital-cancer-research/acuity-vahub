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

import com.acuity.visualisations.rawdatamodel.service.dod.CommonTableService;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Value class to transfer reflection info for annotated method or field.
 * Used to simplify stream operations in {@link CommonTableService}.
 * @param <T> annotation to work with. Typically {@link Column}
 */
@Getter
@EqualsAndHashCode
@Builder(toBuilder = true)
public class AnnotationWithFieldAndReader<T extends Annotation> {
    private Field field;
    /**
     * Method to get the value from {@code field} field. Typically getter,
     * or bare method if it is annotated with {@link T} annotation. In the
     * latter case {@code field} field will be {@code null}.
     */
    private Method fieldReader;
    private T annotationObject;
}
