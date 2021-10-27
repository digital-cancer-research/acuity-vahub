package com.acuity.visualisations.rawdatamodel.dataset.info.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class StudyWarnings implements Serializable {
    
    private String studyId;
    private boolean blinded;
    private boolean randomised;
    private boolean forRegulatoryPurposes;
}
