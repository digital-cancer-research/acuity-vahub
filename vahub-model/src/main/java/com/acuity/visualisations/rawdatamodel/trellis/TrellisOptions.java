package com.acuity.visualisations.rawdatamodel.trellis;

import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrellisOptions<T extends GroupByOption> implements Serializable {
    private T trellisedBy;
    private List<?> trellisOptions;
}
