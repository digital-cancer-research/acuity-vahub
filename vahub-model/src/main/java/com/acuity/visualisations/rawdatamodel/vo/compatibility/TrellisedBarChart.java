package com.acuity.visualisations.rawdatamodel.vo.compatibility;

import com.acuity.visualisations.rawdatamodel.trellis.TrellisOption;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrellisedBarChart<T, G extends Enum<G> & GroupByOption<T>> implements Serializable, TrellisedChart<T, G> {
    private List<TrellisOption<T, G>> trellisedBy;
    private List<? extends OutputBarChartData> data;

}
