package com.acuity.visualisations.rawdatamodel.trellis.grouping;

import com.acuity.visualisations.rawdatamodel.vo.EntityAttribute;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.SecondTimeOfProgression;

public enum SecondTimeOfProgressionGroupByOptions implements GroupByOption<SecondTimeOfProgression> {

    SUBJECT(SecondTimeOfProgression.Attributes.SUBJECT);

    private SecondTimeOfProgression.Attributes attribute;

    SecondTimeOfProgressionGroupByOptions(SecondTimeOfProgression.Attributes attribute) {
        this.attribute = attribute;
    }

    @Override
    public EntityAttribute<SecondTimeOfProgression> getAttribute() {
        return attribute.getAttribute();
    }
}
