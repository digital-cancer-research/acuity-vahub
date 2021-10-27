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
