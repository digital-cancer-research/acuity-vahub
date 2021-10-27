package com.acuity.visualisations.rawdatamodel.vo.timeline.labs;

import com.acuity.visualisations.rawdatamodel.vo.timeline.day.hour.DateDayHour;
import lombok.Data;

import java.io.Serializable;

@Data
public class LabsSummaryEvent implements Serializable {
    private DateDayHour start;

    private Double visitNumber;

    private int numAboveReferenceRange;
    private int numBelowReferenceRange;

    private int numAboveSeverityThreshold;
    private int numBelowSeverityThreshold;
}
