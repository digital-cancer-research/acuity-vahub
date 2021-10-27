package com.acuity.visualisations.rawdatamodel.vo;

import java.util.Date;


public interface HasSubject extends HasSubjectId {

    Subject getSubject();

    String getSubjectId();

    default String getSubjectCode() {
        return getSubject().getSubjectCode();
    }

    default String getClinicalStudyName() {
        return getSubject().getClinicalStudyName();
    }

    default String getClinicalStudyCode() {
        return getSubject().getClinicalStudyCode();
    }

    default String getDatasetCode() {
        return getSubject().getDatasetCode();
    }

    default String getDatasetName() {
        return getSubject().getDatasetName();
    }

    default String getStudyPart() {
        return getSubject().getStudyPart();
    }

    default Date getDateOfFirstDose() {
        return getSubject().getFirstTreatmentDate();
    }

    default Date getDateOfFirstDoseOfDrug(String drug) {
        return drug == null || drug.length() == 0 ? getSubject().getFirstTreatmentDate() : getSubject().getDrugFirstDoseDate().get(drug);
    }
}
