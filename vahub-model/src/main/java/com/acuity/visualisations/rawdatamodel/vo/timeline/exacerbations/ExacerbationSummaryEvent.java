package com.acuity.visualisations.rawdatamodel.vo.timeline.exacerbations;

import com.acuity.visualisations.rawdatamodel.vo.timeline.day.hour.DateDayHour;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@EqualsAndHashCode
@ToString
@Builder
public class ExacerbationSummaryEvent implements Serializable {
    private Integer numberOfDoseReceived;
    private Object severityGrade;
    private DateDayHour start;
    private DateDayHour end;
    private boolean ongoing;
    private boolean imputedEndDate;
    private Integer duration;
}
