package com.acuity.visualisations.rawdatamodel.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

@EqualsAndHashCode(of = "subjectId")
@ToString
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@AcuityEntity(version = 0)
public class SubjectExtRaw implements HasStringId, HasSubjectId, Serializable {

    private String subjectId;
    private Date diagnosisDate; // pathology
    private Integer daysFromDiagnosisDate; // pathology
    private Date recentProgressionDate; // disease extent

    @Override
    public String getId() {
        return subjectId;
    }

    @Override
    public String getSubjectId() {
        return subjectId;
    }
}
