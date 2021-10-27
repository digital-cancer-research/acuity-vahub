package com.acuity.visualisations.rawdatamodel.vo.timeline.conmeds;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class ConmedSummary implements Serializable {
    protected String conmed;
    protected List<Double> doses;
    protected List<String> indications;
    protected List<String> frequencies;
}
