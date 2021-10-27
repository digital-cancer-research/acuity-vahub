package com.acuity.visualisations.rawdatamodel.vo.compatibility;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OutputSSVSummaryMetadata implements Serializable {
    private Map<String, String> demography;
    private Map<String, String> study;
    private Boolean medicalHistories;
}
