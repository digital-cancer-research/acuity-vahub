package com.acuity.visualisations.rawdatamodel.dataproviders;

import com.acuity.visualisations.rawdatamodel.dataproviders.common.SubjectAwareDatasetsRegularDataProvider;
import com.acuity.visualisations.rawdatamodel.vo.PatientDataRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.PatientData;
import org.springframework.stereotype.Component;

@Component
public class PatientDataDatasetsDataProvider extends SubjectAwareDatasetsRegularDataProvider<PatientDataRaw, PatientData> {

    @Override
    protected PatientData getWrapperInstance(PatientDataRaw event, Subject subject) {
        return new PatientData(event, subject);
    }

    @Override
    protected Class<PatientDataRaw> rawDataClass() {
        return PatientDataRaw.class;
    }
}
