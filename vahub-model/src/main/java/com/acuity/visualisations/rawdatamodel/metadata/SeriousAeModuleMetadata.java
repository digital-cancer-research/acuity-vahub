package com.acuity.visualisations.rawdatamodel.metadata;

import com.acuity.visualisations.rawdatamodel.vo.SeriousAeRaw;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.SeriousAe;
import org.springframework.stereotype.Service;

@Service
public class SeriousAeModuleMetadata extends AbstractModuleMetadata<SeriousAeRaw, SeriousAe> {
    @Override
    protected String tab() {
        return "seriousAe";
    }
}
