package com.acuity.visualisations.rawdatamodel.filters;

import com.acuity.visualisations.rawdatamodel.util.CombinedQueryBuilder;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.AssessedNonTargetLesion;
import com.googlecode.cqengine.query.Query;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Collection;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class AssessedNonTargetLesionFilters extends Filters<AssessedNonTargetLesion> {

    public static AssessedNonTargetLesionFilters empty() {
        return new AssessedNonTargetLesionFilters();
    }

    @Override
    public Query<AssessedNonTargetLesion> getQuery(Collection<String> subjectIds) {
        CombinedQueryBuilder<AssessedNonTargetLesion> cqb = new CombinedQueryBuilder<>(AssessedNonTargetLesion.class);
        if (subjectIds != null && !subjectIds.isEmpty()) {
            cqb.add(getFilterQuery(AssessedNonTargetLesion.Attributes.SUBJECT_ID, new SetFilter<>(subjectIds)));
        }
        return cqb.build();
    }
}
