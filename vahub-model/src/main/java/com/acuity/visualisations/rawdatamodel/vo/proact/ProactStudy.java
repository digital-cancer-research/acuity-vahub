package com.acuity.visualisations.rawdatamodel.vo.proact;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * Study VO object for synchronisation with PROACT application.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "Proact study", description = "Study model for PROACT application")
public class ProactStudy implements Serializable {

    @ApiModelProperty(value = "Patient list")
    private List<ProactPatient> patients;

    @ApiModelProperty(value = "Study code")
    private String studyCode;

    @ApiModelProperty(value = "Project name")
    private String projectName;

    @ApiModelProperty(value = "Drug programme")
    private String drugProgramme;
}
