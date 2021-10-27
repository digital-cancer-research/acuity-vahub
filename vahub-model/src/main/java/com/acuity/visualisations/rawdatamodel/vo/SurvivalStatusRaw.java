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
@ToString
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@AcuityEntity(version = 0)
public class SurvivalStatusRaw implements HasStringId, HasSubjectId, Serializable {
    private String id;
    private String subjectId;

    @Column(order = 1, displayName = "Visit date", type = Column.Type.SSV)
    private Date visitDate;
    @Column(order = 2, displayName = "Survival status", type = Column.Type.SSV)
    private String survivalStatus;
    @Column(order = 3, displayName = "Date subject last known to be alive", type = Column.Type.SSV)
    private Date lastAliveDate;
}
