package com.acuity.visualisations.rawdatamodel.trellis.grouping;

import com.acuity.visualisations.rawdatamodel.vo.EntityAttribute;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.QtProlongation;

public enum QtProlongationGroupByOptions implements GroupByOption<QtProlongation> {
    SUBJECT(QtProlongation.Attributes.SUBJECT),
    SUBJECT_ID(QtProlongation.Attributes.SUBJECT_ID),
    ALERT_LEVEL(QtProlongation.Attributes.ALERT_LEVEL);

    private QtProlongation.Attributes attribute;

    QtProlongationGroupByOptions(QtProlongation.Attributes attribute) {
        this.attribute = attribute;
    }

    @Override
    public EntityAttribute<QtProlongation> getAttribute() {
        return attribute.getAttribute();
    }
}
