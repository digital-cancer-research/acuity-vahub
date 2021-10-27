package com.acuity.visualisations.rawdatamodel.service.dod;

import org.springframework.stereotype.Service;

import static com.acuity.visualisations.rawdatamodel.util.Column.Type;

@Service
public class AmlCommonService extends CommonTableService {

    @Override
    protected Type getType() {
        return Type.AML;
    }
}
