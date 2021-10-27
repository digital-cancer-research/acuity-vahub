package com.acuity.visualisations.rawdatamodel.metadata;

import com.acuity.visualisations.rawdatamodel.vo.DoseDiscRaw;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.DoseDisc;
import org.springframework.stereotype.Service;

@Service
public class DoseDiscModuleMetadata extends AbstractModuleMetadata<DoseDiscRaw, DoseDisc> {
    @Override
    protected String tab() {
        return "doseDisc";
    }
}
