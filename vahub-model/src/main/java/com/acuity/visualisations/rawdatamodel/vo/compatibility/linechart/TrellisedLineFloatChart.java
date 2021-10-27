package com.acuity.visualisations.rawdatamodel.vo.compatibility.linechart;

import com.acuity.visualisations.rawdatamodel.trellis.TrellisOption;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedChart;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
public class TrellisedLineFloatChart<T, G extends Enum<G> & GroupByOption<T>, L extends OutputLineChartData> implements TrellisedChart<T, G>, Serializable {
    private List<TrellisOption<T, G>> trellisedBy;
    private List<L> data;
}
