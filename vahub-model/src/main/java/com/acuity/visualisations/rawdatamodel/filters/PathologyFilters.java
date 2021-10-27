package com.acuity.visualisations.rawdatamodel.filters;

import com.acuity.visualisations.rawdatamodel.util.CombinedQueryBuilder;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Pathology;
import com.googlecode.cqengine.query.Query;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Collection;

import static com.acuity.visualisations.rawdatamodel.vo.wrappers.Pathology.Attributes.DAYS_FROM_ORIGINAL_DIAGNOSIS;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.Pathology.Attributes.DIAGNOSIS_DATE;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class PathologyFilters extends Filters<Pathology> {

    public static PathologyFilters empty() {
        return new PathologyFilters();
    }

    protected DateRangeFilter diagnosisDate = new DateRangeFilter();
    protected RangeFilter<Integer> daysFromOriginalDiagnosis = new RangeFilter<>();

    @Override
    public Query<Pathology> getQuery(Collection<String> subjectIds) {
        CombinedQueryBuilder<Pathology> cqb = new CombinedQueryBuilder<>(Pathology.class);
        if (subjectIds != null && !subjectIds.isEmpty()) {
            cqb.add(getFilterQuery(Pathology.Attributes.SUBJECT_ID, new SetFilter<>(subjectIds)));
        }
        return cqb
                .add(getFilterQuery(DIAGNOSIS_DATE, diagnosisDate))
                .add(getFilterQuery(DAYS_FROM_ORIGINAL_DIAGNOSIS, daysFromOriginalDiagnosis))
                .build();
    }
}
