package com.acuity.visualisations.rawdatamodel.filters;

import com.acuity.visualisations.rawdatamodel.util.CombinedQueryBuilder;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.PkResult;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.googlecode.cqengine.query.Query;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Collection;

import static com.acuity.visualisations.rawdatamodel.vo.wrappers.PkResult.Attributes.ANALYTE;

/**
 * PkResult filter class.
 */

@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class PkResultFilters extends Filters<PkResult> {

    @JsonIgnore
    public static PkResultFilters empty() {
        return new PkResultFilters();
    }

    private SetFilter<String> analyte = new SetFilter<>();

    @Override
    public Query<PkResult> getQuery(Collection<String> subjectIds) {
        CombinedQueryBuilder<PkResult> cqb = new CombinedQueryBuilder<>(PkResult.class);
        if (subjectIds != null && !subjectIds.isEmpty()) {
            cqb.add(getFilterQuery(PkResult.Attributes.SUBJECT_ID, new SetFilter<>(subjectIds)));
        }
        return cqb.add(getFilterQuery(ANALYTE, analyte)).build();
    }
}
