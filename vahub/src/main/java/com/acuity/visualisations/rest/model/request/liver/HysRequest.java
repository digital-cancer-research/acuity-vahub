package com.acuity.visualisations.rest.model.request.liver;

import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptionsFiltered;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.LiverGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Liver;
import com.esotericsoftware.kryo.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class HysRequest extends LiverRequest {
    /**
     * Example:
     * "settings": {
     * "filterByTrellisOptions": [
     * {
     * "MEASUREMENT": "AST",
     * "ARM": "Placebo"
     * },
     * {
     * "MEASUREMENT": "AST",
     * "ARM": "SuperDex 10 mg"
     * },
     * {
     * "MEASUREMENT": "AST",
     * "ARM": "SuperDex 20 mg"
     * },
     * {
     * "MEASUREMENT": "ALT",
     * "ARM": "Placebo"
     * }
     * ]
     * }
     */
    @NotNull
    private ChartGroupByOptionsFiltered<Liver, LiverGroupByOptions> settings;
}
