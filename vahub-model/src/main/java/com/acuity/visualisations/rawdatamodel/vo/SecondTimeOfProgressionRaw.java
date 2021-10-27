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

@Getter
@EqualsAndHashCode(of = "id")
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@AcuityEntity(version = 0)
public class SecondTimeOfProgressionRaw implements HasStringId, HasSubjectId, Serializable {
    private String id;
    private String subjectId;
    @Column(order = 1, displayName = "Visit date", type = Column.Type.SSV)
    private Date visitDate;
    @Column(order = 2, displayName = "Assessement performed", type = Column.Type.SSV)
    private String assessmentPerformed;
    @Column(order = 3, displayName = "Reason assessment not performed", type = Column.Type.SSV)
    private String reason;
    @Column(order = 4, displayName = "Date of scan", type = Column.Type.SSV)
    private Date scanDate;
    @Column(order = 5, displayName = "Investigator asmt of patient response", type = Column.Type.SSV)
    private String investigatorAsmt;
    @Column(order = 6, displayName = "Type of progression", type = Column.Type.SSV)
    private String progressionType;
    @Column(order = 7, displayName = "Type of progression (meor)", type = Column.Type.SSV)
    private String progressionMeor;
    @Column(order = 8, displayName = "Other", type = Column.Type.SSV)
    private String other;
}
