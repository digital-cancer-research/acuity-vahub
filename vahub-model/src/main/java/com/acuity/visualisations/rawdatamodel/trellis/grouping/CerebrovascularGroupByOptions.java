package com.acuity.visualisations.rawdatamodel.trellis.grouping;

import com.acuity.visualisations.rawdatamodel.trellis.grouping.annotations.BinableOption;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.annotations.TimestampOption;
import com.acuity.visualisations.rawdatamodel.util.Attributes;
import com.acuity.visualisations.rawdatamodel.vo.EntityAttribute;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Cerebrovascular;

public enum CerebrovascularGroupByOptions implements GroupByOption<Cerebrovascular> {
    EVENT_TYPE(Cerebrovascular.Attributes.EVENT_TYPE),
    PRIMARY_ISCHEMIC_STROKE(Cerebrovascular.Attributes.PRIMARY_ISCHEMIC_STROKE),
    INTRA_HEMORRHAGE_LOC(Cerebrovascular.Attributes.INTRA_HEMORRHAGE_LOC),
    SYMPTOMS_DURATION(Cerebrovascular.Attributes.SYMPTOMS_DURATION),
    TRAUMATIC(Cerebrovascular.Attributes.TRAUMATIC),
    MRS_PRIOR_STROKE(Cerebrovascular.Attributes.MRS_PRIOR_STROKE),
    MRS_DURING_STROKE_HOSP(Cerebrovascular.Attributes.MRS_DURING_STROKE_HOSP),
    MRS_CURR_VISIT_OR_90D_AFTER(Cerebrovascular.Attributes.MRS_CURR_VISIT_OR_90D_AFTER),
    @BinableOption
    @TimestampOption
    START_DATE(Cerebrovascular.Attributes.EVENT_START_DATE) {
        @Override
        public EntityAttribute<Cerebrovascular> getAttribute(Params params) {
            return params == null ? getAttribute() : Attributes.getBinnedAttribute("START_DATE", params, Cerebrovascular::getStartDate);
        }
    };

    private Cerebrovascular.Attributes originAttribute;

    CerebrovascularGroupByOptions(Cerebrovascular.Attributes attribute) {
        this.originAttribute = attribute;
    }

    @Override
    public EntityAttribute<Cerebrovascular> getAttribute() {
        return originAttribute.getAttribute();
    }

}
