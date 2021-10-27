package com.acuity.visualisations.rawdatamodel.trellis.grouping;

import com.acuity.visualisations.rawdatamodel.vo.EntityAttribute;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.AssessedNonTargetLesion;

public enum AssessedNonTargetLesionGroupByOptions implements GroupByOption<AssessedNonTargetLesion> {

    SUBJECT(AssessedNonTargetLesion.Attributes.SUBJECT);

    private AssessedNonTargetLesion.Attributes attribute;

    AssessedNonTargetLesionGroupByOptions(AssessedNonTargetLesion.Attributes attribute) {
        this.attribute = attribute;
    }

    @Override
    public EntityAttribute<AssessedNonTargetLesion> getAttribute() {
        return attribute.getAttribute();
    }
}
