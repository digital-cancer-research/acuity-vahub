package com.acuity.visualisations.rawdatamodel.trellis.grouping;

import com.acuity.visualisations.rawdatamodel.vo.EntityAttribute;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Chemotherapy;

public enum ChemotherapyGroupByOptions implements GroupByOption<Chemotherapy> {

    SUBJECT(Chemotherapy.Attributes.SUBJECT),
    PREFERRED_MED(null) {
        @Override
        public EntityAttribute<Chemotherapy> getAttribute() {
            return EntityAttribute.attribute("preferredMed",
                    e -> e.getEvent().getPreferredMedOrEmpty());
        }
    };

    private Chemotherapy.Attributes attribute;

    ChemotherapyGroupByOptions(Chemotherapy.Attributes attribute) {
        this.attribute = attribute;
    }

    @Override
    public EntityAttribute<Chemotherapy> getAttribute() {
        return attribute.getAttribute();
    }
}
