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

import org.apache.commons.lang3.ObjectUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import static com.acuity.visualisations.rawdatamodel.util.Attributes.DEFAULT_EMPTY_VALUE;

/**
 *
 * @author ksnd199
 */
public final class ObjectUtil {

    private ObjectUtil() {
    }

    public static <T> T nvl(T a, T b) {
        return DEFAULT_EMPTY_VALUE.equals(a) ? b : ObjectUtils.defaultIfNull(a, b);
    }

    public static String emptyIfNull(String a) {
        return ObjectUtils.defaultIfNull(a, DEFAULT_EMPTY_VALUE);
    }

    public static boolean anyNull(Object... objects) {
        return Stream.of(objects).anyMatch(Objects::isNull);
    }

    public static boolean allNull(Object... objects) {
        return Stream.of(objects).allMatch(Objects::isNull);
    }

    public static <K> boolean keysEquals(Map<K, Object> o1, Map<K, Object> o2) {
        return (o1.size() == 0 && o2.size() == 0) || (o1.size() == o2.size()
                && o1.entrySet().stream().allMatch(
                e -> {
                    final Object v1 = e.getValue() == null ? null : e.getValue().toString();
                    final Object o = o2.get(e.getKey());
                    final Object v2 = o == null ? null : o.toString();
                    return Objects.equals(v1, v2);
                }
        ));
    }

    public static class MapWrapper<K> {
        private final Map<K, Object> map;

        public MapWrapper(Map<K, Object> map) {
            this.map = map;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            MapWrapper that = (MapWrapper) o;
            return keysEquals(map, that.map);
        }

        @Override
        public int hashCode() {
            if (map == null) {
                return 0;
            } else {
                int h = 0;
                for (Map.Entry<K, Object> objectObjectEntry : map.entrySet()) {
                    h += entryHashCode(objectObjectEntry);
                }
                return h;
            }
        }

        private int entryHashCode(Map.Entry<K, Object> entry) {
            Object key = entry.getKey();
            Object value = entry.getValue();
            int keyHash = (key == null ? 0 : key.hashCode());
            int valueHash = (value == null ? 0 : value.toString().hashCode());
            return keyHash ^ valueHash;
        }
    }

    public static String toStringNormalizingNumbers(Object o) {
        try {
            return o == null ? null : Double.valueOf(o.toString()).toString();
        } catch (NumberFormatException e) {
            return o.toString();
        }
    }

    public static Double toDouble(Object o) {
        return o == null ? null : Double.valueOf(o.toString());
    }

    public static Integer toInteger(Object o) {
        return o == null ? null : Integer.valueOf(o.toString());
    }

    public static <T> List<T> optionalSingletonList(T item) {
        return item == null ? Collections.emptyList() : Collections.singletonList(item);
    }

    /**
     * Checks to string for equality
     * @param o1 string 1
     * @param o2 string 2
     * @return true if two strings are equal
     */
    public static boolean stringEquals(Object o1, Object o2) {
        final String s1 = o1 == null ? null : o1.toString();
        final String s2 = o2 == null ? null : o2.toString();
        return Objects.equals(s1, s2);
    }
}
