package com.acuity.visualisations.rawdatamodel.metadata;

import com.acuity.visualisations.common.study.metadata.MetadataItem;
import com.acuity.visualisations.common.study.metadata.ModuleMetadata;
import com.acuity.visualisations.rawdatamodel.dataproviders.common.DatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.service.dod.DoDCommonService;
import com.acuity.visualisations.rawdatamodel.util.Column.DatasetType;
import com.acuity.va.security.acl.domain.Datasets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Slf4j
public abstract class AbstractModuleMetadata<R, T> implements ModuleMetadata {

    @Autowired
    protected List<DatasetsDataProvider<R, T>> datasetsDataProvider;
    @Autowired
    protected DoDCommonService doDCommonService;

    protected DatasetsDataProvider<R, T> getEventDataProvider() {
        //  See BaseEventService.getEventDataProvider
        return datasetsDataProvider.get(0); // everyone will have 1 but aes, and ae overrides this method
    }

    protected MetadataItem createMetadataItem(String tab) {
        return new MetadataItem(tab);
    }

    @Override
    public MetadataItem getMetadataItem(Datasets datasets) {
        MetadataItem metadataItem = createMetadataItem(tab());
        try {
            buildAllMetadataItems(metadataItem, datasets);
        } catch (Exception ex) {
            log.error("Unable to get info for " + tab(), ex);
        }
        return metadataItem;
    }

    @Override
    public MetadataItem getNonMergeableMetadataItem(Datasets datasets) {
        return getMetadataItem(datasets);
    }

    protected MetadataItem buildAllMetadataItems(MetadataItem metadataItem, Datasets datasets) {
        try {
            metadataItem = buildMetadataItem(metadataItem, datasets);
        } catch (Exception ex) {
            metadataItem = buildErrorMetadataItem(metadataItem);
            log.error("Unable to build metadata for " + tab(), ex);
        }
        return metadataItem;
    }

    protected MetadataItem buildMetadataItem(MetadataItem metadataItem, Datasets datasets) {
        Collection<T> data = getData(datasets);
        return buildMetadataItem(metadataItem, datasets, data);
    }

    protected MetadataItem buildMetadataItem(MetadataItem metadataItem, Datasets datasets, Collection<T> data) {
        enrichWithDoDMetadata(metadataItem, datasets, data);
        enrichWithDataMetadata(metadataItem, data.size());
        return metadataItem;
    }

    protected void enrichWithDoDMetadata(MetadataItem metadataItem, Datasets datasets, Collection<T> data) {
        final Map<String, String> doDColumns = doDCommonService.getDoDColumns(DatasetType.fromDatasets(datasets), data);
        metadataItem.add("detailsOnDemandColumns", doDColumns.keySet());
        metadataItem.add("detailsOnDemandTitledColumns", doDColumns);
    }

    protected void enrichWithDataMetadata(MetadataItem metadataItem, int eventsCount) {
        metadataItem.addProperty("count", eventsCount);
        metadataItem.addProperty("hasData", eventsCount > 0);
    }


    protected MetadataItem buildErrorMetadataItem(MetadataItem metadataItem) {
        metadataItem.addProperty("count", "N/A");
        metadataItem.addProperty("hasData", false);
        metadataItem.add("detailsOnDemandColumns", new ArrayList<>());
        metadataItem.add("detailsOnDemandTitledColumns", new ArrayList<>());
        return metadataItem;
    }

    protected Collection<T> getData(Datasets datasets) {
        return getEventDataProvider().loadData(datasets);
    }

    protected abstract String tab();
}
