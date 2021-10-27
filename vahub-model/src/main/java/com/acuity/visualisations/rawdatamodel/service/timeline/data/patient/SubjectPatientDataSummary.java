package com.acuity.visualisations.rawdatamodel.service.timeline.data.patient;

import com.acuity.visualisations.rawdatamodel.vo.timeline.SubjectSummary;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SubjectPatientDataSummary extends SubjectSummary {
    private List<PatientDataEvent> events;

    public SubjectPatientDataSummary(String subjectId, String subjectCode, List<PatientDataEvent> events) {
        this.setSubjectId(subjectId);
        this.setSubject(subjectCode);
        this.events = events;
    }
}
