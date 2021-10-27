package com.acuity.visualisations.rest.model.request.respiratory.exacerbation;

import com.acuity.visualisations.common.vo.DayZeroType;
import com.acuity.visualisations.rawdatamodel.axes.TAxes;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class TimelineExacerbationsRequest extends ExacerbationRequest {

    @NotNull
    private TAxes<DayZeroType> dayZero;
}
