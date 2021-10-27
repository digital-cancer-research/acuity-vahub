package com.acuity.visualisations.rawdatamodel.trellis.grouping;

import com.acuity.visualisations.rawdatamodel.vo.EntityAttribute;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.DiseaseExtent;

public enum DiseaseExtentGroupByOptions implements GroupByOption<DiseaseExtent> {

    SUBJECT(DiseaseExtent.Attributes.SUBJECT);

    private DiseaseExtent.Attributes attribute;

    DiseaseExtentGroupByOptions(DiseaseExtent.Attributes attribute) {
        this.attribute = attribute;
    }

    @Override
    public EntityAttribute<DiseaseExtent> getAttribute() {
        return attribute.getAttribute();
    }
}
