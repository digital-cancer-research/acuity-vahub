package com.acuity.visualisations.rawdatamodel.metadata;

import com.acuity.visualisations.common.study.metadata.MetadataItem;
import com.acuity.visualisations.rawdatamodel.Constants;
import com.acuity.visualisations.rawdatamodel.dataproviders.PopulationDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.service.PopulationService;
import com.acuity.visualisations.rawdatamodel.service.dod.DoDCommonService;
import com.acuity.visualisations.rawdatamodel.util.Column;
import com.acuity.visualisations.rawdatamodel.vo.Patient;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.va.security.acl.domain.Datasets;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;

import static com.google.common.collect.Lists.newArrayList;
import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollection;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PopulationModuleMetadataTest {
    @InjectMocks
    private PopulationModuleMetadata moduleMetadata;
    @Mock
    private PopulationDatasetsDataProvider datasetsDataProvider;
    @Mock
    private PopulationService populationService;
    @Mock
    private DoDCommonService doDCommonService;

    @Before
    public void setUp() {
        moduleMetadata.populationDatasetsDataProvider = datasetsDataProvider;
    }

    @Test
    public void shouldGetMetadata() {
        //Given
        when(datasetsDataProvider.loadData(any(Datasets.class))).thenReturn(newArrayList(new Subject(), new Subject()));
        final HashMap<String, String> cols = new HashMap<>();
        cols.put("column1", "title 1");
        cols.put("column2", "title 2");
        when(doDCommonService.getDoDColumns(any(Column.DatasetType.class), anyCollection())).thenReturn(cols);
        when(populationService.getPatientList(any())).thenReturn(newArrayList(new Patient("p", "p2")));
        when(populationService.hasSafetyAsNoInPopulation(any())).thenReturn(true);

        //When
        MetadataItem result = moduleMetadata.getMetadataItem(Constants.DATASETS);

        //Then
        assertThat(result.getKey()).isEqualTo("population");
        String json = result.build();
        assertThat(json).contains("\"count\": 2");
        assertThat(json).contains("\"hasData\": true");
        assertThat(json).contains("\"hasSafetyAsNoInPopulation\": true");
        assertThatJson(json).node("population.detailsOnDemandColumns").isEqualTo(newArrayList("column1", "column2"));
        assertThatJson(json).node("population.patientList").isPresent();
        assertThatJson(json).node("population.availableYAxisOptions").isEqualTo(newArrayList(
                "COUNT_OF_SUBJECTS",
                "PERCENTAGE_OF_ALL_SUBJECTS",
                "PERCENTAGE_OF_SUBJECTS_100_PERCENT_STACKED"));
    }
}
