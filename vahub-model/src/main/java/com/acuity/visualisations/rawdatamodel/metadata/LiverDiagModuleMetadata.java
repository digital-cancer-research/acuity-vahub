package com.acuity.visualisations.rawdatamodel.metadata;

import com.acuity.visualisations.rawdatamodel.vo.LiverDiagRaw;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.LiverDiag;
import org.springframework.stereotype.Service;

@Service
public class LiverDiagModuleMetadata extends AbstractModuleMetadata<LiverDiagRaw, LiverDiag> {
    @Override
    protected String tab() {
        return "liverDiag";
    }
}
