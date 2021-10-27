package com.acuity.visualisations.rawdatamodel.trellis.grouping;

import com.googlecode.cqengine.attribute.Attribute;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class SelectionAttributeDto {
    private Attribute xAxisOption;
    private Attribute yAxisOption;
    private Attribute measurement;
    private Attribute arm;
}
