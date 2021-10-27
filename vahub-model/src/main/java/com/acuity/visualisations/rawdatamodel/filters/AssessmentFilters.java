package com.acuity.visualisations.rawdatamodel.filters;

import com.acuity.visualisations.rawdatamodel.util.CombinedQueryBuilder;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Assessment;
import com.googlecode.cqengine.query.Query;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Collection;

import static com.acuity.visualisations.rawdatamodel.vo.wrappers.Assessment.Attributes.LESION_SITE;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.Assessment.Attributes.RESPONSE;

@Getter
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class AssessmentFilters extends Filters<Assessment> {

    private SetFilter<String> response = new SetFilter<>();
    private SetFilter<String> lesionSite = new SetFilter<>();

    public static AssessmentFilters empty() {
        return new AssessmentFilters();
    }

    @Override
    public Query<Assessment> getQuery(Collection<String> subjectIds) {
        CombinedQueryBuilder<Assessment> cqb = new CombinedQueryBuilder<>(Assessment.class);
        if (subjectIds != null && !subjectIds.isEmpty()) {
            cqb.add(getFilterQuery(Assessment.Attributes.SUBJECT_ID, new SetFilter<>(subjectIds)));
        }
        return cqb
                .add(getFilterQuery(RESPONSE, response))
                .add(getFilterQuery(LESION_SITE, lesionSite))
                .build();
    }
}
