package com.acuity.visualisations.rawdatamodel.trellis.grouping;

import com.acuity.visualisations.rawdatamodel.vo.EntityAttribute;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Liver;

public enum LiverGroupByOptions implements GroupByOption<Liver> {
    SUBJECT(Liver.Attributes.SUBJECT),
    MEASUREMENT(Liver.Attributes.NORMALIZED_LAB_CODE),
    ARM(Liver.Attributes.ARM);

    private Liver.Attributes attribute;

    LiverGroupByOptions(Liver.Attributes attribute) {
        this.attribute = attribute;
    }

    @Override
    public EntityAttribute<Liver> getAttribute() {
        return attribute.getAttribute();
    }
}
