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
@AcuityEntity(version = 1)
public class DiseaseExtentRaw implements HasStringId, HasSubjectId, Serializable {
    private String id;
    private String subjectId;

    @Column(order = 3, displayName = "Site of locally adv metastatic disease", type = Column.Type.SSV)
    private String siteLocalMetaDisease;
    @Column(order = 5, displayName = "Metastatic / locally adv / both", type = Column.Type.SSV)
    private String localOrMetastaticCancer;
    @Column(order = 2, displayName = "Date of most recent progression", type = Column.Type.SSV)
    private Date recentProgressionDate;
    @Column(order = 1, displayName = "Reoccurance of earlier cancer", type = Column.Type.SSV)
    private String recurrenceOfEarlierCancer;
    private Date visitDate;
    @Column(order = 4, displayName = "Other locally advanced sites", type = Column.Type.SSV)
    private String otherLocAdvSites;
    @Column(order = 6, displayName = "Other metastatic sites", type = Column.Type.SSV)
    private String otherMetastaticSites;
}
