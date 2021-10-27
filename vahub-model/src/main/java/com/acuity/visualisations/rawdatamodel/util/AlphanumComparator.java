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

import net.logstash.logback.encoder.org.apache.commons.lang.ObjectUtils;
import se.sawano.java.text.AlphanumericComparator;

import java.util.Comparator;

public class AlphanumComparator<T extends Comparable<T>> implements Comparator<T> {

    private static Comparator<CharSequence> alphanumericComparator = new AlphanumericComparator();

    @Override
    public int compare(T obj1, T obj2) {
        if (!(obj1 instanceof String) || !(obj2 instanceof String)) {
            return ObjectUtils.compare(obj1, obj2, true);
        }

        CharSequence str1 = (String) obj1;
        CharSequence str2 = (String) obj2;

        return alphanumericComparator.compare(str1, str2);
    }
}
