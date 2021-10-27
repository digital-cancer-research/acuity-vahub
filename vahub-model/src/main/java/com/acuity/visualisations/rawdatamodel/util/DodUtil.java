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

import com.google.common.collect.Multimap;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;

/**
 * @author ksnd199
 */
public final class DodUtil {

    public static final String STUDY_ID = "studyId";
    public static final String STUDY_PART = "studyPart";
    public static final String SUBJECT_ID = "subjectId";
    public static final String EVENT_ID = "eventId";

    private DodUtil() {
    }

    public static String toString(Multimap<String, String> multimap, String keyValueSeperator, String entrySeperator) {
        if (multimap == null) {
            return null;
        }

        return multimap.keySet().stream()
                .flatMap((String k) -> {
                    Collection<String> list = multimap.get(k);
                    return list.stream().map(i -> k + keyValueSeperator + i);
                })
                .collect(joining(entrySeperator));
    }

    public static String toString(Map<String, String> map, String keyValueSeperator, String entrySeperator) {
        if (map == null) {
            return null;
        }

        return map.entrySet()
                .stream()
                .map(entry -> entry.getKey() + keyValueSeperator + entry.getValue())
                .collect(joining(entrySeperator));
    }

    public static String toString(List<? extends Object> list, String seperator) {
        if (list == null) {
            return null;
        }

        return list.stream().filter(Objects::nonNull).map(Object::toString).sorted().collect(Collectors.joining(seperator));
    }
}
