package com.acuity.visualisations.rawdatamodel.vo.timeline.dose;

import com.acuity.visualisations.rawdatamodel.vo.timeline.EventInterval;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;


@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DosingSummaryEvent extends EventInterval implements Serializable {

    private PercentChange percentChange = PercentChange.inactive();

    private PeriodType periodType;

    private PeriodType subsequentPeriodType;

    private List<DoseAndFrequency> drugDoses;

    @JsonIgnore
    public boolean isActive() {
        return (periodType == PeriodType.ACTIVE) || (periodType == null && percentChange != null && percentChange.isActive());
    }
}
