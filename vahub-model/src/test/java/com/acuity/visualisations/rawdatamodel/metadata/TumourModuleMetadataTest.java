package com.acuity.visualisations.rawdatamodel.metadata;

import com.acuity.visualisations.common.study.metadata.MetadataItem;
import com.acuity.visualisations.rawdatamodel.dataproviders.AssessedTargetLesionDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.service.dod.DoDCommonService;
import com.acuity.visualisations.rawdatamodel.vo.AssessedTargetLesionRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.AssessedTargetLesion;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_DETECT_DATASETS;
import static com.acuity.visualisations.rawdatamodel.util.Column.DatasetType;
import static com.google.common.collect.Lists.newArrayList;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollection;
import static org.mockito.Mockito.when;

public class TumourModuleMetadataTest {

    @InjectMocks
    private TumourModuleMetadata tumourModuleMetadata;
    @Mock
    private AssessedTargetLesionDatasetsDataProvider tumourDatasetsDataProvider;
    @Mock
    private DoDCommonService doDCommonService;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        tumourModuleMetadata.datasetsDataProvider = newArrayList(tumourDatasetsDataProvider);
    }

    @Test
    public void shouldGetMetadata() {
        when(tumourDatasetsDataProvider.loadDataByVisit(DUMMY_DETECT_DATASETS)).thenReturn(newArrayList(
                new AssessedTargetLesion(new AssessedTargetLesionRaw(), new Subject()),
                new AssessedTargetLesion(new AssessedTargetLesionRaw(), new Subject()),
                new AssessedTargetLesion(new AssessedTargetLesionRaw(), new Subject())));
        when(doDCommonService.getDoDColumns(any(DatasetType.class), anyCollection())).thenReturn(new HashMap());

        MetadataItem metadataItem = tumourModuleMetadata.getMetadataItem(DUMMY_DETECT_DATASETS);
        softly.assertThat(metadataItem.getKey()).isEqualTo("tumour");
        softly.assertThat(metadataItem.build()).contains("\"count\": 3");
        softly.assertThat(metadataItem.build()).contains("\"hasData\": true");
    }

    @Test
    public void shouldGetMetadataNoData() {
        when(tumourDatasetsDataProvider.loadDataByVisit(DUMMY_DETECT_DATASETS)).thenReturn(newArrayList());
        when(doDCommonService.getDoDColumns(any(DatasetType.class), anyCollection())).thenReturn(new HashMap());

        MetadataItem metadataItem = tumourModuleMetadata.getMetadataItem(DUMMY_DETECT_DATASETS);
        softly.assertThat(metadataItem.getKey()).isEqualTo("tumour");
        softly.assertThat(metadataItem.build()).contains("\"count\": 0");
        softly.assertThat(metadataItem.build()).contains("\"hasData\": false");
    }
}
