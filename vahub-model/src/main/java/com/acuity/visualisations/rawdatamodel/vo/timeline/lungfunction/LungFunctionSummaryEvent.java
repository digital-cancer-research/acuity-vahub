package com.acuity.visualisations.rawdatamodel.vo.timeline.lungfunction;

import com.acuity.visualisations.rawdatamodel.vo.timeline.day.hour.DateDayHour;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString(callSuper = true)
@Builder
public class LungFunctionSummaryEvent implements Serializable {
    private DateDayHour start;
    private Double visitNumber;

    private Double maxValuePercentChange;
}
