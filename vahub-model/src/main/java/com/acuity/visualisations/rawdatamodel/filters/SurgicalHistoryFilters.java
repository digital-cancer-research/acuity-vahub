package com.acuity.visualisations.rawdatamodel.filters;

import com.acuity.visualisations.rawdatamodel.util.CombinedQueryBuilder;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.SurgicalHistory;
import com.googlecode.cqengine.query.Query;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Collection;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class SurgicalHistoryFilters extends Filters<SurgicalHistory> {
    private SetFilter<String> surgicalProcedure = new SetFilter<>();
    private SetFilter<String> currentMedication = new SetFilter<>();
    private DateRangeFilter start = new DateRangeFilter();
    private SetFilter<String> preferredTerm = new SetFilter<>();
    private SetFilter<String> hlt = new SetFilter<>();
    private SetFilter<String> soc = new SetFilter<>();

    public static SurgicalHistoryFilters empty() {
        return new SurgicalHistoryFilters();
    }

    @Override
    public Query<SurgicalHistory> getQuery(Collection<String> subjectIds) {
        CombinedQueryBuilder<SurgicalHistory> cqb = new CombinedQueryBuilder<>(SurgicalHistory.class);
        if (subjectIds != null && !subjectIds.isEmpty()) {
            cqb.add(getFilterQuery(SurgicalHistory.Attributes.SUBJECT_ID, new SetFilter<>(subjectIds)));
        }
        return cqb.add(getFilterQuery(SurgicalHistory.Attributes.SURGICAL_PROCEDURE, this.surgicalProcedure))
                .add(getFilterQuery(SurgicalHistory.Attributes.CURRENT_MEDICATION, this.currentMedication))
                .add(getFilterQuery(SurgicalHistory.Attributes.START_DATE, this.start))
                .add(getFilterQuery(SurgicalHistory.Attributes.PREFERRED_TERM, this.preferredTerm))
                .add(getFilterQuery(SurgicalHistory.Attributes.HLT, this.hlt))
                .add(getFilterQuery(SurgicalHistory.Attributes.SOC, this.soc))
                .build();
    }
}
