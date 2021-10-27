package com.acuity.visualisations.rawdatamodel.vo;

import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class AeSubjectTermMaxSeverity extends AeTermArm {
    private String subjectId;
    private String term;
    private AeSeverity maxSeverity;
}
