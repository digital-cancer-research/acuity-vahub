package com.acuity.visualisations.rawdatamodel.trellis.grouping;

import com.acuity.visualisations.rawdatamodel.vo.EntityAttribute;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.LiverDiag;

public enum LiverDiagGroupByOptions implements GroupByOption<LiverDiag> {

    SUBJECT(LiverDiag.Attributes.SUBJECT);

    private LiverDiag.Attributes attribute;

    LiverDiagGroupByOptions(LiverDiag.Attributes attribute) {
        this.attribute = attribute;
    }

    @Override
    public EntityAttribute<LiverDiag> getAttribute() {
        return attribute.getAttribute();
    }
}
