package com.acuity.visualisations.rawdatamodel.filters;

import com.acuity.visualisations.rawdatamodel.util.CombinedQueryBuilder;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.TargetLesion;
import com.googlecode.cqengine.query.Query;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Collection;

@Getter
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class TargetLesionFilters extends Filters<TargetLesion> {

    public static TargetLesionFilters empty() {
        return new TargetLesionFilters();
    }

    @Override
    public Query<TargetLesion> getQuery(Collection<String> subjectIds) {
        CombinedQueryBuilder<TargetLesion> cqb = new CombinedQueryBuilder<>(TargetLesion.class);
        if (subjectIds != null && !subjectIds.isEmpty()) {
            cqb.add(getFilterQuery(TargetLesion.Attributes.SUBJECT_ID, new SetFilter<>(subjectIds)));
        }
        return cqb.build();
    }
}
