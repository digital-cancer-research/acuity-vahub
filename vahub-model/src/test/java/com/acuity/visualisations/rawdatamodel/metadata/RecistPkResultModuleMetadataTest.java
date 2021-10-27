package com.acuity.visualisations.rawdatamodel.metadata;

import com.acuity.visualisations.common.study.metadata.MetadataItem;
import com.acuity.visualisations.rawdatamodel.service.event.PkResultWithResponseService;
import com.acuity.va.security.acl.domain.Datasets;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.acuity.visualisations.rawdatamodel.Constants.DATASETS;
import static com.google.common.collect.Lists.newArrayList;
import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class RecistPkResultModuleMetadataTest {

    @InjectMocks
    private RecistPkResultWithResponseModuleMetadata recistPkResultWithResponseModuleMetadata;
    @Mock
    private PkResultWithResponseService pkResultWithResponseService;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldGetMetadata() {
        final Map<String, String> columns = new LinkedHashMap<>();
        columns.put("newLesions", "New lesions");
        columns.put("ntlResponse", "NTL response");
        columns.put("bestOverallResponse", "Best overall response");
        when(pkResultWithResponseService.getRecistDoDColumns(any(Datasets.class))).thenReturn(columns);

        MetadataItem result = recistPkResultWithResponseModuleMetadata.getMetadataItem(DATASETS);

        assertThat(result.getKey()).isEqualTo("recist-pk");
        String json = result.build();
        assertThatJson(json).node("recist-pk.detailsOnDemandColumns").isEqualTo(newArrayList("newLesions", "ntlResponse", "bestOverallResponse"));
        assertThatJson(json).node("recist-pk.detailsOnDemandTitledColumns").isEqualTo(columns);
    }


}
