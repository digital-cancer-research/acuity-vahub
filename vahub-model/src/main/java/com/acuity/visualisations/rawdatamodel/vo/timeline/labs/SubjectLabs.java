package com.acuity.visualisations.rawdatamodel.vo.timeline.labs;

import com.acuity.visualisations.rawdatamodel.vo.timeline.SubjectSummary;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public abstract class SubjectLabs extends SubjectSummary implements Serializable {

    private String sex;

}
