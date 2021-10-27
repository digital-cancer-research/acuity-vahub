package com.acuity.visualisations.rawdatamodel.filters;

import com.acuity.visualisations.rawdatamodel.util.CombinedQueryBuilder;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.LiverRisk;
import com.googlecode.cqengine.query.Query;
import java.util.Collection;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class LiverRiskFilters extends Filters<LiverRisk> {

    private RangeFilter<Integer> potentialHysLawCaseNum = new RangeFilter<>();
    private SetFilter<String> value = new SetFilter<>();
    private SetFilter<String> occurrence = new SetFilter<>();
    private SetFilter<String> referencePeriod = new SetFilter<>();
    private SetFilter<String> details = new SetFilter<>();
    private DateRangeFilter startDate = new DateRangeFilter();
    private DateRangeFilter stopDate = new DateRangeFilter();
    private RangeFilter<Integer> studyDayAtStart = new RangeFilter<>();
    private RangeFilter<Integer> studyDayAtStop = new RangeFilter<>();
    private SetFilter<String> comment = new SetFilter<>();

    public static LiverRiskFilters empty() {
        return new LiverRiskFilters();
    }

    @Override
    public Query<LiverRisk> getQuery(Collection<String> subjectIds) {
        CombinedQueryBuilder<LiverRisk> cqb = new CombinedQueryBuilder<>(LiverRisk.class);
        if (subjectIds != null && !subjectIds.isEmpty()) {
            cqb.add(getFilterQuery(LiverRisk.Attributes.SUBJECT_ID, new SetFilter<>(subjectIds)));
        }
        return cqb.add(getFilterQuery(LiverRisk.Attributes.POTENTIAL_HYS_LAW_CASE_NUM, this.potentialHysLawCaseNum))
                .add(getFilterQuery(LiverRisk.Attributes.VALUE, this.value))
                .add(getFilterQuery(LiverRisk.Attributes.OCCURRENCE, this.occurrence))
                .add(getFilterQuery(LiverRisk.Attributes.REFERENCE_PERIOD, this.referencePeriod))
                .add(getFilterQuery(LiverRisk.Attributes.DETAILS, this.details))
                .add(getFilterQuery(LiverRisk.Attributes.START_DATE, this.startDate))
                .add(getFilterQuery(LiverRisk.Attributes.STOP_DATE, this.stopDate))
                .add(getFilterQuery(LiverRisk.Attributes.STUDY_DAY_AT_START, this.studyDayAtStart))
                .add(getFilterQuery(LiverRisk.Attributes.STUDY_DAY_AT_STOP, this.studyDayAtStop))
                .add(getFilterQuery(LiverRisk.Attributes.COMMENT, this.comment))
                .build();
    }
}
