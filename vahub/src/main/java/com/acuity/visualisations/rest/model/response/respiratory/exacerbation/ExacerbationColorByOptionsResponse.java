package com.acuity.visualisations.rest.model.response.respiratory.exacerbation;

import com.acuity.visualisations.rawdatamodel.trellis.TrellisOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ExacerbationGroupByOptions;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExacerbationColorByOptionsResponse implements Serializable {
    private List<TrellisOptions<ExacerbationGroupByOptions>> trellisOptions;
}
