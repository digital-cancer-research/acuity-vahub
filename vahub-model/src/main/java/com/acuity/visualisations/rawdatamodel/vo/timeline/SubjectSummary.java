package com.acuity.visualisations.rawdatamodel.vo.timeline;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@EqualsAndHashCode
@ToString
@NoArgsConstructor
public abstract class SubjectSummary implements Serializable {
    private String subjectId;

    private String subject;

    public SubjectSummary(String subjectId, String subject) {
        this.subjectId = subjectId;
        this.subject = subject;
    }
}
