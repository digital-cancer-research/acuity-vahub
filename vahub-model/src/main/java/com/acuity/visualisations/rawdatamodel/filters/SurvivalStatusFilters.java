package com.acuity.visualisations.rawdatamodel.filters;

import com.acuity.visualisations.rawdatamodel.util.CombinedQueryBuilder;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.SurvivalStatus;
import com.googlecode.cqengine.query.Query;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Collection;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class SurvivalStatusFilters extends Filters<SurvivalStatus> {

    public static SurvivalStatusFilters empty() {
        return new SurvivalStatusFilters();
    }

    @Override
    public Query<SurvivalStatus> getQuery(Collection<String> subjectIds) {
        CombinedQueryBuilder<SurvivalStatus> cqb = new CombinedQueryBuilder<>(SurvivalStatus.class);
        if (subjectIds != null && !subjectIds.isEmpty()) {
            cqb.add(getFilterQuery(SurvivalStatus.Attributes.SUBJECT_ID, new SetFilter<>(subjectIds)));
        }
        return cqb.build();
    }
}
