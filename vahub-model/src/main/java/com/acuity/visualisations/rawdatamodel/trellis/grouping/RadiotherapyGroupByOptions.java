package com.acuity.visualisations.rawdatamodel.trellis.grouping;

import com.acuity.visualisations.rawdatamodel.vo.EntityAttribute;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Radiotherapy;

public enum RadiotherapyGroupByOptions implements GroupByOption<Radiotherapy> {

    SUBJECT(Radiotherapy.Attributes.SUBJECT);

    private Radiotherapy.Attributes attribute;

    RadiotherapyGroupByOptions(Radiotherapy.Attributes attribute) {
        this.attribute = attribute;
    }

    @Override
    public EntityAttribute<Radiotherapy> getAttribute() {
        return attribute.getAttribute();
    }
}
