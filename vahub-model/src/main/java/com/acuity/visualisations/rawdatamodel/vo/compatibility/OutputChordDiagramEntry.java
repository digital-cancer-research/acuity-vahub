package com.acuity.visualisations.rawdatamodel.vo.compatibility;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.util.Map;

@Builder
@Getter
public class OutputChordDiagramEntry implements Serializable {
    private String start;
    private String end;
    private int width;
    private Map<String, Integer> contributors;
}
