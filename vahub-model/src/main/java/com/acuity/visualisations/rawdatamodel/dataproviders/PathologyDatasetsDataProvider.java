package com.acuity.visualisations.rawdatamodel.dataproviders;

import com.acuity.visualisations.rawdatamodel.dataproviders.common.SubjectAwareDatasetsRegularDataProvider;
import com.acuity.visualisations.rawdatamodel.vo.PathologyRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Pathology;
import org.springframework.stereotype.Component;

@Component
public class PathologyDatasetsDataProvider extends SubjectAwareDatasetsRegularDataProvider<PathologyRaw, Pathology> {
    @Override
    protected Pathology getWrapperInstance(PathologyRaw event, Subject subject) {
        return new Pathology(event, subject);
    }

    @Override
    protected Class<PathologyRaw> rawDataClass() {
        return PathologyRaw.class;
    }
}
