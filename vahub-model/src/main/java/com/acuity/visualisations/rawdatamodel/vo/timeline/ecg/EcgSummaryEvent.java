package com.acuity.visualisations.rawdatamodel.vo.timeline.ecg;

import com.acuity.visualisations.rawdatamodel.vo.timeline.day.hour.DateDayHour;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class EcgSummaryEvent extends EcgEvent implements Serializable {

    private Double maxValuePercentChange;
    private String qtcfUnit;
    private Double qtcfValue;
    // change from baseline
    private Double qtcfChange;


    @Builder
    public EcgSummaryEvent(DateDayHour start, Double visitNumber, String abnormality, String significant,
                           Double maxValuePercentChange, String qtcfUnit, Double qtcfValue, Double qtcfChange) {
        super(start, visitNumber, abnormality, significant);
        this.maxValuePercentChange = maxValuePercentChange;
        this.qtcfChange = qtcfChange;
        this.qtcfValue = qtcfValue;
        this.qtcfUnit = qtcfUnit;
    }
}
