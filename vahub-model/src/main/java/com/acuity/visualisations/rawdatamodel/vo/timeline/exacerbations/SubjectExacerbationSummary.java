package com.acuity.visualisations.rawdatamodel.vo.timeline.exacerbations;

import com.acuity.visualisations.rawdatamodel.vo.timeline.SubjectSummary;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class SubjectExacerbationSummary extends SubjectSummary implements Serializable {
    private List<ExacerbationSummaryEvent> events;

    @Builder
    private SubjectExacerbationSummary(String subjectId, String subject, List<ExacerbationSummaryEvent> events) {
        super(subjectId, subject);
        this.events = events;
    }
}
