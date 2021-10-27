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
public class DrugDiscontinued implements Serializable {
    private String subjectId;
    private String drug;
    private String discontinued;
    private String discReason;
    private Date discDate;
}
