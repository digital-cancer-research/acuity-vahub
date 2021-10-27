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
@AcuityEntity(version = 4)
public class SurgicalHistoryRaw implements HasStringId, HasSubjectId, Serializable {

    private String id;
    private String subjectId;

    @Column(order = 1, displayName = "Preferred term", type = Column.Type.SSV)
    @Column(order = 4, displayName = "PT name", type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    private String preferredTerm;

    @Column(order = 2, displayName = "Surgical procedure", type = Column.Type.SSV)
    @Column(order = 1, displayName = "Medical history term", type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    private String surgicalProcedure;

    @Column(order = 2, displayName = "Current medication", type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    private String currentMedication;

    @Column(order = 3, displayName = "Date of surgery", type = Column.Type.SSV)
    @Column(order = 3,
            displayName = "Start date",
            defaultSortBy = true,
            defaultSortOrder = 1,
            type = Column.Type.DOD,
            datasetType = Column.DatasetType.ACUITY)
    private Date start;

    private String pt;

    @Column(order = 5, displayName = "HLT name", type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    private String hlt;

    @Column(order = 6, displayName = "SOC name", type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    private String soc;
}
