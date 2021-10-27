package com.acuity.visualisations.rawdatamodel.metadata;

import com.acuity.visualisations.common.study.metadata.MetadataItem;
import com.acuity.visualisations.rawdatamodel.vo.ExacerbationRaw;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Exacerbation;
import com.acuity.va.security.acl.domain.Datasets;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;

import static com.acuity.visualisations.rawdatamodel.axes.BinCountType.COUNT_INCLUDING_DURATION;
import static com.acuity.visualisations.rawdatamodel.axes.BinCountType.COUNT_START_DATES_ONLY;
import static com.acuity.visualisations.rawdatamodel.axes.CountType.COUNT_OF_EVENTS;
import static com.acuity.visualisations.rawdatamodel.axes.CountType.COUNT_OF_SUBJECTS;
import static com.acuity.visualisations.rawdatamodel.axes.CountType.CUMULATIVE_COUNT_OF_EVENTS;
import static com.acuity.visualisations.rawdatamodel.axes.CountType.CUMULATIVE_COUNT_OF_SUBJECTS;
import static com.acuity.visualisations.rawdatamodel.axes.CountType.PERCENTAGE_OF_ALL_EVENTS;
import static com.acuity.visualisations.rawdatamodel.axes.CountType.PERCENTAGE_OF_ALL_SUBJECTS;
import static com.acuity.visualisations.rawdatamodel.axes.CountType.PERCENTAGE_OF_EVENTS_WITHIN_PLOT;
import static com.acuity.visualisations.rawdatamodel.axes.CountType.PERCENTAGE_OF_SUBJECTS_WITHIN_PLOT;

@Service
public class ExacerbationModuleMetadata extends AbstractModuleMetadata<ExacerbationRaw, Exacerbation> {

    @Override
    protected String tab() {
        return "exacerbation";
    }

    @Override
    protected MetadataItem buildMetadataItem(MetadataItem metadataItem, Datasets datasets) {
        metadataItem = super.buildMetadataItem(metadataItem, datasets);
        metadataItem.add("availableYAxisOptionsForGroupedBarChart", Arrays.asList(
                COUNT_OF_SUBJECTS,
                COUNT_OF_EVENTS,
                PERCENTAGE_OF_ALL_SUBJECTS,
                PERCENTAGE_OF_ALL_EVENTS,
                PERCENTAGE_OF_SUBJECTS_WITHIN_PLOT,
                PERCENTAGE_OF_EVENTS_WITHIN_PLOT));
        metadataItem.add("availableOverTimeChartYAxisOptions", Arrays.asList(COUNT_INCLUDING_DURATION,
                COUNT_START_DATES_ONLY));

        metadataItem.add("availableYAxisOptionsForLineChart", Arrays.asList(
                COUNT_OF_EVENTS,
                CUMULATIVE_COUNT_OF_EVENTS,
                COUNT_OF_SUBJECTS,
                CUMULATIVE_COUNT_OF_SUBJECTS
        ));

        return metadataItem;
    }

    @Override
    protected MetadataItem buildErrorMetadataItem(MetadataItem metadataItem) {
        metadataItem = super.buildErrorMetadataItem(metadataItem);
        metadataItem.add("availableYAxisOptionsForGroupedBarChart", Collections.emptyList());
        metadataItem.add("availableOverTimeChartYAxisOptions", Collections.emptyList());
        metadataItem.add("availableYAxisOptionsForLineChart", Collections.emptyList());
        return metadataItem;
    }
}
