package com.acuity.visualisations.rawdatamodel.filters;

import com.acuity.visualisations.rawdatamodel.util.CombinedQueryBuilder;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.LiverDiag;
import com.googlecode.cqengine.query.Query;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Collection;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class LiverDiagFilters extends Filters<LiverDiag> {

    public static LiverDiagFilters empty() {
        return new LiverDiagFilters();
    }

    protected SetFilter<String> liverDiagInv = new SetFilter<>();
    protected SetFilter<String> liverDiagInvSpec = new SetFilter<>();
    protected DateRangeFilter liverDiagInvDate = new DateRangeFilter();
    protected RangeFilter<Integer> studyDayLiverDiagInv = new RangeFilter<>();
    protected SetFilter<String> liverDiagInvResult = new SetFilter<>();
    protected RangeFilter<Integer> potentialHysLawCaseNum = new RangeFilter<>();

    @Override
    public Query<LiverDiag> getQuery(Collection<String> subjectIds) {
        CombinedQueryBuilder<LiverDiag> cqb = new CombinedQueryBuilder<>(LiverDiag.class);
        if (subjectIds != null && !subjectIds.isEmpty()) {
            cqb.add(getFilterQuery(LiverDiag.Attributes.SUBJECT_ID, new SetFilter<>(subjectIds)));
        }
        return cqb
                .add(getFilterQuery(LiverDiag.Attributes.LIVER_DIAG_INVESTIGATION, liverDiagInv))
                .add(getFilterQuery(LiverDiag.Attributes.LIVER_DIAG_INVESTIGATION_SPEC, liverDiagInvSpec))
                .add(getFilterQuery(LiverDiag.Attributes.LIVER_DIAG_INVESTIGATION_DATE, liverDiagInvDate))
                .add(getFilterQuery(LiverDiag.Attributes.STUDY_DAY_LIVER_DIAG_INVESTIGATION, studyDayLiverDiagInv))
                .add(getFilterQuery(LiverDiag.Attributes.LIVER_DIAG_INVESTIGATION_RESULT, liverDiagInvResult))
                .add(getFilterQuery(LiverDiag.Attributes.POTENTIAL_HYS_LAW_CASE_NUM, potentialHysLawCaseNum))
                .build();
    }
}
