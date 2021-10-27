package com.acuity.visualisations.rawdatamodel.filters;

import com.acuity.visualisations.rawdatamodel.util.CombinedQueryBuilder;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.MedicalHistory;
import com.googlecode.cqengine.query.Query;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Collection;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class MedicalHistoryFilters extends Filters<MedicalHistory> {
    private SetFilter<String> category = new SetFilter<>();
    private SetFilter<String> term = new SetFilter<>();
    private SetFilter<String> conditionStatus = new SetFilter<>();
    private SetFilter<String> currentMedication = new SetFilter<>();
    private DateRangeFilter start = new DateRangeFilter();
    private DateRangeFilter end = new DateRangeFilter();
    private SetFilter<String> preferredTerm = new SetFilter<>();
    private SetFilter<String> hlt = new SetFilter<>();
    private SetFilter<String> soc = new SetFilter<>();

    public static MedicalHistoryFilters empty() {
        return new MedicalHistoryFilters();
    }

    @Override
    public Query<MedicalHistory> getQuery(Collection<String> subjectIds) {
        CombinedQueryBuilder<MedicalHistory> cqb = new CombinedQueryBuilder<>(MedicalHistory.class);
        if (subjectIds != null && !subjectIds.isEmpty()) {
            cqb.add(getFilterQuery(MedicalHistory.Attributes.SUBJECT_ID, new SetFilter<>(subjectIds)));
        }
        return cqb.add(getFilterQuery(MedicalHistory.Attributes.TERM, this.term))
                .add(getFilterQuery(MedicalHistory.Attributes.CATEGORY, this.category))
                .add(getFilterQuery(MedicalHistory.Attributes.CONDITION_STATUS, this.conditionStatus))
                .add(getFilterQuery(MedicalHistory.Attributes.CURRENT_MEDICATION, this.currentMedication))
                .add(getFilterQuery(MedicalHistory.Attributes.START_DATE, this.start))
                .add(getFilterQuery(MedicalHistory.Attributes.END_DATE, this.end))
                .add(getFilterQuery(MedicalHistory.Attributes.PREFERRED_TERM, this.preferredTerm))
                .add(getFilterQuery(MedicalHistory.Attributes.HLT, this.hlt))
                .add(getFilterQuery(MedicalHistory.Attributes.SOC, this.soc))
                .build();
    }
}
