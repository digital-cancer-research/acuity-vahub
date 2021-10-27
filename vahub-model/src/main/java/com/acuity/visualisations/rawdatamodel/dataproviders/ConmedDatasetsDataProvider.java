package com.acuity.visualisations.rawdatamodel.dataproviders;

import com.acuity.visualisations.rawdatamodel.dao.ConmedRepository;
import com.acuity.visualisations.rawdatamodel.dataproviders.common.SubjectAwareDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.vo.ConmedRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Conmed;
import com.acuity.va.security.acl.domain.Dataset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;

@Component
public class ConmedDatasetsDataProvider extends SubjectAwareDatasetsDataProvider<ConmedRaw, Conmed> {

    @Autowired
    private ConmedRepository conmedRepository;

    @Override
    protected Conmed getWrapperInstance(ConmedRaw event, Subject subject) {
        return new Conmed(event, subject);
    }

    @Override
    protected Class<ConmedRaw> rawDataClass() {
        return ConmedRaw.class;
    }

    @Override
    protected Collection<ConmedRaw> getData(Dataset ds) {
        return new ArrayList<>(conmedRepository.getRawData(ds.getId()));
    }
}
