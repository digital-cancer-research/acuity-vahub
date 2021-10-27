package com.acuity.visualisations.rawdatamodel.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.ObjectUtils;

import java.io.Serializable;

@EqualsAndHashCode(of = "severityNum")
@ToString
@Getter
@NoArgsConstructor
@Builder(toBuilder = true)
@AllArgsConstructor
public final class AeSeverity implements Serializable {

    /**
     * ie 1,2,3..
     */
    private Integer severityNum;
    /**
     * ie 1 - Mild AE
     */
    private String severity;
    /**
     * ie CTC Grade 1
     */
    private String webappSeverity;
    
    public boolean isNull() {
        return !ObjectUtils.allNotNull(severityNum, webappSeverity);
    }
}
