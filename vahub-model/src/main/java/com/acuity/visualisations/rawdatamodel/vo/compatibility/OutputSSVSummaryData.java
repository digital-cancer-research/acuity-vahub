package com.acuity.visualisations.rawdatamodel.vo.compatibility;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutputSSVSummaryData implements Serializable {
    private String subjectId;
    private Map<String, String> demography;
    private Map<String, String> study;
    private String medicalHistories;
}
