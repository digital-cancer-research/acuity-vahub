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

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@EqualsAndHashCode(of = "id")
@ToString
@Getter
@NoArgsConstructor
@Builder(toBuilder = true)
@AllArgsConstructor
public final class AeSeverityRaw implements Serializable {

    private String id; // severity id
    private String aeId;
    private Date startDate;
    private Date endDate;
    private Date endDateRaw;
    private boolean ongoing;
    private AeSeverity severity;
    private AeEndType endType;

    @Builder.Default
    private Map<String, String> drugsActionTaken = new HashMap<>();

    public Map<String, String> getDrugsActionTaken() {
        return getUnmodifiableMap(drugsActionTaken);
    }

    private static <K, V> Map<K, V> getUnmodifiableMap(Map<K, V> map) {
        return map == null ? null : Collections.unmodifiableMap(map);
    }

    public enum AeEndType {
        DEATH, WITHDRAWAL, LAST_VISIT, NONE
    }

}
