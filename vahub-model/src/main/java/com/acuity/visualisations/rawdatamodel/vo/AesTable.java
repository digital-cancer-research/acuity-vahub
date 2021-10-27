package com.acuity.visualisations.rawdatamodel.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class AesTable implements Serializable {

    private String term;
    private String grade;
    private String treatmentArm;
    // subject per grade/severity term and arm
    private Integer subjectCountPerGrade;
    // subjects per term and arm
    private Integer subjectCountPerTerm;
    // subject on the arm
    private Integer subjectCountPerArm;
    private Integer noIncidenceCount;

    public Integer getNoIncidenceCount() {
        if (subjectCountPerArm != null && subjectCountPerTerm != null) {
            return subjectCountPerArm - subjectCountPerTerm;
        } else {
            return 0;
        }
    }
}
