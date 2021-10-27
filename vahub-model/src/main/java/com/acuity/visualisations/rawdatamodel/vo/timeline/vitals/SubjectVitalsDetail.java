package com.acuity.visualisations.rawdatamodel.vo.timeline.vitals;

import com.acuity.visualisations.rawdatamodel.vo.timeline.SubjectSummary;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SubjectVitalsDetail extends SubjectSummary implements Serializable {
    private String sex;
    private List<VitalsTests> tests;
}
