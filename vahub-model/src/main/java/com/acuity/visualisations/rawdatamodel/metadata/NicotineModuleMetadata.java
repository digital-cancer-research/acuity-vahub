package com.acuity.visualisations.rawdatamodel.metadata;

import com.acuity.visualisations.rawdatamodel.vo.NicotineRaw;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Nicotine;
import org.springframework.stereotype.Service;

@Service
public class NicotineModuleMetadata extends AbstractModuleMetadata<NicotineRaw, Nicotine> {
    @Override
    protected String tab() {
        return "nicotine";
    }
}
