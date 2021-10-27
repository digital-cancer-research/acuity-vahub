package com.acuity.visualisations.rawdatamodel.vo.compatibility;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Builder
@Getter
public class OutputChordDiagramData implements Serializable {
    private List<OutputChordDiagramEntry> data;
    private Map<String, String> colorBook;
}
