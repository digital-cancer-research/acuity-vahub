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

import static com.acuity.visualisations.rawdatamodel.util.DaysUtil.YMD;

@EqualsAndHashCode(of = "id")
@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@AcuityEntity(version = 5)
public class MedicalHistoryRaw implements HasStringId, HasSubjectId, Serializable {

    private String id;
    private String subjectId;

    @Column(order = 1, displayName = "Medical history category", type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    private String category;

    @Column(order = 1, displayName = "Medical condition", type = Column.Type.SSV)
    @Column(order = 2, displayName = "Medical history term", type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    private String term;

    @Column(order = 2, displayName = "Preferred term", type = Column.Type.SSV)
    @Column(order = 7, displayName = "PT name", type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    private String preferredTerm;

    @Column(order = 3, displayName = "Condition status", type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    private String conditionStatus;

    @Column(order = 4, displayName = "Current medication", type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    private String currentMedication;

    @Column(order = 3, displayName = "Start date", type = Column.Type.SSV, dateFormat = YMD)
    @Column(order = 5, displayName = "Start date", columnName = "start", defaultSortBy = true,
            type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    private Date start;

    @Column(order = 4, displayName = "End date", type = Column.Type.SSV, dateFormat = YMD)
    @Column(order = 6, displayName = "End date", type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    private Date end;

    @Column(order = 8, displayName = "HLT name", type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    private String hlt;

    @Column(order = 9, displayName = "SOC name", type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    private String soc;
}
