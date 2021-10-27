package com.acuity.visualisations.rawdatamodel.filters;

import com.acuity.visualisations.rawdatamodel.util.CombinedQueryBuilder;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.DiseaseExtent;
import com.googlecode.cqengine.query.Query;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Collection;

import static com.acuity.visualisations.rawdatamodel.vo.wrappers.DiseaseExtent.Attributes.RECENT_PROGRESSION_DATE;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class DiseaseExtentFilters extends Filters<DiseaseExtent> {

    public static DiseaseExtentFilters empty() {
        return new DiseaseExtentFilters();
    }

    protected DateRangeFilter recentProgressionDate = new DateRangeFilter();

    @Override
    public Query<DiseaseExtent> getQuery(Collection<String> subjectIds) {
        CombinedQueryBuilder<DiseaseExtent> cqb = new CombinedQueryBuilder<>(DiseaseExtent.class);
        if (subjectIds != null && !subjectIds.isEmpty()) {
            cqb.add(getFilterQuery(DiseaseExtent.Attributes.SUBJECT_ID, new SetFilter<>(subjectIds)));
        }
        return cqb
                .add(getFilterQuery(RECENT_PROGRESSION_DATE, recentProgressionDate))
                .build();
    }
}
