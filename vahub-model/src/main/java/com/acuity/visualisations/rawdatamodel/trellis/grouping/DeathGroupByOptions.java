package com.acuity.visualisations.rawdatamodel.trellis.grouping;

import com.acuity.visualisations.rawdatamodel.vo.EntityAttribute;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Death;

public enum DeathGroupByOptions implements GroupByOption<Death> {

    SUBJECT(Death.Attributes.SUBJECT);

    private Death.Attributes attribute;

    DeathGroupByOptions(Death.Attributes attribute) {
        this.attribute = attribute;
    }

    @Override
    public EntityAttribute<Death> getAttribute() {
        return attribute.getAttribute();
    }
}
