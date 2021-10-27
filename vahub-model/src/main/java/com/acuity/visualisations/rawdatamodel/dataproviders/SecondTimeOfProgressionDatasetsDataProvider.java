package com.acuity.visualisations.rawdatamodel.dataproviders;

import com.acuity.visualisations.rawdatamodel.dataproviders.common.SubjectAwareDatasetsRegularDataProvider;
import com.acuity.visualisations.rawdatamodel.vo.SecondTimeOfProgressionRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.SecondTimeOfProgression;
import org.springframework.stereotype.Component;

@Component
public class SecondTimeOfProgressionDatasetsDataProvider extends SubjectAwareDatasetsRegularDataProvider<SecondTimeOfProgressionRaw, SecondTimeOfProgression> {
    @Override
    protected SecondTimeOfProgression getWrapperInstance(SecondTimeOfProgressionRaw event, Subject subject) {
        return new SecondTimeOfProgression(event, subject);
    }

    @Override
    protected Class<SecondTimeOfProgressionRaw> rawDataClass() {
        return SecondTimeOfProgressionRaw.class;
    }
}
