package com.acuity.visualisations.rawdatamodel.dataproviders;

import com.acuity.visualisations.rawdatamodel.dataproviders.common.SubjectAwareDatasetsRegularDataProvider;
import com.acuity.visualisations.rawdatamodel.vo.MedicalHistoryRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.MedicalHistory;
import org.springframework.stereotype.Component;

@Component
public class MedicalHistoryDatasetsDataProvider extends SubjectAwareDatasetsRegularDataProvider<MedicalHistoryRaw, MedicalHistory> {

    @Override
    protected MedicalHistory getWrapperInstance(MedicalHistoryRaw event, Subject subject) {
        return new MedicalHistory(event, subject);
    }

    @Override
    protected Class<MedicalHistoryRaw> rawDataClass() {
        return MedicalHistoryRaw.class;
    }
}
