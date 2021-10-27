package com.acuity.visualisations.rawdatamodel.metadata;

import com.acuity.visualisations.common.study.metadata.MetadataItem;
import com.acuity.visualisations.rawdatamodel.axes.BinCountType;
import com.acuity.visualisations.rawdatamodel.vo.CerebrovascularRaw;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Cerebrovascular;
import com.acuity.va.security.acl.domain.Datasets;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static com.acuity.visualisations.rawdatamodel.axes.CountType.COUNT_OF_EVENTS;
import static com.acuity.visualisations.rawdatamodel.axes.CountType.COUNT_OF_SUBJECTS;
import static com.acuity.visualisations.rawdatamodel.axes.CountType.PERCENTAGE_OF_ALL_EVENTS;
import static com.acuity.visualisations.rawdatamodel.axes.CountType.PERCENTAGE_OF_ALL_SUBJECTS;
import static com.acuity.visualisations.rawdatamodel.axes.CountType.PERCENTAGE_OF_EVENTS_WITHIN_PLOT;
import static com.acuity.visualisations.rawdatamodel.axes.CountType.PERCENTAGE_OF_SUBJECTS_WITHIN_PLOT;
import static com.acuity.visualisations.rawdatamodel.util.Constants.AVAILABLE_BAR_LINE_YAXIS_OPTIONS;
import static com.acuity.visualisations.rawdatamodel.util.Constants.AVAILABLE_YAXIS_OPTIONS;

@Service
public class CerebrovascularModuleMetadata extends AbstractModuleMetadata<CerebrovascularRaw, Cerebrovascular> {

    @Override
    public MetadataItem getNonMergeableMetadataItem(Datasets datasets) {
        return getMetadataItem(datasets);
    }

    @Override
    protected String tab() {
        return "cerebrovascular";
    }

    @Override
    protected MetadataItem buildMetadataItem(MetadataItem metadataItem, Datasets datasets) {
        metadataItem = super.buildMetadataItem(metadataItem, datasets);
        metadataItem.add(AVAILABLE_YAXIS_OPTIONS,
                Arrays.asList(COUNT_OF_SUBJECTS,
                        COUNT_OF_EVENTS,
                        PERCENTAGE_OF_ALL_SUBJECTS,
                        PERCENTAGE_OF_ALL_EVENTS,
                        PERCENTAGE_OF_SUBJECTS_WITHIN_PLOT,
                        PERCENTAGE_OF_EVENTS_WITHIN_PLOT));
        metadataItem.add(AVAILABLE_BAR_LINE_YAXIS_OPTIONS, Collections.singletonList(BinCountType.COUNT_START_DATES_ONLY));
        return metadataItem;
    }

    @Override
    protected MetadataItem buildErrorMetadataItem(MetadataItem metadataItem) {
        metadataItem = super.buildErrorMetadataItem(metadataItem);
        metadataItem.add(AVAILABLE_YAXIS_OPTIONS, new ArrayList<>());
        metadataItem.add(AVAILABLE_BAR_LINE_YAXIS_OPTIONS, new ArrayList<>());
        return metadataItem;
    }
}
