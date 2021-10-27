package com.acuity.visualisations.rawdatamodel.vo.exposure;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import static com.acuity.visualisations.rawdatamodel.util.Attributes.defaultNullableValue;

@Getter
@EqualsAndHashCode
public class CycleDose {
    private String dose;
    private String cycle;

    public CycleDose(String dose, String cycle) {
        this.dose = defaultNullableValue(dose).toString();
        this.cycle = defaultNullableValue(cycle).toString();
    }

    @Getter(lazy = true)
    private final String asString = dose + ", " + cycle;

    @Override
    public String toString() {
        return getAsString();
    }
}
