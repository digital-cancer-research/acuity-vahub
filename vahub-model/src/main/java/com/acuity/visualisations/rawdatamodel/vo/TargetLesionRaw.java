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

import static com.acuity.visualisations.rawdatamodel.util.Constants.NOT_IMPLEMENTED;

@Getter
@EqualsAndHashCode(of = "id")
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@AcuityEntity(version = 5)
public class TargetLesionRaw implements HasStringId, HasSubjectId, Serializable {
    private String id;
    private String subjectId;
    @Column(order = 5.5, displayName = "Site of target lesion", type = Column.Type.SSV)
    private String lesionSite;
    @Column(order = 8, displayName = "Lesion diameter (mm)", type = Column.Type.SSV)
    private Integer lesionDiameter;
    @Column(order = 4.5, displayName = "Lesion number", type = Column.Type.SSV)
    private String lesionNumber;
    @Column(order = 1, displayName = "Date assessed", type = Column.Type.SSV)
    private Date lesionDate;
    private Date visitDate;
    @Column(order = 3, displayName = "Visit number", type = Column.Type.SSV)
    private Integer visitNumber;

    private String baselineLesionSite;
    private boolean baseline;
    private int baselineLesionDiameter;
    private Double lesionPercentageChangeFromBaseline;
    private int sumBaselineDiameter;

    private boolean missingsPresent;
    private boolean missingsAtVisitPresent;
    private Integer lesionCountAtVisit;
    private Integer lesionCountAtBaseline;

    @Column(order = 11, displayName = "Sum of diameters (mm)", type = Column.Type.SSV)
    private Integer lesionsDiameterPerAssessment;
    @Column(order = 12, displayName = "% change from baseline", type = Column.Type.SSV)
    private Double sumPercentageChangeFromBaseline;
    private boolean bestPercentageChange;
    private Double sumBestPercentageChangeFromBaseline;
    @Column(order = 13, displayName = "% change from minimum", type = Column.Type.SSV)
    private Double sumPercentageChangeFromMinimum;
    @Column(order = 14, displayName = "Absolute change from minimum", type = Column.Type.SSV)
    private Integer sumChangeFromMinimum;

    @Column(order = 5, columnName = "methodOfAssessment", displayName = "Method of assessment", type = Column.Type.SSV)
    public String getMethodOfAssessment() {
        return NOT_IMPLEMENTED;
    }

    @Column(order = 7, columnName = "locationWithinSiteSpecification", displayName = "Location within site specification", type = Column.Type.SSV)
    public String getLocationWithinSiteSpecification() {
        return NOT_IMPLEMENTED;
    }

    @Column(order = 9, columnName = "lesionNoLongerMeasurable", displayName = "Lesion no longer measurable", type = Column.Type.SSV)
    public String getLesionNoLongerMeasurable() {
        return NOT_IMPLEMENTED;
    }

    @Column(order = 10, columnName = "lesionIntervention", displayName = "Lesion intervention", type = Column.Type.SSV)
    public String getLesionIntervention() {
        return NOT_IMPLEMENTED;
    }

    @Column(order = 15, columnName = "calculatedResponse", displayName = "ACUITY calculated target lesion visit response", type = Column.Type.SSV)
    public String getCalculatedResponse() {
        return NOT_IMPLEMENTED;
    }
}
