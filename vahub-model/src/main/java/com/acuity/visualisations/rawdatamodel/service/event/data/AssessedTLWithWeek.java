package com.acuity.visualisations.rawdatamodel.service.event.data;

import com.acuity.visualisations.rawdatamodel.util.Column;
import com.acuity.visualisations.rawdatamodel.vo.AssessmentRaw;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.AssessedTargetLesion;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
@AllArgsConstructor
public class AssessedTLWithWeek {

    private AssessedTargetLesion assessedTargetLesion;
    private Integer assessmentWeek;

    @Column(order = -3, columnName = "studyId", displayName = "Study id")
    public String getStudyId() {
        return assessedTargetLesion.getStudyId();
    }

    @Column(order = -2, columnName = "studyPart", displayName = "Study part")
    public String getStudyPart() {
        return assessedTargetLesion.getStudyPart();
    }

    @Column(order = -1, columnName = "subjectId", displayName = "Subject id", defaultSortBy = true)
    public String getRawSubjectCode() {
        return assessedTargetLesion.getSubjectCode();
    }

    @Column(order = 1, columnName = "assessmentWeek", displayName = "RECIST assessment week")
    public Integer getAssessmentWeek() {
        return assessmentWeek;
    }

    @Column(order = 2, columnName = "ntlResponse", displayName = "NTL response")
    public String getNtlResponse() {
        return assessedTargetLesion.getEvent().getNtlResponse();
    }

    @Column(order = 3, columnName = "newLesions", displayName = "New lesions")
    public String getNewLesions() {
        return assessedTargetLesion.getEvent().getNewLesions();
    }

    @Column(order = 4, columnName = "overallResponse", displayName = "Overall response")
    public String getOverallResponse() {
        return assessedTargetLesion.getEvent().getOverallResponse();
    }

    @Column(order = 5, columnName = "bestOverallResponse", displayName = "Best overall response")
    public String getBestOverallResponse() {
        return AssessmentRaw.Response.getShortName(assessedTargetLesion.getEvent().getBestResponse());
    }

}
