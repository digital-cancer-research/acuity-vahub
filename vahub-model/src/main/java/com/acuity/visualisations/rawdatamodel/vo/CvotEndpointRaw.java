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
@Builder(toBuilder = true)
@AllArgsConstructor
@AcuityEntity(version = 6)
public final class CvotEndpointRaw implements HasStringId, HasSubjectId, Serializable {
    private String id;
    private String subjectId;
    private Integer aeNumber;
    @Column(columnName = "startDate", order = 1, displayName = "Start date")
    private Date startDate;
    @Column(columnName = "term", order = 2, displayName = "Term")
    private String term;
    @Column(columnName = "category1", order = 3, displayName = "Category 1")
    private String category1;
    @Column(columnName = "category2", order = 4, displayName = "Category 2")
    private String category2;
    @Column(columnName = "category3", order = 5, displayName = "Category 3")
    private String category3;
    @Column(columnName = "description1", order = 6, displayName = "Description 1")
    private String description1;
    @Column(columnName = "description2", order = 7, displayName = "Description 2")
    private String description2;
    @Column(columnName = "description3", order = 8, displayName = "Description 3")
    private String description3;
}

