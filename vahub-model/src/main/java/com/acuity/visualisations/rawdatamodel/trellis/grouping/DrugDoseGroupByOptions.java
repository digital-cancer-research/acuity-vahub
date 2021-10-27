package com.acuity.visualisations.rawdatamodel.trellis.grouping;

import com.acuity.visualisations.rawdatamodel.vo.EntityAttribute;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.DrugDose;

public enum DrugDoseGroupByOptions implements GroupByOption<DrugDose> {

    SUBJECT(DrugDose.Attributes.SUBJECT);

    private DrugDose.Attributes attribute;

    DrugDoseGroupByOptions(DrugDose.Attributes attribute) {
        this.attribute = attribute;
    }

    @Override
    public EntityAttribute<DrugDose> getAttribute() {
        return attribute.getAttribute();
    }
}
