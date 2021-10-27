package com.acuity.visualisations.rawdatamodel.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * Class aggregates administrative information related to study, i.e. cBioStudyCode, is AML enabled
 */

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class StudyInfoAdministrationDetail implements Serializable {
    private Long studyId;
    private String studyCode;
    private String cBioStudyCode;
    private Boolean amlEnabled;
}
