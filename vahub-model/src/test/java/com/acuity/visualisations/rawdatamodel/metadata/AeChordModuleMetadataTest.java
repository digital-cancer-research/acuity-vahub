package com.acuity.visualisations.rawdatamodel.metadata;

import com.acuity.visualisations.common.study.metadata.MetadataItem;
import com.acuity.visualisations.rawdatamodel.service.ae.chord.AeChordDiagramService;
import com.acuity.va.security.acl.domain.Datasets;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;

import static com.acuity.visualisations.rawdatamodel.Constants.DATASETS;
import static com.acuity.visualisations.rawdatamodel.service.event.AeServiceTest.SETTINGS_WITH_MAX_TIME_FRAME;
import static com.google.common.collect.Lists.newArrayList;
import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AeChordModuleMetadataTest {
    @InjectMocks
    private AeChordModuleMetadata moduleMetadata;

    @Mock
    private AeChordDiagramService aeChordDiagramService;

    @Test
    public void shouldGetMetadata() {
        //Given
        final HashMap<String, String> cols = new HashMap<>();
        cols.put("column1", "title 1");
        cols.put("column2", "title 2");
        when(aeChordDiagramService.getDoDColumns(any(Datasets.class), eq(SETTINGS_WITH_MAX_TIME_FRAME))).thenReturn(cols);

        //When
        MetadataItem result = moduleMetadata.getMetadataItem(DATASETS);

        //Then
        assertThat(result.getKey()).isEqualTo("ae-chord");
        String json = result.build();
        assertThatJson(json).node("ae-chord.detailsOnDemandColumns").isEqualTo(newArrayList("column1", "column2"));
        assertThatJson(json).node("ae-chord.detailsOnDemandTitledColumns").isEqualTo(cols);
    }
}
