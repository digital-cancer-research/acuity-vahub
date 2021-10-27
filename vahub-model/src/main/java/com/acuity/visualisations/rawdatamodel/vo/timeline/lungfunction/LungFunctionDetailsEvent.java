package com.acuity.visualisations.rawdatamodel.vo.timeline.lungfunction;

import com.acuity.visualisations.rawdatamodel.vo.timeline.day.hour.DateDayHour;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@Builder
@ToString(callSuper = true)
public class LungFunctionDetailsEvent implements Serializable {
    private DateDayHour start;
    private Double visitNumber;

    private Double baselineValue;
    private Double valueRaw;
    private String unitRaw;

    // second series (optional)
    private Double valueChangeFromBaseline;
    private String unitChangeFromBaseline;

    // third series (optional)
    private Double valuePercentChangeFromBaseline;
    private String unitPercentChangeFromBaseline;

    private boolean baselineFlag;
}
