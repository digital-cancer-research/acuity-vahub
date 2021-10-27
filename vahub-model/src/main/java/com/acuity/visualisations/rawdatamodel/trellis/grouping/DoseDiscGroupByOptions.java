package com.acuity.visualisations.rawdatamodel.trellis.grouping;

import com.acuity.visualisations.rawdatamodel.vo.EntityAttribute;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.DoseDisc;

public enum DoseDiscGroupByOptions implements GroupByOption<DoseDisc> {

    SUBJECT(DoseDisc.Attributes.SUBJECT);

    private DoseDisc.Attributes attribute;

    DoseDiscGroupByOptions(DoseDisc.Attributes attribute) {
        this.attribute = attribute;
    }

    @Override
    public EntityAttribute<DoseDisc> getAttribute() {
        return attribute.getAttribute();
    }
}
