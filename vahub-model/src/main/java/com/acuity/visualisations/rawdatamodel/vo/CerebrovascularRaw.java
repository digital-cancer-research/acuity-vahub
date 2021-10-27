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

/**
 * Created by knml167 on 6/9/2017.
 */
@Getter
@EqualsAndHashCode(of = "id")
@ToString
@NoArgsConstructor
@Builder
@AllArgsConstructor
@AcuityEntity(version = 7)
public class CerebrovascularRaw implements HasStringId, HasSubjectId, Serializable {
    private String id;
    private String subjectId;
    @Column(columnName = "startDate", order = 3, displayName = "Start date")
    private Date startDate;
    @Column(columnName = "term", order = 4, displayName = "Term")
    private String term;
    private int aeNumber;
    @Column(columnName = "eventType", order = 1, displayName = "Event type")
    private String eventType;
    @Column(columnName = "primaryIschemicStroke", order = 5, displayName = "Primary ischemic stroke")
    private String primaryIschemicStroke;
    @Column(columnName = "traumatic", order = 6, displayName = "Traumatic")
    private String traumatic;
    @Column(columnName = "intraHemorrhageLoc", order = 7, displayName = "Intra hemorrhage loc")
    private String intraHemorrhageLoc;
    @Column(columnName = "intraHemorrhageOtherLoc", order = 8, displayName = "Intra hemorrhage other loc")
    private String intraHemorrhageOtherLoc;
    @Column(columnName = "symptomsDuration", order = 9, displayName = "Symptoms duration")
    private String symptomsDuration;
    @Column(columnName = "mrsPriorToStroke", order = 10, displayName = "Mrs prior to stroke")
    private String mrsPriorToStroke;
    @Column(columnName = "mrsDuringStrokeHosp", order = 11, displayName = "Mrs during stroke hosp")
    private String mrsDuringStrokeHosp;
    @Column(columnName = "mrsCurrVisitOr90dAfter", order = 12, displayName = "Mrs curr visit or 90d after")
    private String mrsCurrVisitOr90dAfter;
    @Column(columnName = "comment", order = 13, displayName = "Comment")
    private String comment;
}
