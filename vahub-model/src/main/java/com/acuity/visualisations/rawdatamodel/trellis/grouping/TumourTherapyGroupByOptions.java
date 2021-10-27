package com.acuity.visualisations.rawdatamodel.trellis.grouping;

import com.acuity.visualisations.rawdatamodel.vo.EntityAttribute;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.TumourTherapy;

/**
 * Group-by options for a combined TumourTherapy class
 */
public enum TumourTherapyGroupByOptions implements GroupByOption<TumourTherapy> {

    START(TumourTherapy.Attributes.WEEKS_TO_START_DATE),
    END(TumourTherapy.Attributes.WEEKS_TO_END_DATE),
    SUBJECT(TumourTherapy.Attributes.SUBJECT),
    MOST_RECENT_THERAPY(TumourTherapy.Attributes.MOST_RECENT_THERAPY),
    ALL_PRIOR_THERAPIES(TumourTherapy.Attributes.ALL_PRIOR_THERAPIES);

    private TumourTherapy.Attributes origin;
    TumourTherapyGroupByOptions(TumourTherapy.Attributes origin) {
        this.origin = origin;
    }

    @Override
    public EntityAttribute<TumourTherapy> getAttribute() {
        return origin.getAttribute();
    }
}
