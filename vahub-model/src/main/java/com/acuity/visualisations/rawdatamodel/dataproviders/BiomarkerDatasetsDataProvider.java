package com.acuity.visualisations.rawdatamodel.dataproviders;

import com.acuity.visualisations.rawdatamodel.dataproviders.common.SubjectAwareDatasetsRegularDataProvider;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.biomarker.BiomarkerRaw;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Biomarker;
import org.springframework.stereotype.Component;

@Component
public class BiomarkerDatasetsDataProvider extends SubjectAwareDatasetsRegularDataProvider<BiomarkerRaw, Biomarker> {

    @Override
    protected Biomarker getWrapperInstance(BiomarkerRaw event, Subject subject) {
        return new Biomarker(event, subject);
    }

    @Override
    protected Class<BiomarkerRaw> rawDataClass() {
        return BiomarkerRaw.class;
    }
}
