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

package com.acuity.visualisations.rawdatamodel.vo.biomarker;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

@AllArgsConstructor
@Getter
@ToString(of = "name")
public enum BiomarkerMutation {

    AMPLIFICATION_MUTATION("Amplification", 20, 30),
    GAIN_MUTATION("Gain", 30, 30),
    REARRANGEMENT_MUTATION("Rearrangement", 30, 30),
    NONSYNONYMOUS_MUTATION("Nonsynonymous mutation", 10, 10),
    DELETION_MUTATION("Deletion", 20, 20),
    INDEL_MUTATION("InDel", 30, 30),
    SPLICE_MUTATION("Splice", 10, 10),
    TRUNCATING_MUTATION("Truncating", 10, 10),
    OTHER_MUTATION("Other", 50, 30),
    NOT_RECOGNISED_MUTATION("Not recognised", 60, 30),
    PROMOTER("Promoter", 40, 30);

    private String name;
    private Integer priorityIfSomaticStatusKnown;
    private Integer priorityIfSomaticStatusLikely;
    private static final Map<String, BiomarkerMutation> ENUM_MAP;

    static {
        Map<String, BiomarkerMutation> map = Stream.of(BiomarkerMutation.values()).collect(toMap(BiomarkerMutation::getName, identity()));
        ENUM_MAP = Collections.unmodifiableMap(map);
    }

    public static BiomarkerMutation getMutationByName(String name) {
        return ENUM_MAP.get(name);
    }

    public static final Comparator<BiomarkerMutation> SOMATIC_STATUS_KNOWN_COMPARATOR =
            Comparator.comparing(BiomarkerMutation::getPriorityIfSomaticStatusKnown).thenComparing(BiomarkerMutation::getName);
    public static final Comparator<BiomarkerMutation> SOMATIC_STATUS_LIKELY_COMPARATOR =
            Comparator.comparing(BiomarkerMutation::getPriorityIfSomaticStatusLikely).thenComparing(BiomarkerMutation::getName);
}
