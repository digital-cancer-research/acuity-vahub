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
import java.util.UUID;

import static com.acuity.visualisations.rawdatamodel.util.Constants.NOT_IMPLEMENTED;
import static com.acuity.visualisations.rawdatamodel.util.DaysUtil.YMD;

@Getter
@ToString
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@AcuityEntity(version = 1)
public class AssessedNonTargetLesionRaw implements HasStringId, HasSubjectId, Serializable {

    @Builder.Default
    private String id = UUID.randomUUID().toString();
    private String subjectId;
    @Column(order = 6, displayName = "Response at assessment", type = Column.Type.SSV)
    private String response;
    private NonTargetLesionRaw nonTargetLesionRaw;
    private AssessmentRaw assessmentRaw;

    @Column(order = 1, columnName = "lesionDate", displayName = "Date assessed", type = Column.Type.SSV, dateFormat = YMD)
    public Date getLesionDate() {
        return nonTargetLesionRaw.getLesionDate();
    }

    @Column(order = 3, columnName = "visitNumber", displayName = "Visit number", type = Column.Type.SSV)
    public Integer getVisitNumber() {
        return nonTargetLesionRaw.getVisitNumber();
    }

    @Column(order = 4, columnName = "lesionSite", displayName = "Site of lesion", type = Column.Type.SSV)
    public String getLesionSite() {
        return nonTargetLesionRaw.getLesionSite();
    }

    @Column(order = 5, columnName = "assessmentMethod", displayName = "Method of assessment", type = Column.Type.SSV)
    public String getAssessmentMethod() {
        return NOT_IMPLEMENTED;
    }


}
