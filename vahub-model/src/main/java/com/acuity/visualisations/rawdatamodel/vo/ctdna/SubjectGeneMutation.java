package com.acuity.visualisations.rawdatamodel.vo.ctdna;

import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;


import static com.acuity.visualisations.rawdatamodel.util.Attributes.defaultNullableValue;

@Getter
@EqualsAndHashCode
@NoArgsConstructor
public class SubjectGeneMutation implements Serializable {
    private String subjectCode;
    private String gene;
    private String mutation;

    public SubjectGeneMutation(String subjectCode, String gene, String mutation) {
        this.subjectCode = subjectCode;
        this.gene = defaultNullableValue(gene).toString();
        this.mutation = defaultNullableValue(mutation).toString();
    }

    @Getter(lazy = true)
    private final String asString = String.format("%s, %s, %s", subjectCode, gene, mutation);

    @Override
    public String toString() {
        return getAsString();
    }
}
