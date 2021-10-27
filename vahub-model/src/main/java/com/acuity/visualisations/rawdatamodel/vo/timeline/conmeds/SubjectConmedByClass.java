package com.acuity.visualisations.rawdatamodel.vo.timeline.conmeds;

import com.acuity.visualisations.rawdatamodel.vo.timeline.SubjectSummary;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SubjectConmedByClass extends SubjectSummary implements Serializable {
    private List<ConmedEventsByClass> conmedClasses;
}
