package com.acuity.visualisations.rawdatamodel.vo.exposure;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import static com.acuity.visualisations.rawdatamodel.util.Attributes.defaultNullableValue;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class VisitDose {
    private String dose;
    private Object visit;

    @Getter(lazy = true)
    private final String asString = String.format("%s, visit %s", defaultNullableValue(dose), defaultNullableValue(visit));

    @Override
    public String toString() {
        return getAsString();
    }
}
