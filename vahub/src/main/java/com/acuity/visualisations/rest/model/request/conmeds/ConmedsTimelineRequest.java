package com.acuity.visualisations.rest.model.request.conmeds;

import com.acuity.visualisations.common.vo.DayZeroType;
import com.acuity.visualisations.rawdatamodel.axes.TAxes;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class ConmedsTimelineRequest extends ConmedsRequest {
    @NotNull
    private TAxes<DayZeroType> dayZero;
}
