package com.acuity.visualisations.rawdatamodel.dataproviders;

import com.acuity.visualisations.rawdatamodel.dataproviders.common.SubjectAwareDatasetsRegularDataProvider;
import com.acuity.visualisations.rawdatamodel.vo.DiseaseExtentRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.DiseaseExtent;
import org.springframework.stereotype.Component;

@Component
public class DiseaseExtentDatasetsDataProvider extends SubjectAwareDatasetsRegularDataProvider<DiseaseExtentRaw, DiseaseExtent> {
    @Override
    protected DiseaseExtent getWrapperInstance(DiseaseExtentRaw event, Subject subject) {
        return new DiseaseExtent(event, subject);
    }

    @Override
    protected Class<DiseaseExtentRaw> rawDataClass() {
        return DiseaseExtentRaw.class;
    }
}
