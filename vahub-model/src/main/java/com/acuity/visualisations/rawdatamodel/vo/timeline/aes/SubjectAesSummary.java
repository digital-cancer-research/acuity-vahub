package com.acuity.visualisations.rawdatamodel.vo.timeline.aes;

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
public final class SubjectAesSummary extends SubjectSummary implements Serializable {
    private List<AeMaxCtcEvent> events;

    @Builder
    private SubjectAesSummary(String subjectId, String subject, List<AeMaxCtcEvent> events) {
        super(subjectId, subject);
        this.events = events;
    }
}
