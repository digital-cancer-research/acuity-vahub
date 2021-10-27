package com.acuity.visualisations.rawdatamodel.dataproviders;

import com.acuity.visualisations.rawdatamodel.dataproviders.common.SubjectAwareDatasetsRegularDataProvider;
import com.acuity.visualisations.rawdatamodel.vo.PkResultRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.PkResult;
import org.springframework.stereotype.Component;

@Component
public class PkResultDatasetsDataProvider extends SubjectAwareDatasetsRegularDataProvider<PkResultRaw, PkResult> {

    @Override
    protected PkResult getWrapperInstance(PkResultRaw event, Subject subject) {
        return new PkResult(event, subject);
    }

    @Override
    protected Class<PkResultRaw> rawDataClass() {
        return PkResultRaw.class;
    }
}
