package com.acuity.visualisations.rawdatamodel.metadata;

import com.acuity.visualisations.common.study.metadata.MetadataItem;
import com.acuity.visualisations.rawdatamodel.service.ssv.ColorInitializer;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.CtDnaGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.CtDnaRaw;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.CtDna;
import com.acuity.va.security.acl.domain.Datasets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

import static com.acuity.visualisations.rawdatamodel.util.Constants.AVAILABLE_YAXIS_OPTIONS;
import static com.acuity.visualisations.rawdatamodel.util.Constants.HAS_TRACKED_MUTATIONS;
import static com.acuity.visualisations.rawdatamodel.util.Constants.TRACKED_MUTATIONS_STRING;
import static com.acuity.visualisations.rawdatamodel.util.Constants.YES;
import static com.google.common.collect.Lists.newArrayList;

@Service
public class CtDnaModuleMetadata extends AbstractModuleColoringMetadata<CtDnaRaw, CtDna> {
    @Autowired
    @Qualifier("ctDnaService")
    private ColorInitializer colorInitializer;

    @Override
    protected String tab() {
        return "ctdna";
    }

    @Override
    protected MetadataItem buildMetadataItem(MetadataItem metadataItem, Datasets datasets) {
        Collection<CtDna> data = getData(datasets);
        metadataItem = super.buildMetadataItem(metadataItem, datasets, data);
        // linechart Y axis
        metadataItem.add(AVAILABLE_YAXIS_OPTIONS,
                newArrayList(CtDnaGroupByOptions.VARIANT_ALLELE_FREQUENCY_PERCENT.toString(),
                        CtDnaGroupByOptions.VARIANT_ALLELE_FREQUENCY.toString()));
        final boolean hasTrackedMutations = data.stream().anyMatch(e -> YES.equals(e.getEvent().getTrackedMutation()));
        metadataItem.addProperty(HAS_TRACKED_MUTATIONS, hasTrackedMutations);
        metadataItem.addProperty(TRACKED_MUTATIONS_STRING, CtDna.ONLY_TRACKED_MUTATIONS);
        return metadataItem;
    }

    @Override
    protected MetadataItem buildErrorMetadataItem(MetadataItem metadataItem) {
        metadataItem = super.buildErrorMetadataItem(metadataItem);
        metadataItem.add(AVAILABLE_YAXIS_OPTIONS, new ArrayList<>());
        metadataItem.addProperty(HAS_TRACKED_MUTATIONS, false);
        metadataItem.addProperty(TRACKED_MUTATIONS_STRING, CtDna.ONLY_TRACKED_MUTATIONS);
        return metadataItem;
    }

    @Override
    ColorInitializer getColorInitializer() {
        return colorInitializer;
    }
}
