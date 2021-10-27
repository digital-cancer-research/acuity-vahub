package com.acuity.visualisations.rawdatamodel.filters;

import com.acuity.visualisations.rawdatamodel.util.CombinedQueryBuilder;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.DoseDisc;
import com.googlecode.cqengine.query.Query;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Collection;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class DoseDiscFilters extends Filters<DoseDisc> {
    private SetFilter<String> studyDrug = new SetFilter<>();
    private DateRangeFilter discDate = new DateRangeFilter();
    private RangeFilter<Integer> studyDayAtDisc = new RangeFilter<>();
    private SetFilter<String> discMainReason = new SetFilter<>();
    private SetFilter<String> discSpec = new SetFilter<>();
    private SetFilter<String> subjectDecisionSpec = new SetFilter<>();
    private SetFilter<String> subjectDecisionSpecOther = new SetFilter<>();

    public static DoseDiscFilters empty() {
        return new DoseDiscFilters();
    }

    @Override
    public Query<DoseDisc> getQuery(Collection<String> subjectIds) {
        CombinedQueryBuilder<DoseDisc> cqb = new CombinedQueryBuilder<>(DoseDisc.class);
        if (subjectIds != null && !subjectIds.isEmpty()) {
            cqb.add(getFilterQuery(DoseDisc.Attributes.SUBJECT_ID, new SetFilter<>(subjectIds)));
        }
        return cqb.add(getFilterQuery(DoseDisc.Attributes.STUDY_DRUG, this.studyDrug))
                .add(getFilterQuery(DoseDisc.Attributes.DISC_DATE, this.discDate))
                .add(getFilterQuery(DoseDisc.Attributes.DISC_REASON, this.discMainReason))
                .add(getFilterQuery(DoseDisc.Attributes.DISC_SPEC, this.discSpec))
                .add(getFilterQuery(DoseDisc.Attributes.SUBJECT_DECISION_SPEC, this.subjectDecisionSpec))
                .add(getFilterQuery(DoseDisc.Attributes.SUBJECT_DECISION_SPEC_OTHER, this.subjectDecisionSpecOther))
                .add(getFilterQuery(DoseDisc.Attributes.STUDY_DAY_AT_DISC, this.studyDayAtDisc))
                .build();
    }
}
