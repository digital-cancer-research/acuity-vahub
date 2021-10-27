package com.acuity.visualisations.rest.model.request.aes;

import com.acuity.visualisations.rawdatamodel.trellis.grouping.AeGroupByOptions;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AesTableRequest extends AesRequest {
    private AeGroupByOptions aeLevel;
}
