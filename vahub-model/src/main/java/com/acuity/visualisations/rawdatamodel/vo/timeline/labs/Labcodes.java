package com.acuity.visualisations.rawdatamodel.vo.timeline.labs;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class Labcodes implements Serializable {

    private String labcode;

    protected Double refLow;
    protected Double refHigh;

    private List<LabsDetailsEvent> events;
}
