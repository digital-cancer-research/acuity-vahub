package com.acuity.visualisations.rawdatamodel.vo.timeline.labs;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SubjectLabsDetail extends SubjectLabs implements Serializable {
    private List<Labcodes> labcodes;
}
