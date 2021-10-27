package com.acuity.visualisations.rawdatamodel.service.timeline.data.patient;

import com.acuity.visualisations.rawdatamodel.vo.timeline.SubjectSummary;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SubjectPatientDataDetail extends SubjectSummary {
    private List<PatientDataTests> tests;

    public SubjectPatientDataDetail(String subjectId, String subjectCode, List<PatientDataTests> tests) {
        this.setSubjectId(subjectId);
        this.setSubject(subjectCode);
        this.tests = tests;
    }
}
