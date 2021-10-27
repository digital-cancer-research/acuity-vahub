package com.acuity.visualisations.rawdatamodel.trellis.grouping;

import com.acuity.visualisations.rawdatamodel.vo.EntityAttribute;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.LiverRisk;

public enum LiverRiskGroupByOption implements GroupByOption<LiverRisk> {

    SUBJECT(LiverRisk.Attributes.SUBJECT);

    private LiverRisk.Attributes attribute;

    LiverRiskGroupByOption(LiverRisk.Attributes attribute) {
        this.attribute = attribute;
    }

    @Override
    public EntityAttribute<LiverRisk> getAttribute() {
        return attribute.getAttribute();
    }

}
