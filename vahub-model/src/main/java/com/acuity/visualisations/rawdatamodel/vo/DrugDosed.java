package com.acuity.visualisations.rawdatamodel.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

@ToString
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DrugDosed implements Serializable {

    private String subjectId;
    private String drug;
    private String dosed;
    private Date firstDoseDate;
    private String maxDose;
    private Double rawMaxDose;
    private String maxFrequency;
    private Integer totalDurationInclBreaks;
    private Integer totalDurationExclBreaks;
    private Date lastDoseDate;
}
