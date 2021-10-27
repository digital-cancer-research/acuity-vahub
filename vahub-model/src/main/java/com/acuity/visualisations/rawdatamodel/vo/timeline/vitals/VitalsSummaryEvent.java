package com.acuity.visualisations.rawdatamodel.vo.timeline.vitals;

import com.acuity.visualisations.rawdatamodel.vo.timeline.day.hour.DateDayHour;
import lombok.Data;

import java.io.Serializable;

@Data
public class VitalsSummaryEvent implements Serializable {
    private Double visitNumber;
    private DateDayHour start;

    private Double maxValuePercentChange;
}
