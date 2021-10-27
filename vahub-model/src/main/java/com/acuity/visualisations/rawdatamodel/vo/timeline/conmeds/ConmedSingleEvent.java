package com.acuity.visualisations.rawdatamodel.vo.timeline.conmeds;

import com.acuity.visualisations.rawdatamodel.vo.timeline.EventInterval;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ConmedSingleEvent extends EventInterval implements Serializable {
    protected String conmed;
    protected String indication;
    protected String frequency;
    protected Double dose;
}
