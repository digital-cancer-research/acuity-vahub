package com.acuity.visualisations.rawdatamodel.dataproviders;

import com.acuity.visualisations.rawdatamodel.dataproviders.common.SubjectAwareDatasetsRegularDataProvider;
import com.acuity.visualisations.rawdatamodel.vo.CerebrovascularRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Cerebrovascular;
import org.springframework.stereotype.Component;

@Component
public class CerebrovascularDatasetsDataProvider extends SubjectAwareDatasetsRegularDataProvider<CerebrovascularRaw, Cerebrovascular> {


    @Override
    protected Cerebrovascular getWrapperInstance(CerebrovascularRaw event, Subject subject) {
        return new Cerebrovascular(event, subject);
    }

    @Override
    protected Class<CerebrovascularRaw> rawDataClass() {
        return CerebrovascularRaw.class;
    }
}
