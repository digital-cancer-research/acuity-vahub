package com.acuity.visualisations.rawdatamodel.vo.timeline.vitals;

import com.acuity.visualisations.rawdatamodel.vo.timeline.day.hour.DateDayHour;
import lombok.Data;

import java.io.Serializable;


@Data
public class VitalsDetailEvent implements Serializable {
    private Double visitNumber;
    private DateDayHour start;

    private Double baselineValue;
    private Double valueRaw;
    private String unitRaw;
    private Double valueChangeFromBaseline;
    private String unitChangeFromBaseline;
    private Double valuePercentChangeFromBaseline;
    private String unitPercentChangeFromBaseline;
    private boolean baselineFlag;
}
