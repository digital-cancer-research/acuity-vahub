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

package com.acuity.visualisations.common.vo;

/**
 * The dayzero date. Currently its first dose, but randomisation and others to follow
 *
 * @author ksnd199
 */
public enum DayZeroType {

    DAYS_SINCE_FIRST_DOSE,
    DAYS_SINCE_STUDY_DAY,
    DAYS_SINCE_RANDOMISATION,
    DAYS_SINCE_FIRST_TREATMENT;

    /**
     * Check if the name of enum instance is equals to the given name
     *
     * @param name enum item name
     * @return true if equals
     */
    public boolean is(String name) {
        return name().equals(name);
    }
}
