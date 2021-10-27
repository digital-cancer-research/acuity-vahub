package com.acuity.visualisations.rawdatamodel.dataset.info.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudySelectionDatasetInfo implements Serializable {

    private Long datasetId;
    private Integer numberOfDosedSubjects;
    private Date dataCutoffDate;
    private Date lastRecordedEventDate;
}
