package com.acuity.visualisations.rawdatamodel.vo.compatibility;

import com.acuity.visualisations.rawdatamodel.trellis.TrellisOption;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

public interface TrellisedChart<T, G extends Enum<G> & GroupByOption<T>> {
    List<TrellisOption<T, G>> getTrellisedBy();

    @JsonIgnore
    default String getTrellisByString() {
        return StringUtils.join(getTrellisedBy().stream().map(to -> to.getTrellisedBy().name() + to.getTrellisOption().toString()).collect(
                Collectors.toList()), '#');
    }

}
