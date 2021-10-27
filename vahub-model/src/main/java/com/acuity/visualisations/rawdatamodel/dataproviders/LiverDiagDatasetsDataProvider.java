package com.acuity.visualisations.rawdatamodel.dataproviders;

import com.acuity.visualisations.rawdatamodel.dataproviders.common.SubjectAwareDatasetsRegularDataProvider;
import com.acuity.visualisations.rawdatamodel.vo.LiverDiagRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.LiverDiag;
import org.springframework.stereotype.Component;

@Component
public class LiverDiagDatasetsDataProvider
        extends SubjectAwareDatasetsRegularDataProvider<LiverDiagRaw, LiverDiag> {

    @Override
    protected LiverDiag getWrapperInstance(LiverDiagRaw event, Subject subject) {
        return new LiverDiag(event, subject);
    }

    @Override
    protected Class<LiverDiagRaw> rawDataClass() {
        return LiverDiagRaw.class;
    }
}
