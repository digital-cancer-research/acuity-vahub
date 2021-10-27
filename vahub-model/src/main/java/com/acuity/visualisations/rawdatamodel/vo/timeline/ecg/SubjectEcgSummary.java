package com.acuity.visualisations.rawdatamodel.vo.timeline.ecg;

import com.acuity.visualisations.rawdatamodel.vo.timeline.day.hour.DateDayHour;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class SubjectEcgSummary extends SubjectEcg implements Serializable {
    private List<EcgSummaryEvent> events;
    private DateDayHour baseline;

    @Builder
    private SubjectEcgSummary(String subjectId, String subject, List<EcgSummaryEvent> events, String sex, DateDayHour baseline) {
        super(subjectId, subject, sex);
        this.events = events;
        this.baseline = baseline;
    }
}
