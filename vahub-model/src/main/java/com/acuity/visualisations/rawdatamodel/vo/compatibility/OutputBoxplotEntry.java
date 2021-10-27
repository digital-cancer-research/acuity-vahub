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

package com.acuity.visualisations.rawdatamodel.vo.compatibility;

import com.acuity.visualisations.rawdatamodel.vo.plots.BoxplotCalculationObject;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = {"x"})
@Builder
public final class OutputBoxplotEntry implements Serializable {
    
    private String x;
    private Double xRank;
    private Long subjectCount;
    // all events including outliers
    private Long eventCount;
    private Double median;
    private Double upperQuartile;
    private Double lowerQuartile;
    private Double upperWhisker;
    private Double lowerWhisker;
    //we might need some custom attrs in outliers (like subject id) so it's not simply set of doubles
    private Set<OutputBoxPlotOutlier> outliers;


    public static OutputBoxplotEntry of(String x, Double xRank, BoxplotCalculationObject boxplot) {
        return boxplot == null ? empty(x, xRank) : OutputBoxplotEntry.builder()
                .xRank(xRank)
                .x(x)
                .subjectCount(boxplot.getSubjectCount())
                .eventCount(boxplot.getEventCount())
                .median(boxplot.getMedian())
                .upperQuartile(boxplot.getUpperQuartile())
                .lowerQuartile(boxplot.getLowerQuartile())
                .upperWhisker(boxplot.getUpperWhisker())
                .lowerWhisker(boxplot.getLowerWhisker())
                .outliers(boxplot.getOutliers().stream()
                        .filter(Objects::nonNull)
                        .map(o -> OutputBoxPlotOutlier.of(o, x)).collect(Collectors.toSet()))
                .build();
    }
    public static OutputBoxplotEntry empty(String x, Double xRank) {
        return OutputBoxplotEntry.builder()
                .xRank(xRank)
                .x(x)
                .outliers(Collections.emptySet())
                .build();
    }
}
