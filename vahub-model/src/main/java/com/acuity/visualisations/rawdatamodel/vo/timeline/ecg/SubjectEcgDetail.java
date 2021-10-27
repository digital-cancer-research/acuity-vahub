package com.acuity.visualisations.rawdatamodel.vo.timeline.ecg;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public final class SubjectEcgDetail extends SubjectEcg implements Serializable {
    private List<EcgTest> tests;

    @Builder
    private SubjectEcgDetail(String subjectId, String subject, List<EcgTest> tests, String sex) {
        super(subjectId, subject, sex);
        this.tests = tests;
    }
}
