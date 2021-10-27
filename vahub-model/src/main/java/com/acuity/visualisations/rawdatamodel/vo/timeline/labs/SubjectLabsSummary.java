package com.acuity.visualisations.rawdatamodel.vo.timeline.labs;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class SubjectLabsSummary extends SubjectLabs implements Serializable {
    private List<LabsSummaryEvent> events;
}
