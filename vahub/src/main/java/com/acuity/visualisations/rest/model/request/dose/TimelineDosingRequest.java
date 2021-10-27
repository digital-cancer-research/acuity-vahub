package com.acuity.visualisations.rest.model.request.dose;

import com.acuity.visualisations.common.vo.DayZeroType;
import com.acuity.visualisations.rawdatamodel.axes.TAxes;
import com.acuity.visualisations.rawdatamodel.vo.timeline.dose.MaxDoseType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class TimelineDosingRequest extends DrugDoseRequest {

    @NotNull
    private TAxes<DayZeroType> dayZero;
    private MaxDoseType maxDoseType;
}
