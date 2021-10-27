package com.acuity.visualisations.rawdatamodel.vo.timeline.conmeds;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Data
@EqualsAndHashCode
@ToString
public class ConmedEventsByDrug implements Serializable {
    protected String conmedMedication;
    protected List<ConmedSingleEvent> events;
}
