package com.acuity.visualisations.rawdatamodel.trellis.grouping;

import com.acuity.visualisations.rawdatamodel.vo.EntityAttribute;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.SurvivalStatus;

public enum SurvivalStatusGroupByOptions implements GroupByOption<SurvivalStatus> {

    SUBJECT(SurvivalStatus.Attributes.SUBJECT);

    private SurvivalStatus.Attributes attribute;

    SurvivalStatusGroupByOptions(SurvivalStatus.Attributes attribute) {
        this.attribute = attribute;
    }

    @Override
    public EntityAttribute<SurvivalStatus> getAttribute() {
        return attribute.getAttribute();
    }
}
