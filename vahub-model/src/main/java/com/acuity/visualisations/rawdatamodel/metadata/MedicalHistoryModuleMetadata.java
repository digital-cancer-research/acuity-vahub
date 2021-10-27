package com.acuity.visualisations.rawdatamodel.metadata;

import com.acuity.visualisations.rawdatamodel.vo.MedicalHistoryRaw;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.MedicalHistory;
import org.springframework.stereotype.Service;

@Service
public class MedicalHistoryModuleMetadata extends AbstractModuleMetadata<MedicalHistoryRaw, MedicalHistory> {
    @Override
    protected String tab() {
        return "medicalHistory";
    }
}
