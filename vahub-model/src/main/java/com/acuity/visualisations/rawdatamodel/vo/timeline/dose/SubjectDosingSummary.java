package com.acuity.visualisations.rawdatamodel.vo.timeline.dose;

import com.acuity.visualisations.rawdatamodel.vo.timeline.SubjectSummary;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author ksnd199
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SubjectDosingSummary extends SubjectSummary implements Serializable {

    private boolean ongoing;

    private List<DosingSummaryEvent> events;
}
