package com.acuity.visualisations.rawdatamodel.axes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TAxes<T extends Enum<T>> implements Serializable {
    private T value;
    private Integer intarg;
    private String stringarg;

    public TAxes(T value) {
        this.value = value;
    }
}
