package com.acuity.visualisations.rawdatamodel.metadata;

import com.acuity.visualisations.rawdatamodel.vo.AlcoholRaw;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Alcohol;
import org.springframework.stereotype.Service;

@Service
public class AlcoholModuleMetadata extends AbstractModuleMetadata<AlcoholRaw, Alcohol> {
    @Override
    protected String tab() {
        return "alcohol";
    }
}
