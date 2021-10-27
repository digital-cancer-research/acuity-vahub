package com.acuity.visualisations.rawdatamodel.vo.timeline.lungfunction;

import com.acuity.visualisations.rawdatamodel.vo.timeline.SubjectSummary;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * Status detail of spirometry/lung function (visits) per measurement type for a subject, which consists of a list of LungFunctionDetailEvent.
 */
@Getter
@ToString(callSuper = true)
public final class SubjectLungFunctionDetail extends SubjectSummary implements Serializable {
    private String sex;

    private List<LungFunctionCodes> lungFunctionCodes;

    @Builder
    private SubjectLungFunctionDetail(String subjectId, String subject,
                                      List<LungFunctionCodes> codes,
                                      String sex) {
        super(subjectId, subject);
        this.lungFunctionCodes = codes;
        this.sex = sex;
    }
}
