package com.acuity.visualisations.rawdatamodel.filters;

import com.acuity.visualisations.rawdatamodel.util.CombinedQueryBuilder;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.NonTargetLesion;
import com.googlecode.cqengine.query.Query;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Collection;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class NonTargetLesionFilters extends Filters<NonTargetLesion> {

    public static NonTargetLesionFilters empty() {
        return new NonTargetLesionFilters();
    }

    @Override
    public Query<NonTargetLesion> getQuery(Collection<String> subjectIds) {
        CombinedQueryBuilder<NonTargetLesion> cqb = new CombinedQueryBuilder<>(NonTargetLesion.class);
        if (subjectIds != null && !subjectIds.isEmpty()) {
            cqb.add(getFilterQuery(NonTargetLesion.Attributes.SUBJECT_ID, new SetFilter<>(subjectIds)));
        }
        return cqb.build();
    }
}
