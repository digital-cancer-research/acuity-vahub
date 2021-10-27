package com.acuity.visualisations.rest.model.response.renal;

import com.acuity.visualisations.rawdatamodel.trellis.TrellisOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.RenalGroupByOptions;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RenalColorByOptionsResponse implements Serializable {
    private List<TrellisOptions<RenalGroupByOptions>> trellisOptions;
}
