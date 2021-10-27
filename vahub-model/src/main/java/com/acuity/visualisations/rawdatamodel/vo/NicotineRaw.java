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
@AllArgsConstructor
@Builder(toBuilder = true)
@AcuityEntity(version = 1)
public final class NicotineRaw implements HasStringId, HasSubjectId, Serializable {
    private String id;
    private String subjectId;

    @Column(order = 1, displayName = "Substance Category", type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    private String category;

    @Column(order = 2, displayName = "Substance Use Occurrence", type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    private String useOccurrence;

    @Column(order = 3, displayName = "Substance Type", type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    private String type;

    @Column(order = 4, displayName = "Other Substance Type Specification",
            type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    private String otherTypeSpec;

    @Column(order = 5, displayName = "Substance Type Use Occurrence",
            type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    private String subTypeUseOccurrence;

    @Column(order = 7, displayName = "Substance Use Start Date", columnName = "startDate", defaultSortBy = true,
            type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    private Date startDate;

    @Column(order = 8, displayName = "Substance Use End Date",
            type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    private Date endDate;

    @Column(order = 6, displayName = "Current Substance Use Specification",
            type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    private String currentUseSpec;

    @Column(order = 9, displayName = "Substance Consumption", type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    private Integer consumption;

    @Column(order = 10, displayName = "Substance Use Frequency Interval",
            type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    private String frequencyInterval;

    @Column(order = 11, displayName = "Number of Pack Years",
            type = Column.Type.DOD, datasetType = Column.DatasetType.ACUITY)
    private Integer numberPackYears;

}
