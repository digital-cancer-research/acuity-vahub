package com.acuity.visualisations.rawdatamodel.vo;

import static com.acuity.visualisations.rawdatamodel.util.ObjectUtil.nvl;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 *
 * @author ksnd199
 */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Getter
@NoArgsConstructor
public class AeSubjectTermArmMaxSeverity extends AeTermArmMaxSeverity {

    private String subjectId;

    public AeSubjectTermArmMaxSeverity(String subjectId, String term, String treatmentArm, AeSeverity maxSeverity) {
        super(nvl(term, "No term recorded"), treatmentArm, maxSeverity);
        this.subjectId = subjectId;
    }
}
