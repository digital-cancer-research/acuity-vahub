package com.acuity.visualisations.rawdatamodel.trellis.grouping;

import com.acuity.visualisations.rawdatamodel.vo.EntityAttribute;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.TargetLesion;

public enum TargetLesionGroupByOptions implements GroupByOption<TargetLesion> {

    SUBJECT(TargetLesion.Attributes.SUBJECT);

    private TargetLesion.Attributes attribute;

    TargetLesionGroupByOptions(TargetLesion.Attributes attribute) {
        this.attribute = attribute;
    }

    @Override
    public EntityAttribute<TargetLesion> getAttribute() {
        return attribute.getAttribute();
    }
}
