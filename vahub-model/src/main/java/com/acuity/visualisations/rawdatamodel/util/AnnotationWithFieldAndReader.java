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
