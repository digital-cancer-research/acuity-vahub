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
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Builder
public class AeSummariesTable {
    private String datasetName;
    private long countDosedSubject;
    private Set<AeSummariesCohortCount> cohortCounts = new HashSet<>();
    private List<AeSummariesRow> rows = new ArrayList<>();

    @Data
    @EqualsAndHashCode(exclude = {"count"})
    @AllArgsConstructor
    public static class AeSummariesCohortCount {
        private String cohort;
        private String grouping;
        private GroupingType groupingType;
        private String studyPart;
        private int count;

        public AeSummariesCohortCount(String cohort, String grouping, GroupingType groupingType, String studyPart) {
            this.cohort = cohort;
            this.grouping = grouping;
            this.groupingType = groupingType;
            this.studyPart = studyPart;
        }
    }

    @Data
    @Builder
    public static class AeSummariesRow {
        private String rowDescription;
        private String soc;
        private String pt;
        private String drug;
        private List<AeSummariesCell> cells = new ArrayList<>();
    }

    @Data
    public static class AeSummariesCell {
        private AeSummariesCohortCount cohortCount;
        private int value;
        private double percentage;

        public AeSummariesCell(String cohort, String grouping, int value, double percentage, GroupingType groupingType, String studyPart) {
            cohortCount = new AeSummariesCohortCount(cohort, grouping, groupingType, studyPart);
            this.value = value;
            this.percentage = percentage;
        }
    }

    public enum GroupingType {
        DOSE, NONE
    }
}
