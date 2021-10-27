package com.acuity.visualisations.rawdatamodel.service.plots.vo;

import com.acuity.visualisations.rawdatamodel.vo.HasStringId;
import com.acuity.visualisations.rawdatamodel.vo.HasSubject;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BarChartCalculationObject<T extends HasStringId & HasSubject> {
    @JsonIgnore
    private Set<String> subjects;
    @JsonIgnore
    private Collection<T> eventSet;
    @JsonIgnore
    private Integer events;
    private Double value;
    private Integer totalSubject;
}
