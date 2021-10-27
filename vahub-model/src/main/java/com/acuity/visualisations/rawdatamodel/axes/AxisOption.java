package com.acuity.visualisations.rawdatamodel.axes;


import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class AxisOption<T extends Enum<T>> implements Serializable {
    private T groupByOption;
    private boolean timestampOption = false;
    private boolean supportsDuration = false;
    private boolean binableOption = false;
    private boolean hasDrugOption = false;

    public AxisOption(T groupByOption) {
        this.groupByOption = groupByOption;
    }
}
