package com.acuity.visualisations.rawdatamodel.vo.timeline.aes;

import com.acuity.visualisations.rawdatamodel.vo.AeSeverityRaw;
import com.acuity.visualisations.rawdatamodel.vo.timeline.day.hour.DateDayHour;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.util.Set;

@Data
@EqualsAndHashCode()
@ToString(callSuper = true)
@Builder
public class AeMaxCtcEvent implements Serializable {

    private int maxSeverityGradeNum;
    private String maxSeverityGrade;
    private int numberOfEvents;
    private boolean ongoing;
    private boolean imputedEndDate;
    private Integer duration;
    private DateDayHour start;
    private DateDayHour end;
    private AeSeverityRaw.AeEndType endType;
    private String lastVisitNumber;

    private Set<String> pts;
}
