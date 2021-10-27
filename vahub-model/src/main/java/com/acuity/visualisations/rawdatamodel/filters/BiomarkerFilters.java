package com.acuity.visualisations.rawdatamodel.filters;

import com.acuity.visualisations.rawdatamodel.util.CombinedQueryBuilder;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Biomarker;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.googlecode.cqengine.query.Query;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Collection;

import static com.acuity.visualisations.rawdatamodel.vo.wrappers.Biomarker.Attributes.GENE;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.Biomarker.Attributes.MUTATION;

/**
 * Biomarkers filter class.
 */

@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class BiomarkerFilters extends Filters<Biomarker> {

    @JsonIgnore
    public static BiomarkerFilters empty() {
        return new BiomarkerFilters();
    }

    private SetFilter<String> gene = new SetFilter<>();
    private SetFilter<String> mutation = new SetFilter<>();
    private RangeFilter<Integer> genePercentage = new RangeFilter<>();

    @Override
    public Query<Biomarker> getQuery(Collection<String> subjectIds) {
        CombinedQueryBuilder<Biomarker> cqb = new CombinedQueryBuilder<>(Biomarker.class);
        if (subjectIds != null && !subjectIds.isEmpty()) {
            cqb.add(getFilterQuery(Biomarker.Attributes.SUBJECT_ID, new SetFilter<>(subjectIds)));
        }
        return cqb.add(getFilterQuery(GENE, gene)).
                add(getFilterQuery(MUTATION, mutation)).
                build();
    }
}
