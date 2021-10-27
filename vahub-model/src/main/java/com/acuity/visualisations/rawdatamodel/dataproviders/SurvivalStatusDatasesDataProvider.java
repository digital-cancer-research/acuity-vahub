package com.acuity.visualisations.rawdatamodel.dataproviders;

import com.acuity.visualisations.rawdatamodel.dataproviders.common.SubjectAwareDatasetsRegularDataProvider;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.SurvivalStatusRaw;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.SurvivalStatus;
import org.springframework.stereotype.Component;

@Component
public class SurvivalStatusDatasesDataProvider extends SubjectAwareDatasetsRegularDataProvider<SurvivalStatusRaw, SurvivalStatus> {
    @Override
    protected SurvivalStatus getWrapperInstance(SurvivalStatusRaw event, Subject subject) {
        return new SurvivalStatus(event, subject);
    }

    @Override
    protected Class<SurvivalStatusRaw> rawDataClass() {
        return SurvivalStatusRaw.class;
    }
}
