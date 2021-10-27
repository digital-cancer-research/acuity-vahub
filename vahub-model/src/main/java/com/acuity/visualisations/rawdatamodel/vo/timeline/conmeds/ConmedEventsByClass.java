package com.acuity.visualisations.rawdatamodel.vo.timeline.conmeds;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Data
@EqualsAndHashCode
@ToString
public class ConmedEventsByClass implements Serializable {
    protected String conmedClass;
    protected List<ConmedSummaryEvent> events;
}
