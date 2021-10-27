package com.acuity.visualisations.rawdatamodel.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

@Getter
@EqualsAndHashCode(of = "id")
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@AcuityEntity(version = 1)
public class PatientDataRaw implements HasStringId, HasSubjectId, Serializable {

    private String id;
    private String subjectId;
    private String measurementName;
    private Double value;
    private String unit;
    private Date measurementDate;
    private Date reportDate;
    private String comment;
    private String sourceType;
    private String sourceId;
}
