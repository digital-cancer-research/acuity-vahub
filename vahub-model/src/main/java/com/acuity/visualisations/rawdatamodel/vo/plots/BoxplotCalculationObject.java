package com.acuity.visualisations.rawdatamodel.vo.plots;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
@Builder
public final class BoxplotCalculationObject implements Serializable {
    
    private Double median;
    private Double upperQuartile;
    private Double lowerQuartile;
    private Double upperWhisker;
    private Double lowerWhisker;
    private Long subjectCount;
    // all events including outliers
    private Long eventCount;
    //we might need some custom attrs in outliers (like subject id) so it's not simply set of doubles
    private Set<BoxPlotOutlier> outliers;

}
