package com.acuity.visualisations.rawdatamodel.filters;

import com.acuity.visualisations.rawdatamodel.util.CombinedQueryBuilder;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.AssessedTargetLesion;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.googlecode.cqengine.attribute.Attribute;
import com.googlecode.cqengine.query.Query;
import com.googlecode.cqengine.query.QueryFactory;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Collection;

import static com.acuity.visualisations.rawdatamodel.vo.wrappers.AssessedTargetLesion.Attributes.BEST_PERCENTAGE_CHANGE;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.AssessedTargetLesion.Attributes.BEST_RESPONSE;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.AssessedTargetLesion.Attributes.NON_TARGET_LESIONS_PRESENT;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class AssessedTargetLesionFilters extends Filters<AssessedTargetLesion> {

    @JsonIgnore
    public static AssessedTargetLesionFilters empty() {
        return new AssessedTargetLesionFilters();
    }

    private SetFilter<String> bestResponse = new SetFilter<>();
    private RangeFilter<Double> bestPercentageChangeFromBaseline = new RangeFilter<>();
    private SetFilter<String> nonTargetLesionsPresent = new SetFilter<>();

    @Override
    public Query<AssessedTargetLesion> getQuery(Collection<String> subjectIds) {
        CombinedQueryBuilder<AssessedTargetLesion> cqb = new CombinedQueryBuilder<>(AssessedTargetLesion.class);
        if (subjectIds != null) {
            if (subjectIds.isEmpty()) {
                Attribute cqEngineAttr = AssessedTargetLesion.Attributes.SUBJECT_ID.getAttribute().getCqEngineAttr();
                cqb.add(QueryFactory.in(cqEngineAttr, subjectIds));
            } else {
                cqb.add(getFilterQuery(AssessedTargetLesion.Attributes.SUBJECT_ID, new SetFilter<>(subjectIds)));
            }
        }
        return cqb
                .add(getFilterQuery(BEST_RESPONSE, bestResponse))
                .add(getFilterQuery(BEST_PERCENTAGE_CHANGE, bestPercentageChangeFromBaseline))
                .add(getFilterQuery(NON_TARGET_LESIONS_PRESENT, nonTargetLesionsPresent))
                .build();
    }
}
