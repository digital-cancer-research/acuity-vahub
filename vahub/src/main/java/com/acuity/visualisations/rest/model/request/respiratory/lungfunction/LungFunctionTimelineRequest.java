package com.acuity.visualisations.rest.model.request.respiratory.lungfunction;

import com.acuity.visualisations.common.vo.DayZeroType;
import com.acuity.visualisations.rawdatamodel.axes.TAxes;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class LungFunctionTimelineRequest extends LungFunctionRequest {
    @NotNull
    private TAxes<DayZeroType> dayZero;
}
