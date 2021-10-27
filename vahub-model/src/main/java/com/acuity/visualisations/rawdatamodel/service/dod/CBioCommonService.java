package com.acuity.visualisations.rawdatamodel.service.dod;

import com.acuity.visualisations.rawdatamodel.util.Column;
import org.springframework.stereotype.Service;

@Service
public class CBioCommonService extends CommonTableService {

    @Override
    protected Column.Type getType() {
        return Column.Type.CBIO;
    }
}
