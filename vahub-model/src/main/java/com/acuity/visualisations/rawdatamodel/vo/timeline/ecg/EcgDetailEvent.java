package com.acuity.visualisations.rawdatamodel.vo.timeline.ecg;

import com.acuity.visualisations.rawdatamodel.vo.timeline.day.hour.DateDayHour;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class EcgDetailEvent extends EcgEvent implements Serializable {
    private Double baselineValue;
    private Double valueRaw;
    private String unitRaw;
    private Double valueChangeFromBaseline;
    private String unitChangeFromBaseline;
    private Double valuePercentChangeFromBaseline;
    private String unitPercentChangeFromBaseline;
    private boolean baselineFlag;

    @Builder
    public EcgDetailEvent(DateDayHour start, Double visitNumber, String abnormality, String significant,
                          Double baselineValue, Double valueRaw, String unitRaw, Double valueChangeFromBaseline,
                          String unitChangeFromBaseline, Double valuePercentChangeFromBaseline,
                          String unitPercentChangeFromBaseline, boolean baselineFlag) {
        super(start, visitNumber, abnormality, significant);
        this.baselineValue = baselineValue;
        this.valueRaw = valueRaw;
        this.unitRaw = unitRaw;
        this.valueChangeFromBaseline = valueChangeFromBaseline;
        this.unitChangeFromBaseline = unitChangeFromBaseline;
        this.valuePercentChangeFromBaseline = valuePercentChangeFromBaseline;
        this.unitPercentChangeFromBaseline = unitPercentChangeFromBaseline;
        this.baselineFlag = baselineFlag;
    }
}
