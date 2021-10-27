package com.acuity.visualisations.rawdatamodel.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubjectCategoryValue implements Serializable {

    private String subjectId;
    private String category;
    private String value;

    public CategoryValue getCategoryValue() {
        return new CategoryValue(category, value);
    }
}
