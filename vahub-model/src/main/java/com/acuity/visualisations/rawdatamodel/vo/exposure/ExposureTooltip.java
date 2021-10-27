package com.acuity.visualisations.rawdatamodel.vo.exposure;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class ExposureTooltip implements Serializable {
    private int dataPoints;
    private String colorByValue;
    private ExposureData exposureData;
}
