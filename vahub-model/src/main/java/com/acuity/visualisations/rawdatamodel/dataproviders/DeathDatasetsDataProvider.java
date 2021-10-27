package com.acuity.visualisations.rawdatamodel.dataproviders;

import com.acuity.visualisations.rawdatamodel.dataproviders.common.SubjectAwareDatasetsRegularDataProvider;
import com.acuity.visualisations.rawdatamodel.vo.DeathRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Death;
import org.springframework.stereotype.Component;

@Component
public class DeathDatasetsDataProvider extends SubjectAwareDatasetsRegularDataProvider<DeathRaw, Death> {

    @Override
    protected Death getWrapperInstance(DeathRaw event, Subject subject) {
        return new Death(event, subject);
    }

    @Override
    protected Class<DeathRaw> rawDataClass() {
        return DeathRaw.class;
    }
}
