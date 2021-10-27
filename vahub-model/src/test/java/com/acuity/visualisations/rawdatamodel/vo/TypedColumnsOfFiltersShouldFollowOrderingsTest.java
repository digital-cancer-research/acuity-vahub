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

package com.acuity.visualisations.rawdatamodel.vo;

import com.acuity.visualisations.rawdatamodel.util.ClassScanner;
import com.acuity.visualisations.rawdatamodel.util.Column;
import lombok.SneakyThrows;
import one.util.streamex.StreamEx;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TypedColumnsOfFiltersShouldFollowOrderingsTest {

    private static final String FILTERS_PACKAGE = "com.acuity.visualisations.rawdatamodel";
    private static final String COLUMN_NAME_DELIMITER = " ] and [ ";
    private static final String SORT_ORDERS_ARE_WRONG = "[ %s ] columns of [ %s ] type have the same sort order = %s.\n" +
            " Take a look at [ %s ] and its members";
    private static final String COLUMN_ORDER_IS_WRONG_MESSAGE = "[ %s ] columns of [ %s ] type from [ %s ] and its members contain the same ordering = %s." +
            "\nColumn order indexes are: %s ";
    private static final Predicate<? super Map.Entry<Double, List<Column>>> isOrderingEqualForDistinctColumns = doubleListEntry ->
            doubleListEntry
                    .getValue().stream()
                    .map(Column::displayName)
                    .collect(Collectors.toSet()).size() != 1;
    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test
    public void compositeClassesShouldNotHaveColumnOrderingsDuplicates() {
        Map<? extends Class<?>, List<Class<?>>> compositeClassesWihColumnAnnotations = getCompositeClassesContainingColumnAnnotation();

        compositeClassesWihColumnAnnotations.forEach(this::verifyCompositeClassColumnOrderingsAreNotDuplicated);
    }

    @Test
    public void compositeClassesShouldHaveNoDuplicationsInSortOrderOfColumns() {
        Map<? extends Class<?>, List<Class<?>>> compositeClassesContainingColumnAnnotation = getCompositeClassesContainingColumnAnnotation();

        compositeClassesContainingColumnAnnotation.forEach(this::verifyCompositeClassColumnsSortOrderingsAreNotDuplicated);

    }

    @Test
    public void subjectClassShouldHasCorrectOrderings() {
        HashMap<Column.Type, Map<Double, List<Column>>> typedOrderVsColumnMap = Stream.concat(
                Stream.of(Subject.class.getDeclaredFields()),
                Stream.of(Subject.class.getDeclaredMethods())
        )
                .map(accessibleObject -> accessibleObject.getAnnotationsByType(Column.class))
                .flatMap(Stream::of)
                .collect(Collectors.groupingBy(Column::type, HashMap::new, Collectors.groupingBy(Column::order)));

        typedOrderVsColumnMap
                .values()
                .stream()
                .flatMap(doubleListMap -> doubleListMap.entrySet().stream())
                .forEach(verifyOrderVsColumnEntriesForRightColumnOrderings(Subject.class, typedOrderVsColumnMap));

        Stream.concat(
                Stream.of(Subject.class.getDeclaredFields()),
                Stream.of(Subject.class.getDeclaredMethods())
        )
                .flatMap(accessibleObject -> Stream.of(accessibleObject.getAnnotationsByType(Column.class)))
                .filter(Column::defaultSortBy)
                .collect(Collectors.groupingBy(Column::type, HashMap::new, Collectors.groupingBy(Column::defaultSortOrder)))
                .values()
                .stream()
                .flatMap(doubleListMap -> doubleListMap.entrySet().stream())
                .forEach(verifyOrderVsColumnEntriesForRightSortOrderings(Subject.class));

    }

    @SneakyThrows
    private Map<? extends Class<?>, List<Class<?>>> getCompositeClassesContainingColumnAnnotation() {
        //we collect all classes with methods and fields having @Column annotation
        Map<String, Class<?>> classMap = StreamEx.of(ClassScanner.getAllClassesFromPackage(FILTERS_PACKAGE))
                .filter(clazz -> Stream.concat(
                        Stream.of(clazz.getDeclaredMethods()),
                        Stream.of(clazz.getDeclaredFields())
                ).anyMatch(accessibleObject -> accessibleObject.getAnnotationsByType(Column.class).length > 0))
                .collect(Collectors.toMap(Class::getName, Function.identity()));

        // we return composite class map (key - parent (wrapper) class, values - composition classes)
        return StreamEx.of(classMap.values())
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(
                                Function.identity(),
                                clazz -> StreamEx.of(clazz.getDeclaredConstructors())
                                        .flatMap(constructor -> Arrays.stream(constructor.getParameterTypes()))
                                        .filter(param -> classMap.containsKey(param.getName()))
                                        .toList()),
                        collectedListMap -> StreamEx.of(collectedListMap.entrySet())
                                .filter(entry -> entry.getValue().size() != 0)
                                .toMap(Map.Entry::getKey, Map.Entry::getValue)));
    }

    private void verifyCompositeClassColumnsSortOrderingsAreNotDuplicated(Class<?> wrapper, List<Class<?>> members) {
        HashMap<Column.Type, Map<Double, List<Column>>> orderingsVsColumnsMapForTypes =
                getOrderingsVsColumnsMapForTypes(wrapper, members, Collectors.groupingBy(Column::defaultSortOrder), Column::defaultSortBy);

        StreamEx.of(orderingsVsColumnsMapForTypes.values())
                .map(Map::entrySet)
                .flatMap(StreamEx::of)
                .filter(isOrderingEqualForDistinctColumns)
                .forEach(verifyOrderVsColumnEntriesForRightSortOrderings(wrapper));
    }


    private void verifyCompositeClassColumnOrderingsAreNotDuplicated(Class<?> wrapper, List<Class<?>> members) {
        HashMap<Column.Type, Map<Double, List<Column>>> orderVsColumnsForAllTypes = getOrderingsVsColumnsMapForTypes(wrapper,
                members, Collectors.groupingBy(Column::order), column -> true);

        StreamEx.of(orderVsColumnsForAllTypes.values())
                .forEach(doubleListMap -> doubleListMap
                        .entrySet()
                        .stream()
                        .filter(isOrderingEqualForDistinctColumns)
                        .forEach(verifyOrderVsColumnEntriesForRightColumnOrderings(wrapper, orderVsColumnsForAllTypes)
                        ));
    }


    private HashMap<Column.Type, Map<Double, List<Column>>> getOrderingsVsColumnsMapForTypes(Class<?> wrapper,
                                                                                             List<Class<?>> members,
                                                                                             Collector<Column, ?, Map<Double, List<Column>>> downwardCollector,
                                                                                             Predicate<Column> optionalFilterForCollector) {
        return StreamEx.of(members)
                .prepend(wrapper)
                .filter(clazz -> !Subject.class.equals(clazz))
                .flatMap(clazz -> Stream.concat(
                        Stream.of(clazz.getDeclaredMethods()),
                        Stream.of(clazz.getDeclaredFields())
                ))
                .flatMap(accessibleObject -> Stream.of(accessibleObject.getAnnotationsByType(Column.class)))
                .filter(optionalFilterForCollector)
                .collect(Collectors.groupingBy(Column::type, HashMap::new, downwardCollector));
    }

    private Consumer<Map.Entry<Double, List<Column>>> verifyOrderVsColumnEntriesForRightSortOrderings(Class<?> verifiedClass) {
        return sortOrderVsColumnsEntry -> {
            softly.assertThat(sortOrderVsColumnsEntry.getValue().size())
                    .withFailMessage(String.format(SORT_ORDERS_ARE_WRONG,
                            StreamEx.of(sortOrderVsColumnsEntry.getValue())
                                    .map(Column::displayName)
                                    .collect(Collectors.joining(COLUMN_NAME_DELIMITER)),
                            sortOrderVsColumnsEntry.getValue().get(0).type(),
                            sortOrderVsColumnsEntry.getKey(),
                            verifiedClass
                    ))
                    .isEqualTo(1);
        };
    }

    private Consumer<Map.Entry<Double, List<Column>>> verifyOrderVsColumnEntriesForRightColumnOrderings(Class<?> wrapper,
                                                                                                        HashMap<Column.Type, Map<Double,
                                                                                                                List<Column>>> orderVsColumnsForAllTypes) {
        return orderVsColumnEntry -> {
            softly.assertThat(orderVsColumnEntry.getValue().size())
                    .withFailMessage(
                            String.format(
                                    COLUMN_ORDER_IS_WRONG_MESSAGE,
                                    orderVsColumnEntry.getValue()
                                            .stream()
                                            .map(Column::displayName)
                                            .collect(Collectors.joining(COLUMN_NAME_DELIMITER)),
                                    orderVsColumnEntry.getValue().get(0).type(),
                                    wrapper,
                                    orderVsColumnEntry.getKey(),
                                    orderVsColumnsForAllTypes.get(orderVsColumnEntry.getValue().get(0).type())
                                            .keySet().stream()
                                            .sorted(Comparator.comparingDouble(Double::doubleValue))
                                            .collect(Collectors.toList())))
                    .isEqualTo(1);
        };
    }
}
