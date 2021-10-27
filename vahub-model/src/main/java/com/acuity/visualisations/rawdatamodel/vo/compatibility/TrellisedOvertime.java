package com.acuity.visualisations.rawdatamodel.vo.compatibility;

import com.acuity.visualisations.rawdatamodel.trellis.TrellisOption;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.GroupByKey;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrellisedOvertime<T, G extends Enum<G> & GroupByOption<T>> implements TrellisedChart<T, G>, Serializable {
    private List<TrellisOption<T, G>> trellisedBy;
    private OutputOvertimeData data;

    public static <T, G extends Enum<G> & GroupByOption<T>> TrellisedOvertime<T, G> of(GroupByKey<T, G> key, OutputOvertimeData data) {
        return new TrellisedOvertime<>(key.getTrellisByValues().entrySet().stream()
                .map(k -> TrellisOption.of(k.getKey(), k.getValue())).collect(Collectors.toList()), data);
    }
}
