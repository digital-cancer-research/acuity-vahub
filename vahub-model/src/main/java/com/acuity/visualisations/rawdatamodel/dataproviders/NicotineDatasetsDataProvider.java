package com.acuity.visualisations.rawdatamodel.dataproviders;

import com.acuity.visualisations.rawdatamodel.dataproviders.common.SubjectAwareDatasetsRegularDataProvider;
import com.acuity.visualisations.rawdatamodel.vo.NicotineRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Nicotine;
import org.springframework.stereotype.Component;

@Component
public class NicotineDatasetsDataProvider extends
        SubjectAwareDatasetsRegularDataProvider<NicotineRaw, Nicotine> {
    @Override
    protected Nicotine getWrapperInstance(NicotineRaw event, Subject subject) {
        return new Nicotine(event, subject);
    }

    @Override
    protected Class<NicotineRaw> rawDataClass() {
        return NicotineRaw.class;
    }
}
