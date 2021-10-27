package com.acuity.visualisations.rawdatamodel.vo.exposure;

import com.acuity.visualisations.rawdatamodel.vo.wrappers.Exposure;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

import static com.acuity.visualisations.rawdatamodel.util.Attributes.defaultNullableValue;

@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Getter
public class ExposureData implements Serializable {

    private String subject;
    private String treatmentCycle;
    private String analyte;
    private String visit;
    private String dose;
    private String day;

    public ExposureData(SubjectCycle subjectCycle, Exposure event) {
        this.subject = subjectCycle.getSubject();
        this.treatmentCycle = defaultNullableValue(subjectCycle.getCycle().getTreatmentCycle()).toString();
        this.analyte = defaultNullableValue(subjectCycle.getCycle().getAnalyte()).toString();
        this.visit = defaultNullableValue(subjectCycle.getCycle().getVisit()).toString();
        this.dose = defaultNullableValue(event.getEvent().getTreatment()).toString();
        this.day = defaultNullableValue(event.getEvent().getProtocolScheduleDay()).toString();
    }
}
