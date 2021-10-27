package com.acuity.visualisations.rawdatamodel.vo.biomarker;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serializable;

@Builder
@EqualsAndHashCode
@Getter
public class BiomarkerParameters implements Serializable {
    private String mutation;
    private String somaticStatus;
    private String aminoAcidChange;
    private Integer copyNumberAlterationCopyNumber;
    private Integer alleleFrequency;
}
