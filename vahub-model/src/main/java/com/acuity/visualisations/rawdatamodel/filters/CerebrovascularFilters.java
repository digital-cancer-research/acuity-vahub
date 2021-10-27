package com.acuity.visualisations.rawdatamodel.filters;

import com.acuity.visualisations.rawdatamodel.util.CombinedQueryBuilder;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Cerebrovascular;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.googlecode.cqengine.query.Query;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Collection;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class CerebrovascularFilters extends Filters<Cerebrovascular> {

    private SetFilter<String> eventType = new SetFilter<>();
    private DateRangeFilter eventStartDate = new DateRangeFilter();
    private SetFilter<String> eventTerm = new SetFilter<>();
    private SetFilter<String> aeNumber = new SetFilter<>();
    private SetFilter<String> primaryIschemicStroke = new SetFilter<>();
    private SetFilter<String> traumatic = new SetFilter<>();
    private SetFilter<String> symptomsDuration = new SetFilter<>();
    private SetFilter<String> intraHemorrhageLoc = new SetFilter<>();
    private SetFilter<String> intraHemorrhageOtherLoc = new SetFilter<>();
    private SetFilter<String> mrsPriorStroke = new SetFilter<>();
    private SetFilter<String> mrsDuringStrokeHosp = new SetFilter<>();
    private SetFilter<String> mrsCurrVisitOr90DAfterStroke = new SetFilter<>();
    private SetFilter<String> comment = new SetFilter<>();

    @JsonIgnore
    public static CerebrovascularFilters empty() {
        return new CerebrovascularFilters();
    }

    @Override
    public Query<Cerebrovascular> getQuery(Collection<String> subjectIds) {
        CombinedQueryBuilder<Cerebrovascular> cqb = new CombinedQueryBuilder<>(Cerebrovascular.class);
        if (subjectIds != null && !subjectIds.isEmpty()) {
            cqb.add(getFilterQuery(Cerebrovascular.Attributes.SUBJECT_ID, new SetFilter<>(subjectIds)));
        }
        return cqb.add(getFilterQuery(Cerebrovascular.Attributes.AE_NUMBER, aeNumber)).
                add(getFilterQuery(Cerebrovascular.Attributes.EVENT_TERM, eventTerm)).
                add(getFilterQuery(Cerebrovascular.Attributes.EVENT_TYPE, eventType)).
                add(getFilterQuery(Cerebrovascular.Attributes.TRAUMATIC, traumatic)).
                add(getFilterQuery(Cerebrovascular.Attributes.INTRA_HEMORRHAGE_LOC, intraHemorrhageLoc)).
                add(getFilterQuery(Cerebrovascular.Attributes.INTRA_HEMORRHAGE_OTHER_LOC, intraHemorrhageOtherLoc)).
                add(getFilterQuery(Cerebrovascular.Attributes.COMMENT, comment)).
                add(getFilterQuery(Cerebrovascular.Attributes.MRS_CURR_VISIT_OR_90D_AFTER, mrsCurrVisitOr90DAfterStroke)).
                add(getFilterQuery(Cerebrovascular.Attributes.MRS_DURING_STROKE_HOSP, mrsDuringStrokeHosp)).
                add(getFilterQuery(Cerebrovascular.Attributes.MRS_PRIOR_STROKE, mrsPriorStroke)).
                add(getFilterQuery(Cerebrovascular.Attributes.EVENT_START_DATE, eventStartDate)).
                add(getFilterQuery(Cerebrovascular.Attributes.PRIMARY_ISCHEMIC_STROKE, primaryIschemicStroke)).
                add(getFilterQuery(Cerebrovascular.Attributes.SYMPTOMS_DURATION, symptomsDuration)).
                build();
    }
}
