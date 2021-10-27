package com.acuity.visualisations.rawdatamodel.metadata;

import com.acuity.visualisations.rawdatamodel.vo.DeathRaw;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Death;
import org.springframework.stereotype.Service;

@Service
public class DeathModuleMetadata extends AbstractModuleMetadata<DeathRaw, Death> {
    @Override
    protected String tab() {
        return "death";
    }
}
