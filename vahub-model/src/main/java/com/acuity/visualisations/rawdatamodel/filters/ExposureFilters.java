package com.acuity.visualisations.rawdatamodel.filters;

import com.acuity.visualisations.rawdatamodel.util.CombinedQueryBuilder;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Exposure;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.googlecode.cqengine.query.Query;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Collection;

import static com.acuity.visualisations.rawdatamodel.vo.wrappers.Exposure.Attributes.ANALYTE;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.Exposure.Attributes.ANALYTE_CONCENTRATION;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.Exposure.Attributes.ANALYTE_UNIT;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.Exposure.Attributes.DAY;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.Exposure.Attributes.TIME_FROM_ADMINISTRATION;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.Exposure.Attributes.TREATMENT_CYCLE;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.Exposure.Attributes.VISIT;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.Exposure.Attributes.TREATMENT;
import static com.google.common.collect.Lists.newArrayList;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class ExposureFilters extends Filters<Exposure> {

    @JsonIgnore
    public static ExposureFilters empty() {
        return new ExposureFilters();
    }

    private SetFilter<String> analyte = new SetFilter<>();
    private SetFilter<String> treatment = new SetFilter<>();
    private RangeFilter<Double> analyteConcentration = new RangeFilter<>();
    private SetFilter<String> analyteUnit = new SetFilter<>();
    private RangeFilter<Double> timeFromAdministration = new RangeFilter<>();
    private SetFilter<String> treatmentCycle = new SetFilter<>();
    private SetFilter<Integer> visit = new SetFilter<>();
    private SetFilter<Integer> day = new SetFilter<>();

    @Override
    public Query<Exposure> getQuery() {
        return getQuery(newArrayList());
    }

    public Query<Exposure> getQuery(Collection<String> subjectIds) {

        CombinedQueryBuilder<Exposure> cqb = new CombinedQueryBuilder<>(Exposure.class);
        if (subjectIds != null && !subjectIds.isEmpty()) {
            cqb.add(getFilterQuery(Exposure.Attributes.SUBJECT_ID, new SetFilter<>(subjectIds)));
        }
        return cqb.add(getFilterQuery(ANALYTE, analyte)).
                add(getFilterQuery(ANALYTE_CONCENTRATION, analyteConcentration)).
                add(getFilterQuery(ANALYTE_UNIT, analyteUnit)).
                add(getFilterQuery(TIME_FROM_ADMINISTRATION, timeFromAdministration)).
                add(getFilterQuery(TREATMENT_CYCLE, treatmentCycle)).
                add(getFilterQuery(VISIT, visit)).
                add(getFilterQuery(TREATMENT, treatment)).
                add(getFilterQuery(DAY, day)).
                build();
    }
}
