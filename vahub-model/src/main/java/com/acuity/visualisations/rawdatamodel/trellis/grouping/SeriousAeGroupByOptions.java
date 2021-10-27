package com.acuity.visualisations.rawdatamodel.trellis.grouping;

import com.acuity.visualisations.rawdatamodel.vo.EntityAttribute;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.SeriousAe;

public enum SeriousAeGroupByOptions implements GroupByOption<SeriousAe> {

    SUBJECT(SeriousAe.Attributes.SUBJECT);

    private SeriousAe.Attributes attribute;

    SeriousAeGroupByOptions(SeriousAe.Attributes attribute) {
        this.attribute = attribute;
    }

    @Override
    public EntityAttribute<SeriousAe> getAttribute() {
        return attribute.getAttribute();
    }
}
