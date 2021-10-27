package com.acuity.visualisations.rawdatamodel.trellis.grouping;

import com.acuity.visualisations.rawdatamodel.vo.EntityAttribute;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.MedicalHistory;

public enum MedicalHistoryGroupByOptions implements GroupByOption<MedicalHistory> {

    SUBJECT(MedicalHistory.Attributes.SUBJECT);

    private MedicalHistory.Attributes attribute;

    MedicalHistoryGroupByOptions(MedicalHistory.Attributes attribute) {
        this.attribute = attribute;
    }

    @Override
    public EntityAttribute<MedicalHistory> getAttribute() {
        return attribute.getAttribute();
    }
}
