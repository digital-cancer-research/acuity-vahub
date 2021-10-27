package com.acuity.visualisations.rawdatamodel.trellis.grouping;

import com.acuity.visualisations.rawdatamodel.vo.EntityAttribute;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.plots.ChordCalculationObject;

public enum ChordGroupByOptions implements GroupByOption<ChordCalculationObject> {

    START(ChordCalculationObject.Attributes.START),
    END(ChordCalculationObject.Attributes.END);

    private ChordCalculationObject.Attributes attribute;

    ChordGroupByOptions(ChordCalculationObject.Attributes attribute) {
        this.attribute = attribute;
    }

    @Override
    public EntityAttribute<ChordCalculationObject> getAttribute() {
        return attribute.getAttribute();
    }
}
