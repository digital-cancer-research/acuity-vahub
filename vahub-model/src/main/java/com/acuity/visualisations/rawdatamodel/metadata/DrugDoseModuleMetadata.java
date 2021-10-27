package com.acuity.visualisations.rawdatamodel.metadata;

import com.acuity.visualisations.rawdatamodel.vo.DrugDoseRaw;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.DrugDose;
import org.springframework.stereotype.Service;

@Service
public class DrugDoseModuleMetadata extends AbstractModuleMetadata<DrugDoseRaw, DrugDose> {
    @Override
    protected String tab() {
        return "dose";
    }
}
