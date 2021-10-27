package com.acuity.visualisations.rest.model.response.conmeds;

import com.acuity.visualisations.rawdatamodel.trellis.TrellisOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ConmedGroupByOptions;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConmedsTrellisingResponse implements Serializable {
    private List<TrellisOptions<ConmedGroupByOptions>> trellisOptions;
}
