package com.acuity.visualisations.rawdatamodel.vo.timeline.aes;

import com.acuity.visualisations.rawdatamodel.vo.timeline.SubjectSummary;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString
public final class SubjectAesDetail extends SubjectSummary implements Serializable {
    @Builder
    private SubjectAesDetail(String subjectId, String subject, List<AeDetail> aes) {
        super(subjectId, subject);
        this.aes = aes;
    }

    private List<AeDetail> aes;
}
