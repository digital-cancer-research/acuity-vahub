package com.acuity.visualisations.rawdatamodel.axes;


import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class AxisOptions<T extends Enum<T>> implements Serializable {
    private List<AxisOption<T>> options;
    private boolean hasRandomization;
    private List<String> drugs;
}
