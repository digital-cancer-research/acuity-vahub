package com.acuity.visualisations.rawdatamodel.trellis.grouping;

import com.acuity.visualisations.rawdatamodel.vo.EntityAttribute;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.NonTargetLesion;

public enum NonTargetLesionGroupByOptions implements GroupByOption<NonTargetLesion> {

    SUBJECT(NonTargetLesion.Attributes.SUBJECT);

    private NonTargetLesion.Attributes attribute;

    NonTargetLesionGroupByOptions(NonTargetLesion.Attributes attribute) {
        this.attribute = attribute;
    }

    @Override
    public EntityAttribute<NonTargetLesion> getAttribute() {
        return attribute.getAttribute();
    }
}
