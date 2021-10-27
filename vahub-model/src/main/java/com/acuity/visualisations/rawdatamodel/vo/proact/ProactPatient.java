package com.acuity.visualisations.rawdatamodel.vo.proact;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * Patient VO object for synchronisation with PROACT application.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "Proact patient", description = "Patient model for PROACT application")
public class ProactPatient implements Serializable {

    @ApiModelProperty(value = "Patient id")
    private String patientId;
    @ApiModelProperty(value = "Subject code (ECode)")
    private String subjectCode;
    @ApiModelProperty(value = "Race")
    private String race;
    @ApiModelProperty(value = "Sex")
    private String sex;
    @ApiModelProperty(value = "Birth date")
    private Date birthDate;
    @ApiModelProperty(value = "First visit date")
    private Date firstVisitDate;
    @ApiModelProperty(value = "First dose date")
    private Date firstDoseDate;
    @ApiModelProperty(value = "Country")
    private String country;
    @ApiModelProperty(value = "Centre")
    private String centre;

}
