package com.acuity.visualisations.rawdatamodel.vo.timeline.labs;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;


@EqualsAndHashCode(callSuper = true)
@Data
public class LabsDetailsEvent extends LabsSummaryEvent implements Serializable {

    private String id;
    private String labcode;

    private Double baselineValue;
    private Boolean baselineFlag;
    private Double valueRaw;
    private String unitRaw;

    private Double valueChangeFromBaseline;
    private String unitChangeFromBaseline;

    private Double valuePercentChangeFromBaseline;
    private String unitPercentChangeFromBaseline;
}
