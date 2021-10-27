package com.acuity.visualisations.rawdatamodel.metadata;

import com.acuity.visualisations.rawdatamodel.vo.SurgicalHistoryRaw;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.SurgicalHistory;
import org.springframework.stereotype.Service;

@Service
public class SurgicalHistoryModuleMetadata extends AbstractModuleMetadata<SurgicalHistoryRaw, SurgicalHistory> {
    @Override
    protected String tab() {
        return "surgicalHistory";
    }
}
