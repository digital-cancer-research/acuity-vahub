package com.acuity.visualisations.rawdatamodel.metadata;

import com.acuity.visualisations.rawdatamodel.dataproviders.PkResultWithResponseDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.common.DatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.filters.PkResultFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.service.event.PkResultWithResponseService;
import com.acuity.visualisations.rawdatamodel.vo.PkResultRaw;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.PkResult;
import com.acuity.va.security.acl.domain.Datasets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PkResultWithResponseModuleMetadata extends PkResultAbstractModuleMetadata {

    @Autowired
    private PkResultWithResponseDatasetsDataProvider pkResultWithResponseDatasetsDataProvider;
    @Autowired
    private PkResultWithResponseService pkResultWithResponseService;

    @Override
    protected String tab() {
        return "pkResultWithResponse";
    }

    @Override
    protected DatasetsDataProvider<PkResultRaw, PkResult> getEventDataProvider() {
        return pkResultWithResponseDatasetsDataProvider;
    }

    @Override
    protected boolean hasXAxis(Datasets datasets) {
        return !pkResultWithResponseService
                .getAvailableBoxPlotXAxis(datasets, PkResultFilters.empty(), PopulationFilters.empty())
                .getOptions()
                .isEmpty();
    }
}
