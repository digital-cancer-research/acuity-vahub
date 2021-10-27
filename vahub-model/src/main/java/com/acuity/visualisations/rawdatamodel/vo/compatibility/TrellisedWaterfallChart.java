package com.acuity.visualisations.rawdatamodel.vo.compatibility;

import com.acuity.visualisations.rawdatamodel.trellis.TrellisOption;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
public class TrellisedWaterfallChart<T, G extends Enum<G> & GroupByOption<T>> implements TrellisedChart<T, G>, Serializable {
    private List<TrellisOption<T, G>> trellisedBy;
    private OutputWaterfallData data;
}
