package com.acuity.visualisations.rawdatamodel.vo.timeline.labs;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class Categories implements Serializable {
    private String category;
    private List<LabsSummaryEvent> events;
}
