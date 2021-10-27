package com.acuity.visualisations.rawdatamodel.dataproviders.common;

import com.acuity.visualisations.common.lookup.BeanLookupService;
import com.acuity.visualisations.common.lookup.CacheableDataProvider;
import com.acuity.visualisations.rawdatamodel.vo.PrecalculationSupport;
import com.acuity.va.security.acl.domain.Dataset;
import com.acuity.va.security.acl.domain.Datasets;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public abstract class DatasetsDataProvider<R, T> {
    @Autowired
    protected BeanLookupService lkup;

    @Autowired
    protected CacheableDataProvider dataProvider;

    @SneakyThrows
    public Collection<T> loadData(Datasets datasets) {
         List<R> events = datasets.getDatasets().stream()
                .map(this::getData)
                .flatMap(Collection::stream)
                .map(e -> e instanceof PrecalculationSupport ? ((PrecalculationSupport<R>) e).runPrecalculations() : e)
                .collect(Collectors.toList());

        return wrap(datasets, events);
    }

    protected abstract Collection<T> wrap(Datasets datasets, Collection<R> events);

    protected abstract Collection<R> getData(Dataset dataset);

    protected abstract Class<R> rawDataClass();
}
