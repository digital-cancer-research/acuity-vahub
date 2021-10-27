package com.acuity.visualisations.rawdatamodel.vo;

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
public class AeTermArmMaxSeverity extends AeTermArm {

    protected AeSeverity maxSeverity;

    public AeTermArmMaxSeverity(String term, String treatmentArm, AeSeverity maxSeverity) {
        super(term, treatmentArm);

        if (maxSeverity == null || maxSeverity.isNull()) {
            this.maxSeverity = new AeSeverity(-1, "No severity grade recorded", "No severity grade recorded");
        } else {
            this.maxSeverity = maxSeverity;
        }
    }
}
