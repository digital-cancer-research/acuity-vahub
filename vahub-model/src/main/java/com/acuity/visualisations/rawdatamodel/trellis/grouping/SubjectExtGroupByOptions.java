package com.acuity.visualisations.rawdatamodel.trellis.grouping;

import com.acuity.visualisations.rawdatamodel.vo.EntityAttribute;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.SubjectExt;

public enum SubjectExtGroupByOptions implements GroupByOption<SubjectExt> {

    SUBJECT(SubjectExt.Attributes.SUBJECT_ID);

    private SubjectExt.Attributes attribute;

    SubjectExtGroupByOptions(SubjectExt.Attributes attribute) {
        this.attribute = attribute;
    }

    @Override
    public EntityAttribute<SubjectExt> getAttribute() {
        return attribute.getAttribute();
    }
}
