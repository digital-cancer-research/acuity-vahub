package com.acuity.visualisations.rawdatamodel.filters;

import com.acuity.visualisations.rawdatamodel.util.CombinedQueryBuilder;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.SubjectExt;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.googlecode.cqengine.query.Query;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Collection;

/**
 * SubjectExt filter class.
 */

@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class SubjectExtFilters extends Filters<SubjectExt> {

    @JsonIgnore
    public static SubjectExtFilters empty() {
        return new SubjectExtFilters();
    }

    protected DateRangeFilter diagnosisDate = new DateRangeFilter(); // pathology
    protected RangeFilter<Integer> daysFromDiagnosisDate = new RangeFilter<>(); // pathology
    protected DateRangeFilter recentProgressionDate = new DateRangeFilter(); // disease extent

    @Override
    public Query<SubjectExt> getQuery(Collection<String> subjectIds) {
        CombinedQueryBuilder<SubjectExt> cqb = new CombinedQueryBuilder<>(SubjectExt.class);
        if (subjectIds != null && !subjectIds.isEmpty()) {
            cqb.add(getFilterQuery(SubjectExt.Attributes.SUBJECT_ID, new SetFilter<>(subjectIds)));
        }
        return cqb
                .add(getFilterQuery(SubjectExt.Attributes.DIAGNOSIS_DATE, diagnosisDate))
                .add(getFilterQuery(SubjectExt.Attributes.DAYS_FROM_DIAGNOSIS_DATE, daysFromDiagnosisDate))
                .add(getFilterQuery(SubjectExt.Attributes.RECENT_PROGRESSION_DATE, recentProgressionDate))
                .build();
    }
}

