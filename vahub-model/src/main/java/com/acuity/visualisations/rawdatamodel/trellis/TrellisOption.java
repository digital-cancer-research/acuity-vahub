package com.acuity.visualisations.rawdatamodel.trellis;

import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@EqualsAndHashCode
@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TrellisOption<T, G extends Enum<G> & GroupByOption<T>> implements Serializable {
    private G trellisedBy;
    private Object trellisOption;

    @Override
    public String toString() {
        return trellisOption == null ? "(EMPTY)" : trellisOption.toString();
    }

    public static <T, G extends Enum<G> & GroupByOption<T>> TrellisOption<T, G> of(G trellisedBy, Object trellisOption) {
        return new TrellisOption<T, G>(trellisedBy, trellisOption);
    }
}
