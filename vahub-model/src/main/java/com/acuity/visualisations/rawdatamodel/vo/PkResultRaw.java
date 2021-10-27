package com.acuity.visualisations.rawdatamodel.vo;

import com.acuity.visualisations.rawdatamodel.util.Column;
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
@Builder(toBuilder = true)
@AcuityEntity(version = 9)
public class PkResultRaw implements HasStringId, HasSubjectId, Serializable {

    private String id;
    private String subjectId;
    @Column(columnName = "parameter", order = 5, displayName = "Parameter")
    private String parameter;
    @Column(columnName = "parameterValue", order = 6, displayName = "Value")
    private Double parameterValue;
    @Column(columnName = "parameterUnit", order = 7, displayName = "Unit")
    private String parameterUnit;
    @Column(columnName = "analyte", order = 1, displayName = "Analyte")
    private String analyte;
    private Date visitDate;
    private Integer visitNumber;
    @Column(columnName = "treatment", order = 8, displayName = "Nominal dose")
    private Double treatment;
    @Column(columnName = "cycle", order = 2, displayName = "Cycle")
    private String treatmentCycle;
    @Column(columnName = "protocolScheduleStartDay", order = 3, displayName = "Nominal day")
    private String protocolScheduleStartDay;
    @Column(columnName = "visit", order = 4, displayName = "Visit")
    private String visit;
    @Column(columnName = "actualDose", order = 9, displayName = "Actual administered dose")
    private Double actualDose;
    private String bestOverallResponse;
}
