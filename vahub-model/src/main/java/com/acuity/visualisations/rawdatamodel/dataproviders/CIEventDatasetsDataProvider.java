package com.acuity.visualisations.rawdatamodel.dataproviders;

import com.acuity.visualisations.rawdatamodel.dataproviders.common.SubjectAwareDatasetsRegularDataProvider;
import com.acuity.visualisations.rawdatamodel.vo.CIEventRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.CIEvent;
import org.springframework.stereotype.Component;

@Component
public class CIEventDatasetsDataProvider extends SubjectAwareDatasetsRegularDataProvider<CIEventRaw, CIEvent> {

    @Override
    protected CIEvent getWrapperInstance(CIEventRaw event, Subject subject) {
        return new CIEvent(event, subject);
    }

    @Override
    protected Class<CIEventRaw> rawDataClass() {
        return CIEventRaw.class;
    }
}
