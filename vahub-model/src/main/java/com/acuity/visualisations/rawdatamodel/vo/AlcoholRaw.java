package com.acuity.visualisations.rawdatamodel.vo;

import com.acuity.visualisations.rawdatamodel.util.Column;
import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@EqualsAndHashCode(of = "id")
@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@AcuityEntity(version = 0)
public class AlcoholRaw implements HasStringId, HasSubjectId, Serializable {
    private String id;
    private String subjectId;

    @Column(order = 1, displayName = "Substance category", type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    private String substanceCategory;
    @Column(order = 2, displayName = "Substance use occurrence", type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    private String substanceUseOccurrence;
    @Column(order = 3, displayName = "Type of substance", type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    private String substanceType;
    @Column(order = 4, displayName = "Other substance type specification", type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    private String otherSubstanceTypeSpec;
    @Column(order = 5, displayName = "Substance type use occurrence", type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    private String substanceTypeUseOccurrence;
    @Column(order = 7, displayName = "Frequency", type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    private String frequency;
    @Column(order = 6, displayName = "Substance consumption", type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    private Double substanceConsumption;
    @Column(order = 8, columnName = "startDate", displayName = "Start date", type = Column.Type.DOD,
            datasetType = Column.DatasetType.ACUITY, defaultSortBy = true)
    private Date startDate;
    @Column(order = 9, displayName = "End date", type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    private Date endDate;

}
