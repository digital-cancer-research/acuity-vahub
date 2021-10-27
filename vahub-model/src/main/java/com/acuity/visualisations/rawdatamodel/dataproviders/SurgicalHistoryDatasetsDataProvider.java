package com.acuity.visualisations.rawdatamodel.dataproviders;

import com.acuity.visualisations.rawdatamodel.dataproviders.common.SubjectAwareDatasetsRegularDataProvider;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.SurgicalHistoryRaw;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.SurgicalHistory;
import org.springframework.stereotype.Component;

@Component
public class SurgicalHistoryDatasetsDataProvider extends SubjectAwareDatasetsRegularDataProvider<SurgicalHistoryRaw, SurgicalHistory> {

    @Override
    protected SurgicalHistory getWrapperInstance(SurgicalHistoryRaw event, Subject subject) {
        return new SurgicalHistory(event, subject);
    }

    @Override
    protected Class<SurgicalHistoryRaw> rawDataClass() {
        return SurgicalHistoryRaw.class;
    }
}
