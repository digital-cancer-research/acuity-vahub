package com.acuity.visualisations.rawdatamodel.trellis.grouping;

import com.acuity.visualisations.rawdatamodel.vo.EntityAttribute;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.SurgicalHistory;

public enum SurgicalHistoryGroupByOptions implements GroupByOption<SurgicalHistory> {

    SUBJECT(SurgicalHistory.Attributes.SUBJECT);

    private SurgicalHistory.Attributes attribute;

    SurgicalHistoryGroupByOptions(SurgicalHistory.Attributes attribute) {
        this.attribute = attribute;
    }

    @Override
    public EntityAttribute<SurgicalHistory> getAttribute() {
        return attribute.getAttribute();
    }
}
