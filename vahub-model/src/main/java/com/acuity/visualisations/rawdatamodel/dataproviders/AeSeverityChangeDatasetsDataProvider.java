package com.acuity.visualisations.rawdatamodel.dataproviders;

import com.acuity.visualisations.rawdatamodel.util.AeRawTransformer;
import com.acuity.visualisations.rawdatamodel.vo.AeRaw;
import com.acuity.va.security.acl.domain.Dataset;
import java.util.Collection;
import org.springframework.stereotype.Component;

@Component
public class AeSeverityChangeDatasetsDataProvider extends AeIncidenceDatasetsDataProvider {

    @Override
    protected Collection<AeRaw> getData(Dataset dataset) {

        Collection<AeRaw> aeIncidence = super.getData(dataset);
        
        return AeRawTransformer.transformToSeverityChange(aeIncidence);
    }
}
