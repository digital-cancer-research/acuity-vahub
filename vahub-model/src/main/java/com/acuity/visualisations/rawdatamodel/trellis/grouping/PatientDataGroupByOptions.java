package com.acuity.visualisations.rawdatamodel.trellis.grouping;

import com.acuity.visualisations.rawdatamodel.trellis.grouping.annotations.BinableOption;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.annotations.TimestampOption;
import com.acuity.visualisations.rawdatamodel.util.Attributes;
import com.acuity.visualisations.rawdatamodel.vo.EntityAttribute;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.PatientData;

public enum PatientDataGroupByOptions implements GroupByOption<PatientData> {

    SUBJECT(PatientData.Attributes.SUBJECT),
    SUBJECT_ID(PatientData.Attributes.SUBJECT_ID),

    @TimestampOption
    @BinableOption
    MEASUREMENT_DATE(PatientData.Attributes.MEASUREMENT_DATE) {

        @Override
        public EntityAttribute<PatientData> getAttribute(Params params) {
            return Attributes.getDateAttribute("MEASUREMENT_DATE", params, e -> e.getEvent().getMeasurementDate());
        }
    };

    private PatientData.Attributes attribute;

    PatientDataGroupByOptions(PatientData.Attributes attribute) {
        this.attribute = attribute;
    }

    @Override
    public EntityAttribute<PatientData> getAttribute() {
        return attribute.getAttribute();
    }
}

