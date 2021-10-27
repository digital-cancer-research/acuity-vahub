package com.acuity.visualisations.rawdatamodel.trellis.grouping;

import com.acuity.visualisations.rawdatamodel.vo.EntityAttribute;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Alcohol;

public enum AlcoholGroupByOptions implements GroupByOption<Alcohol> {

    SUBJECT(Alcohol.Attributes.SUBJECT);

    private Alcohol.Attributes attribute;

    AlcoholGroupByOptions(Alcohol.Attributes attribute) {
        this.attribute = attribute;
    }

    @Override
    public EntityAttribute<Alcohol> getAttribute() {
        return attribute.getAttribute();
    }
}
