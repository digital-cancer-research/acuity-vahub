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

package com.acuity.visualisations.common.util;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang3.math.NumberUtils;

/**
 * This is because H2 and oracle return different values for max/min for dates etv
 *
 * @author ksnd199
 */
public final class ObjectConvertor {

    private ObjectConvertor() {
    }

    /**
     * Converts BigDeciaml or Long to float.
     */
    public static Float toFloat(Object number) {
        if (number == null) {
            return null;
        } else if (number instanceof BigDecimal) {
            return ((BigDecimal) number).floatValue();
        } else if (number instanceof Long) {
            return ((Long) number).floatValue();
        } else {
            return NumberUtils.toFloat(number.toString());
        }
    }

    /**
     * Converts BigDeciaml or Long to double.
     */
    public static Double toDouble(Object number) {
        if (number == null) {
            return null;
        } else if (number instanceof BigDecimal) {
            return ((BigDecimal) number).doubleValue();
        } else if (number instanceof Long) {
            return ((Long) number).doubleValue();
        } else {
            return NumberUtils.toDouble(number.toString());
        }
    }

    /**
     * Converts BigDeciaml or Long to int.
     */
    public static Integer toInt(Object number) {
        if (number == null) {
            return null;
        } else if (number instanceof BigDecimal) {
            return ((BigDecimal) number).intValue();
        } else if (number instanceof Long) {
            return ((Long) number).intValue();
        } else {
            return NumberUtils.toInt(number.toString());
        }
    }

    /**
     * Converts BigDeciaml or Long to Long.
     */
    public static Long toLong(Object number) {
        if (number == null) {
            return null;
        } else if (number instanceof BigDecimal) {
            return ((BigDecimal) number).longValue();
        } else if (number instanceof Long) {
            return (Long) number;
        } else {
            return NumberUtils.toLong(number.toString());
        }
    }

    /**
     * Converts to String.
     */
    public static String toString(Object string) {
        if (string == null) {
            return null;
        } else {
            return string.toString();
        }
    }

    /**
     * Converts to Date.
     */
    public static Date toDate(Object date) {
        if (date == null) {
            return null;
        }  else if (date instanceof Date) {
            return ((Date) date);
        } else {
            throw new IllegalStateException(date + " is not a date object");
        }
    }
}
