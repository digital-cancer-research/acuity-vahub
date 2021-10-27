package com.acuity.visualisations.rawdatamodel.vo;

import com.acuity.visualisations.rawdatamodel.util.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

@EqualsAndHashCode(of = "id")
@Builder(toBuilder = true)
@AcuityEntity(version = 0)
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class LiverDiagRaw implements HasStringId, HasSubjectId, Serializable {
    private String id;
    private String subjectId;

    @Column(order = 1, displayName = "Liver diagnostic investigation")
    private String liverDiagInv;
    @Column(order = 2, displayName = "Liver diagnostic investigation specification")
    private String liverDiagInvSpec;
    @Column(order = 5, displayName = "Liver diagnostic investigation results")
    private String liverDiagInvResult;
    @Column(order = 3, columnName = "liverDiagInvDate", displayName = "Liver diagnostic investigation date", defaultSortBy = true)
    private Date liverDiagInvDate;
    @Column(order = 6, displayName = "Potential Hy's law case number")
    private Integer potentialHysLawCaseNum;
}
