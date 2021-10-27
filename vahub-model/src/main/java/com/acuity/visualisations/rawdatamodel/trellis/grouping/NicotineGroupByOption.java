package com.acuity.visualisations.rawdatamodel.trellis.grouping;

import com.acuity.visualisations.rawdatamodel.vo.EntityAttribute;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Nicotine;

public enum NicotineGroupByOption implements GroupByOption<Nicotine> {

    SUBJECT(Nicotine.Attributes.SUBJECT);

    private Nicotine.Attributes attribute;

    NicotineGroupByOption(Nicotine.Attributes attribute) {
        this.attribute = attribute;
    }

    @Override
    public EntityAttribute<Nicotine> getAttribute() {
        return attribute.getAttribute();
    }
}
