package com.acuity.visualisations.rawdatamodel.filters;

import com.acuity.visualisations.rawdatamodel.util.CombinedQueryBuilder;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.CtDna;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.googlecode.cqengine.query.Query;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Collection;

import static com.acuity.visualisations.rawdatamodel.util.Constants.YES;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.CtDna.ONLY_TRACKED_MUTATIONS;

/**
 * CtDna filter class.
 */

@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class CtDnaFilters extends Filters<CtDna> {

    @JsonIgnore
    public static CtDnaFilters empty() {
        return new CtDnaFilters();
    }

    private SetFilter<String> gene = new SetFilter<>();
    private SetFilter<String> mutation = new SetFilter<>();
    private SetFilter<String> trackedMutation = new SetFilter<>();

    @Override
    public Query<CtDna> getQuery(Collection<String> subjectIds) {
        CombinedQueryBuilder<CtDna> cqb = new CombinedQueryBuilder<>(CtDna.class);
        if (subjectIds != null && !subjectIds.isEmpty()) {
            cqb.add(getFilterQuery(CtDna.Attributes.SUBJECT_ID, new SetFilter<>(subjectIds)));
        }

        // on UI for 'Tracked Mutations' filter there is only one checkbox with the text ONLY_TRACKED_MUTATIONS,
        // that can be either checked or unchecked. In case it is checked, only CtDna events with
        // trackedMutation = 'Yes' must be returned after filtering. Else the filter must not affect the result.
        SetFilter<String> trackedMutationNormalized = new SetFilter<>();
        if (trackedMutation.getValues().contains(ONLY_TRACKED_MUTATIONS)) {
            trackedMutationNormalized.completeWithValue(YES);
        }

        return cqb.add(getFilterQuery(CtDna.Attributes.GENE, gene))
                  .add(getFilterQuery(CtDna.Attributes.MUTATION, mutation))
                  .add(getFilterQuery(CtDna.Attributes.TRACKED_MUTATION, trackedMutationNormalized))
                  .build();
    }
}
