package com.acuity.visualisations.rawdatamodel.dataproviders;

import com.acuity.visualisations.rawdatamodel.dataproviders.common.SubjectAwareDatasetsRegularDataProvider;
import com.acuity.visualisations.rawdatamodel.vo.RadiotherapyRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Radiotherapy;
import org.springframework.stereotype.Component;

@Component
public class RadiotherapyDatasetsDataProvider extends SubjectAwareDatasetsRegularDataProvider<RadiotherapyRaw, Radiotherapy> {

    @Override
    protected Radiotherapy getWrapperInstance(RadiotherapyRaw event, Subject subject) {
        return new Radiotherapy(event, subject);
    }

    @Override
    protected Class<RadiotherapyRaw> rawDataClass() {
        return RadiotherapyRaw.class;
    }
}
