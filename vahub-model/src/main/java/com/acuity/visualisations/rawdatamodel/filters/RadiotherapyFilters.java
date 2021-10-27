package com.acuity.visualisations.rawdatamodel.filters;

import com.acuity.visualisations.rawdatamodel.util.CombinedQueryBuilder;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Radiotherapy;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.googlecode.cqengine.query.Query;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Collection;

import static com.acuity.visualisations.rawdatamodel.vo.wrappers.Radiotherapy.Attributes.RADIATION_DOSE;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.Radiotherapy.Attributes.THERAPY_STATUS;
import static com.googlecode.cqengine.query.QueryFactory.none;

/**
 * Radiotherapy filter class.
 */

@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
@NoArgsConstructor
public class RadiotherapyFilters extends Filters<Radiotherapy> {

    @JsonIgnore
    public static RadiotherapyFilters empty() {
        return new RadiotherapyFilters();
    }

    @JsonIgnore
    public RadiotherapyFilters(boolean enabled) {
        this();
        this.isRadiotherapyEnabled = enabled;

    }

    protected SetFilter<String> therapyStatus = new SetFilter<>();
    protected RangeFilter<Double> radiationDose = new RangeFilter<>();

    @JsonIgnore
    private boolean isRadiotherapyEnabled = true;

    @Override
    public Query<Radiotherapy> getQuery(Collection<String> subjectIds) {
        CombinedQueryBuilder<Radiotherapy> cqb = new CombinedQueryBuilder<>(Radiotherapy.class);
        if (subjectIds != null && !subjectIds.isEmpty()) {
            cqb.add(getFilterQuery(Radiotherapy.Attributes.SUBJECT_ID, new SetFilter<>(subjectIds)));
        }
        if (!isRadiotherapyEnabled) {
            return cqb.add(none(Radiotherapy.class)).build();
        }
        return cqb
                .add(getFilterQuery(RADIATION_DOSE, radiationDose))
                .add(getFilterQuery(THERAPY_STATUS, therapyStatus))
                .build();
    }
}
