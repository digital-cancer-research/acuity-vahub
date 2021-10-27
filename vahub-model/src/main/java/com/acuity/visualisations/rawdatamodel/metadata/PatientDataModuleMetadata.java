package com.acuity.visualisations.rawdatamodel.metadata;

import com.acuity.visualisations.rawdatamodel.vo.PatientDataRaw;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.PatientData;
import org.springframework.stereotype.Service;

@Service
public class PatientDataModuleMetadata extends AbstractModuleMetadata<PatientDataRaw, PatientData> {

    @Override
    protected String tab() {
        return "patientData";
    }
}
