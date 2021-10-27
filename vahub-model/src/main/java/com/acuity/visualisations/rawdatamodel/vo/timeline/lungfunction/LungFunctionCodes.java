package com.acuity.visualisations.rawdatamodel.vo.timeline.lungfunction;

import com.acuity.visualisations.rawdatamodel.vo.timeline.day.hour.DateDayHour;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
public class LungFunctionCodes implements Serializable {
    
    private String code;
    private List<LungFunctionDetailsEvent> events;
    private DateDayHour baseline;
}
