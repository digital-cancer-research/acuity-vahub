package com.acuity.visualisations.rawdatamodel.util;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Stream;

import static org.apache.commons.lang3.reflect.FieldUtils.getAllFieldsList;

public final class AnnotationUtil {

    private AnnotationUtil() {

    }

    /**
     * Gets all fields and methods of the given class that are annotated with
     * the Column annotation with specific DatasetType and Table type.
     * Can handle repeatable annotations.
     *
     * @param key triple of Class, Dataset type and Column type to gather metadata for
     * @return a stream of AnnotationWithFieldAndReader (possibly empty).
     * @throws IllegalArgumentException if the class is {@code null}
     */
    public static Stream<AnnotationWithFieldAndReader<Column>> getAnnotatedMethods(MetadataCacheKey key) {
        Validate.isTrue(key.getEntity() != null, "The class must not be null");
        Stream<AnnotationWithFieldAndReader<Column>> fieldsAnnotated = getAllFieldsList(key.getEntity()).stream()
                .map(field -> AnnotationWithFieldAndReader.<Column>builder()
                        .field(field)
                        .fieldReader(getGetter(key.getEntity(), field))
                        .annotationObject(getColumnAnnotationFor(field, key.getDatasetType(), key.getColumnType()))
                        .build());
        Stream<AnnotationWithFieldAndReader<Column>> methodsAnnotated = Arrays.stream(key.getEntity().getMethods())
                .map(method -> AnnotationWithFieldAndReader.<Column>builder()
                        .fieldReader(method)
                        .annotationObject(getColumnAnnotationFor(method, key.getDatasetType(), key.getColumnType()))
                        .build());
        return Stream.concat(fieldsAnnotated, methodsAnnotated)
                .filter(a -> a.getAnnotationObject() != null);
    }

    /**
     * Returns method to access provided field. Typically getter.
     *
     * @param clazz get method from this class
     * @param field field from which the method gets value
     * @return method
     */
    public static Method getGetter(Class<?> clazz, Field field) {
        String fieldName = field.getName();
        Method reader = MethodUtils.getAccessibleMethod(clazz, "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1));
        if (reader == null) {
            reader = MethodUtils.getAccessibleMethod(clazz, "is" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1));
        }
        return reader;
    }

    private static Column getColumnAnnotationFor(AnnotatedElement elem, Column.DatasetType datasetType, Column.Type tableType) {
        return Arrays.stream(elem.getAnnotationsByType(Column.class))
                .filter(a -> a.type() == tableType
                        && (a.datasetType() == datasetType
                        || a.datasetType() == Column.DatasetType.ACUITY_DETECT))
                .findFirst()
                .orElse(null);
    }
}
