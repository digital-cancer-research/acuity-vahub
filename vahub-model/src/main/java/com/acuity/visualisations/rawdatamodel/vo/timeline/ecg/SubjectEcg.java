package com.acuity.visualisations.rawdatamodel.vo.timeline.ecg;

import com.acuity.visualisations.rawdatamodel.vo.timeline.SubjectSummary;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubjectEcg extends SubjectSummary {
    private String sex;

    public SubjectEcg(String subjectId, String subject, String sex) {
        super(subjectId, subject);
        this.sex = sex;
    }
}
