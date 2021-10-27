package com.acuity.visualisations.rawdatamodel.vo.pkresult;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serializable;

import static com.acuity.visualisations.rawdatamodel.util.Attributes.defaultNullableValue;

@Getter
@EqualsAndHashCode
public class CycleDay implements Serializable {
    private String cycle;
    private String day;

    public CycleDay(String cycle, String day) {
        this.cycle = defaultNullableValue(cycle).toString();
        this.day = defaultNullableValue(day).toString();
    }

    @Getter(lazy = true)
    private final String asString = String.format("[%s, %s]", cycle, day);

    @Override
    public String toString() {
        return getAsString();
    }
}
