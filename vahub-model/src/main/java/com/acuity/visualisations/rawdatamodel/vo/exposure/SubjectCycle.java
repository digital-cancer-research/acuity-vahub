package com.acuity.visualisations.rawdatamodel.vo.exposure;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder(toBuilder = true)
public class SubjectCycle implements Serializable {
    private String subject;
    private Cycle cycle;
    @Getter(lazy = true)
    private final String asString = String.format("%s, %s", subject, cycle.toString());

    @Override
    public String toString() {
        return getAsString();
    }

}
