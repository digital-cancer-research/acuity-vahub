package com.acuity.visualisations.rawdatamodel.dataproviders.common;

import com.acuity.visualisations.rawdatamodel.dao.api.RawDataRepository;
import com.acuity.visualisations.rawdatamodel.vo.HasStringId;
import com.acuity.visualisations.rawdatamodel.vo.HasSubjectId;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.SubjectAwareWrapper;
import com.acuity.va.security.acl.domain.Dataset;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;

public abstract class SubjectAwareDatasetsRegularDataProvider<
        T extends HasSubjectId & HasStringId,
        W extends SubjectAwareWrapper<T>>
        extends SubjectAwareDatasetsDataProvider<T, W> {

    @Autowired
    protected RawDataRepository<T> rawDataRepository;

    @Override
    protected Collection<T> getData(Dataset dataset) {
        return dataProvider.getData(
                rawDataClass(),
                dataset,
                ds -> rawDataRepository.getRawData(ds.getId())
        );
    }
}
