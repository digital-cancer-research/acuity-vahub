package com.acuity.visualisations.rawdatamodel.metadata;

import com.acuity.visualisations.common.study.metadata.MetadataItem;
import com.acuity.visualisations.rawdatamodel.axes.CountType;
import com.acuity.visualisations.rawdatamodel.dao.StudyInfoRepository;
import com.acuity.visualisations.rawdatamodel.vo.QtProlongationRaw;
import com.acuity.visualisations.rawdatamodel.vo.StudyInfoAdministrationDetail;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.QtProlongation;
import com.acuity.va.security.acl.domain.Datasets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.acuity.visualisations.rawdatamodel.util.Constants.AVAILABLE_YAXIS_OPTIONS;
import static com.google.common.collect.Lists.newArrayList;

@Service
public class QtProlongationModuleMetadata extends AbstractModuleMetadata<QtProlongationRaw, QtProlongation> {
    private static final String QT_PROLONGATION_INFO_KEY = "qt-prolongation";

    @Autowired
    private StudyInfoRepository studyInfoRepository;

    @Override
    protected String tab() {
        return QT_PROLONGATION_INFO_KEY;
    }

    @Override
    protected MetadataItem buildMetadataItem(MetadataItem metadataItem, Datasets datasets) {
        List<Long> datasetIdsWithAmlEnabled = studyInfoRepository.getStudyInfoByDatasetIds(datasets.getIds()).stream()
                .filter(StudyInfoAdministrationDetail::getAmlEnabled)
                .map(StudyInfoAdministrationDetail::getStudyId).collect(Collectors.toList());
        if (datasetIdsWithAmlEnabled.isEmpty()) {
            metadataItem = buildErrorMetadataItem(metadataItem);
        } else {
            Datasets ds = Datasets.toAcuityDataset(datasetIdsWithAmlEnabled);
            metadataItem = super.buildMetadataItem(metadataItem, ds);
            metadataItem.add(AVAILABLE_YAXIS_OPTIONS, newArrayList(CountType.COUNT_OF_EVENTS));
        }
        return metadataItem;
    }

    @Override
    protected MetadataItem buildErrorMetadataItem(MetadataItem metadataItem) {
        return super.buildErrorMetadataItem(metadataItem)
                    .add(AVAILABLE_YAXIS_OPTIONS, Collections.emptyList());
    }
}
