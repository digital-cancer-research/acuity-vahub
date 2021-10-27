package com.acuity.visualisations.rawdatamodel.vo.timeline.dose;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author ksnd199
 */
@Data
@ToString
@Builder
public class DrugDosingSummary implements Serializable {

    private String drug;

    private boolean ongoing;

    private List<DosingSummaryEvent> events;
}
