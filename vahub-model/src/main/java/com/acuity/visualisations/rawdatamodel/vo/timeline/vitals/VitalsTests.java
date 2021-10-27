package com.acuity.visualisations.rawdatamodel.vo.timeline.vitals;

import com.acuity.visualisations.rawdatamodel.vo.timeline.day.hour.DateDayHour;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class VitalsTests implements Serializable {
    
    private String testName;
    private List<VitalsDetailEvent> events;
    private DateDayHour baseline;
}
