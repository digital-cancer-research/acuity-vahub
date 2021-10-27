package com.acuity.visualisations.rawdatamodel.dataproviders;

import com.acuity.visualisations.rawdatamodel.dataproviders.common.SubjectAwareDatasetsRegularDataProvider;
import com.acuity.visualisations.rawdatamodel.vo.CvotEndpointRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.CvotEndpoint;
import org.springframework.stereotype.Component;

@Component
public class CvotEndpointDatasetsDataProvider extends SubjectAwareDatasetsRegularDataProvider<CvotEndpointRaw, CvotEndpoint> {

    @Override
    protected CvotEndpoint getWrapperInstance(CvotEndpointRaw event, Subject subject) {
        return new CvotEndpoint(event, subject);
    }

    @Override
    protected Class<CvotEndpointRaw> rawDataClass() {
        return CvotEndpointRaw.class;
    }
}
