package com.acuity.visualisations.rawdatamodel.filters;

import com.acuity.visualisations.rawdatamodel.util.CombinedQueryBuilder;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.SecondTimeOfProgression;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.googlecode.cqengine.query.Query;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Collection;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class SecondTimeOfProgressionFilters extends Filters<SecondTimeOfProgression> {
    @JsonIgnore
    public static SecondTimeOfProgressionFilters empty() {
        return new SecondTimeOfProgressionFilters();
    }

    @Override
    public Query<SecondTimeOfProgression> getQuery(Collection<String> subjectIds) {
        CombinedQueryBuilder<SecondTimeOfProgression> cqb = new CombinedQueryBuilder<>(SecondTimeOfProgression.class);
        if (subjectIds != null && !subjectIds.isEmpty()) {
            cqb.add(getFilterQuery(SecondTimeOfProgression.Attributes.SUBJECT_ID, new SetFilter<>(subjectIds)));
        }
        return cqb.build();
    }
}
