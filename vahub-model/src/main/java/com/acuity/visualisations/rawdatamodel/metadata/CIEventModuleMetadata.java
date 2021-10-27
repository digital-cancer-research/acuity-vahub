package com.acuity.visualisations.rawdatamodel.metadata;

import com.acuity.visualisations.common.study.metadata.MetadataItem;
import com.acuity.visualisations.rawdatamodel.axes.CountType;
import com.acuity.visualisations.rawdatamodel.vo.CIEventRaw;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.CIEvent;
import com.acuity.va.security.acl.domain.Datasets;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;

import static com.acuity.visualisations.rawdatamodel.axes.CountType.COUNT_OF_EVENTS;
import static com.acuity.visualisations.rawdatamodel.axes.CountType.COUNT_OF_SUBJECTS;
import static com.acuity.visualisations.rawdatamodel.axes.CountType.PERCENTAGE_OF_ALL_EVENTS;
import static com.acuity.visualisations.rawdatamodel.axes.CountType.PERCENTAGE_OF_ALL_SUBJECTS;
import static com.acuity.visualisations.rawdatamodel.axes.CountType.PERCENTAGE_OF_EVENTS_WITHIN_PLOT;
import static com.acuity.visualisations.rawdatamodel.axes.CountType.PERCENTAGE_OF_SUBJECTS_WITHIN_PLOT;
import static com.acuity.visualisations.rawdatamodel.util.Constants.AVAILABLE_BAR_LINE_YAXIS_OPTIONS;
import static com.acuity.visualisations.rawdatamodel.util.Constants.AVAILABLE_YAXIS_OPTIONS;
import static com.google.common.collect.Lists.newArrayList;

/**
 * Created by knml167 on 5/22/2017.
 */
@Service
public class CIEventModuleMetadata extends AbstractModuleMetadata<CIEventRaw, CIEvent> {

    @Override
    protected String tab() {
        return "cievents";
    }

    @Override
    protected MetadataItem buildMetadataItem(MetadataItem metadataItem, Datasets datasets) {
        metadataItem = super.buildMetadataItem(metadataItem, datasets);
        metadataItem.add(AVAILABLE_YAXIS_OPTIONS, Arrays.asList(COUNT_OF_SUBJECTS,
                COUNT_OF_EVENTS,
                PERCENTAGE_OF_ALL_SUBJECTS,
                PERCENTAGE_OF_ALL_EVENTS,
                PERCENTAGE_OF_SUBJECTS_WITHIN_PLOT,
                PERCENTAGE_OF_EVENTS_WITHIN_PLOT));
        metadataItem.add(AVAILABLE_BAR_LINE_YAXIS_OPTIONS, newArrayList(CountType.COUNT_OF_EVENTS));
        return metadataItem;
    }

    @Override
    protected MetadataItem buildErrorMetadataItem(MetadataItem metadataItem) {
        metadataItem = super.buildErrorMetadataItem(metadataItem);
        metadataItem.add(AVAILABLE_YAXIS_OPTIONS, new ArrayList<>());
        return metadataItem;
    }
}
