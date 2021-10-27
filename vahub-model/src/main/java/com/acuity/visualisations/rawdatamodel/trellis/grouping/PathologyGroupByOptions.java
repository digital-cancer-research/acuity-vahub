package com.acuity.visualisations.rawdatamodel.trellis.grouping;

import com.acuity.visualisations.rawdatamodel.vo.EntityAttribute;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Pathology;

public enum PathologyGroupByOptions implements GroupByOption<Pathology> {

    SUBJECT(Pathology.Attributes.SUBJECT);

    private Pathology.Attributes attribute;

    PathologyGroupByOptions(Pathology.Attributes attribute) {
        this.attribute = attribute;
    }

    @Override
    public EntityAttribute<Pathology> getAttribute() {
        return attribute.getAttribute();
    }
}
