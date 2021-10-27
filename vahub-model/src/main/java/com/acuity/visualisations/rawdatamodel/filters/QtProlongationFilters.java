package com.acuity.visualisations.rawdatamodel.filters;

import com.acuity.visualisations.rawdatamodel.util.CombinedQueryBuilder;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.QtProlongation;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.googlecode.cqengine.query.Query;
import java.util.Collection;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class QtProlongationFilters extends Filters<QtProlongation> {
    @JsonIgnore
    public static QtProlongationFilters empty() {
        return new QtProlongationFilters();
    }

    @Override
    public Query<QtProlongation> getQuery(Collection<String> subjectIds) {
        CombinedQueryBuilder<QtProlongation> cqb = new CombinedQueryBuilder<>(QtProlongation.class);
        if (subjectIds != null && !subjectIds.isEmpty()) {
            cqb.add(getFilterQuery(QtProlongation.Attributes.SUBJECT_ID, new SetFilter<>(subjectIds)));
        }
        return cqb.build();
    }
}
