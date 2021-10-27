package com.acuity.visualisations.rawdatamodel.dataproviders;

import com.acuity.visualisations.rawdatamodel.dataproviders.common.SubjectAwareDatasetsRegularDataProvider;
import com.acuity.visualisations.rawdatamodel.vo.ChemotherapyRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Chemotherapy;
import org.springframework.stereotype.Component;

@Component
public class ChemotherapyDatasetsDataProvider extends SubjectAwareDatasetsRegularDataProvider<ChemotherapyRaw, Chemotherapy> {

    @Override
    protected Chemotherapy getWrapperInstance(ChemotherapyRaw event, Subject subject) {
        return new Chemotherapy(event, subject);
    }

    @Override
    protected Class<ChemotherapyRaw> rawDataClass() {
        return ChemotherapyRaw.class;
    }
}
