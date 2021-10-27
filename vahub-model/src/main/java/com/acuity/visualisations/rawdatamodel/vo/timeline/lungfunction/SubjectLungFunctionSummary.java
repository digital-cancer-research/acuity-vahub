package com.acuity.visualisations.rawdatamodel.vo.timeline.lungfunction;

import com.acuity.visualisations.rawdatamodel.vo.timeline.day.hour.DateDayHour;
import com.acuity.visualisations.rawdatamodel.vo.timeline.SubjectSummary;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * Status summary of lung function (visits) for a subject, which consists of a list of LungFunctionSummaryEvent.
 */
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class SubjectLungFunctionSummary extends SubjectSummary implements Serializable {
    private String sex;
    private DateDayHour baseline;

    private List<LungFunctionSummaryEvent> events;

    @Builder
    private SubjectLungFunctionSummary(String subjectId, String subject,
                                       List<LungFunctionSummaryEvent> events,
                                       String sex,
                                       DateDayHour baseline) {
        super(subjectId, subject);
        this.events = events;
        this.sex = sex;
        this.baseline = baseline;
    }
}
