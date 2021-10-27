package com.acuity.visualisations.rawdatamodel.trellis.grouping;

import com.acuity.visualisations.rawdatamodel.vo.EntityAttribute;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Assessment;

public enum AssessmentGroupByOptions implements GroupByOption<Assessment> {

    SUBJECT(Assessment.Attributes.SUBJECT);

    private Assessment.Attributes attribute;

    AssessmentGroupByOptions(Assessment.Attributes attribute) {
        this.attribute = attribute;
    }

    @Override
    public EntityAttribute<Assessment> getAttribute() {
        return attribute.getAttribute();
    }
}
