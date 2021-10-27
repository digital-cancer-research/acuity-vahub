package com.acuity.visualisations.rawdatamodel.vo.plots;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class ChordDiagramSelectionDetail implements Serializable {

    @Builder.Default
    private Set<ChordContributor> eventIds = new HashSet<>();
    @Builder.Default
    private Set<String> subjectIds = new HashSet<>();
    private int eventCount;
    private int totalEvents;
    private int totalSubjects;
}
