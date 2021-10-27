package com.acuity.visualisations.rawdatamodel.metadata;

import com.acuity.visualisations.rawdatamodel.vo.LiverRiskRaw;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.LiverRisk;
import org.springframework.stereotype.Service;

@Service
public class LiverRiskModuleMetadata extends AbstractModuleMetadata<LiverRiskRaw, LiverRisk> {
    @Override
    protected String tab() {
        return "liverRisk";
    }
}
