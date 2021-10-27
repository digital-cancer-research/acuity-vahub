package com.acuity.visualisations.rawdatamodel.metadata;

import com.acuity.visualisations.rawdatamodel.dataproviders.PkResultDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.common.DatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.filters.PkResultFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.service.event.PkResultService;
import com.acuity.visualisations.rawdatamodel.vo.PkResultRaw;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.PkResult;
import com.acuity.va.security.acl.domain.Datasets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PkResultModuleMetadata extends PkResultAbstractModuleMetadata {

    @Autowired
    private PkResultDatasetsDataProvider pkResultDatasetsDataProvider;
    @Autowired
    private PkResultService pkResultService;

    @Override
    protected String tab() {
        return "pkResult";
    }

    @Override
    protected DatasetsDataProvider<PkResultRaw, PkResult> getEventDataProvider() {
        return pkResultDatasetsDataProvider;
    }

    @Override
    protected boolean hasXAxis(Datasets datasets) {
        return !pkResultService
                .getAvailableBoxPlotXAxis(datasets, PkResultFilters.empty(), PopulationFilters.empty())
                .getOptions()
                .isEmpty();
    }
}
