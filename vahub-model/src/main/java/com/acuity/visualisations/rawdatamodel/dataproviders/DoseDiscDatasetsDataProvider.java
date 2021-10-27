package com.acuity.visualisations.rawdatamodel.dataproviders;

import com.acuity.visualisations.rawdatamodel.dataproviders.common.SubjectAwareDatasetsRegularDataProvider;
import com.acuity.visualisations.rawdatamodel.vo.DoseDiscRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.DoseDisc;
import org.springframework.stereotype.Component;

@Component
public class DoseDiscDatasetsDataProvider extends SubjectAwareDatasetsRegularDataProvider<DoseDiscRaw, DoseDisc> {

    @Override
    protected DoseDisc getWrapperInstance(DoseDiscRaw event, Subject subject) {
        return new DoseDisc(event, subject);
    }

    @Override
    protected Class<DoseDiscRaw> rawDataClass() {
        return DoseDiscRaw.class;
    }
}
